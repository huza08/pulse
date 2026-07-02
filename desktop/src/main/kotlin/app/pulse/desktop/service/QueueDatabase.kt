package app.pulse.desktop.service

import app.pulse.providers.innertube.Innertube
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.io.File
import java.sql.Connection
import java.sql.DriverManager

/**
 * Persists and restores the playback queue + position using local SQLite.
 * Mirrors Android's maybeSavePlayerQueue / maybeRestorePlayerQueue pattern.
 */
object QueueDatabase {
    val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        encodeDefaults = true
        classDiscriminator = "#class"
    }

    private const val DB_NAME = "pulse_queue.db"
    private val dbDir: File by lazy {
        val userHome = System.getProperty("user.home")
        File(userHome, ".pulse").also { it.mkdirs() }
    }

    private var connection: Connection? = null

    /** Schema version for future migrations. */
    private const val SCHEMA_VERSION = 1

    /** Save data: queue items, current index, playback position, loop mode, volume. */
    data class SavedQueue(
        val queue: List<SongEntry>,
        val currentIndex: Int,
        val positionMs: Long,
        val durationMs: Long,
        val loopMode: LoopMode,
        val volume: Float
    )

    data class SongEntry(
        val videoId: String,
        val songJson: String   // JSON-serialized Innertube.SongItem
    )

    /** Ensure DB + table exist. Called once at app start. */
    fun init() {
        Class.forName("org.sqlite.JDBC")
        val dbFile = File(dbDir, DB_NAME)
        val conn = DriverManager.getConnection("jdbc:sqlite:${dbFile.absolutePath}")
        connection = conn

        conn.createStatement().execute(
            """
            CREATE TABLE IF NOT EXISTS player_queue (
                id          INTEGER PRIMARY KEY AUTOINCREMENT,
                video_id    TEXT    NOT NULL UNIQUE,
                song_json   TEXT    NOT NULL,
                queue_index INTEGER NOT NULL,
                is_current  INTEGER NOT NULL DEFAULT 0,
                position_ms INTEGER NOT NULL DEFAULT 0
            )
            """.trimIndent()
        )

        conn.createStatement().execute(
            """
            CREATE TABLE IF NOT EXISTS player_meta (
                key   TEXT PRIMARY KEY,
                value TEXT NOT NULL
            )
            """.trimIndent()
        )

        // Ensure schema version is set
        val rs = conn.createStatement().executeQuery("SELECT value FROM player_meta WHERE key = 'schema_version'")
        if (!rs.next()) {
            conn.createStatement().execute("INSERT INTO player_meta (key, value) VALUES ('schema_version', '$SCHEMA_VERSION')")
        }
        rs.close()
    }

    /** Save current queue state. Thread-safe. */
    fun save(state: PlaybackState) = synchronized(this) {
        val conn = connection ?: return

        // clear old data
        conn.createStatement().execute("DELETE FROM player_queue")
        conn.createStatement().execute("DELETE FROM player_meta WHERE key != 'schema_version'")

        val queue = state.queue
        if (queue.isEmpty()) return

        val stmt = conn.prepareStatement(
            "INSERT INTO player_queue (video_id, song_json, queue_index, is_current, position_ms) VALUES (?, ?, ?, ?, ?)"
        )

        val currentVideoId = queue.getOrNull(state.currentIndex)
            ?.info?.endpoint?.videoId

        queue.forEachIndexed { index, song ->
            val videoId = song.info?.endpoint?.videoId ?: return@forEachIndexed
            val songStr = json.encodeToString(song)
            val isCurrent = if (videoId == currentVideoId) 1 else 0
            val pos = if (isCurrent == 1) state.currentPositionMs else 0L

            stmt.setString(1, videoId)
            stmt.setString(2, songStr)
            stmt.setInt(3, index)
            stmt.setInt(4, isCurrent)
            stmt.setLong(5, pos)
            stmt.addBatch()
        }
        stmt.executeBatch()
        stmt.close()

        // save meta: currentIndex, loopMode, volume
        val metaStmt = conn.prepareStatement("INSERT OR REPLACE INTO player_meta (key, value) VALUES (?, ?)")
        metaStmt.setString(1, "current_index")
        metaStmt.setString(2, state.currentIndex.toString())
        metaStmt.addBatch()

        metaStmt.setString(1, "loop_mode")
        metaStmt.setString(2, state.loopMode.name)
        metaStmt.addBatch()

        metaStmt.setString(1, "volume")
        metaStmt.setString(2, state.volume.toString())
        metaStmt.addBatch()

        metaStmt.setString(1, "duration_ms")
        metaStmt.setString(2, state.durationMs.toString())
        metaStmt.addBatch()

        metaStmt.executeBatch()
        metaStmt.close()
    }

    /** Restore saved queue state. Thread-safe. Returns null if nothing saved. */
    fun restore(): SavedQueue? = synchronized(this) {
        val conn = connection ?: return null

        val queueRs = conn.createStatement().executeQuery(
            "SELECT video_id, song_json, queue_index, is_current, position_ms FROM player_queue ORDER BY queue_index ASC"
        )

        val entries = mutableListOf<SongEntry>()
        var currentPositionMs = 0L

        while (queueRs.next()) {
            val videoId = queueRs.getString("video_id")
            val songJson = queueRs.getString("song_json")
            val pos = queueRs.getLong("position_ms")
            if (queueRs.getInt("is_current") == 1) {
                currentPositionMs = pos
            }
            entries.add(SongEntry(videoId = videoId, songJson = songJson))
        }
        queueRs.close()

        if (entries.isEmpty()) return null

        // restore meta
        val metaMap = mutableMapOf<String, String>()
        val metaRs = conn.createStatement().executeQuery("SELECT key, value FROM player_meta")
        while (metaRs.next()) {
            metaMap[metaRs.getString("key")] = metaRs.getString("value")
        }
        metaRs.close()

        val currentIndex = metaMap["current_index"]?.toIntOrNull() ?: 0
        val loopMode = metaMap["loop_mode"]?.let { runCatching { LoopMode.valueOf(it) }.getOrNull() } ?: LoopMode.NONE
        val volume = metaMap["volume"]?.toFloatOrNull() ?: 1f
        val durationMs = metaMap["duration_ms"]?.toLongOrNull() ?: 0L

        return SavedQueue(
            queue = entries,
            currentIndex = currentIndex.coerceIn(0, entries.lastIndex),
            positionMs = currentPositionMs,
            durationMs = durationMs,
            loopMode = loopMode,
            volume = volume
        )
    }

    /** clear saved queue (e.g. after successful restore). Thread-safe. */
    fun clear() {
        synchronized(this) {
            val conn = connection ?: return@synchronized
            conn.createStatement().execute("DELETE FROM player_queue")
            conn.createStatement().execute("DELETE FROM player_meta WHERE key != 'schema_version'")
        }
    }

    /** Close DB connection. */
    fun close() {
        synchronized(this) {
            connection?.close()
            connection = null
        }
    }
}
