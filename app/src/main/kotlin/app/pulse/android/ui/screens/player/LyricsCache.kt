package app.pulse.android.ui.screens.player

import app.pulse.android.models.Lyrics

object LyricsCache {
    private const val MAX_SIZE = 50
    private val cache = object : LinkedHashMap<String, Lyrics>(MAX_SIZE, 0.75f, true) {
        override fun removeEldestEntry(
            eldest: MutableMap.MutableEntry<String, Lyrics>
        ): Boolean = size > MAX_SIZE
    }

    @Synchronized
    operator fun get(mediaId: String): Lyrics? = cache[mediaId]

    @Synchronized
    operator fun set(mediaId: String, lyrics: Lyrics) {
        cache[mediaId] = lyrics
    }
}
