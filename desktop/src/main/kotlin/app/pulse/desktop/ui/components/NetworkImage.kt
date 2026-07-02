package app.pulse.desktop.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL
import java.security.MessageDigest

private val imageCacheDir = File(System.getProperty("java.io.tmpdir"), "pulse-image-cache")

private const val IMAGE_CACHE_MAX_AGE_MS = 24L * 60 * 60 * 1000   // 24h
private const val IMAGE_CACHE_MAX_BYTES = 50L * 1024 * 1024         // 50 MB

/** evict stale or oversized image cache entries. runs on first class load. */
private fun cleanImageCache() {
    val dir = imageCacheDir
    if (!dir.isDirectory) return
    val now = System.currentTimeMillis()
    val files = dir.listFiles()?.filter { it.isFile }?.toMutableList() ?: return

    val kept = mutableListOf<File>()
    for (f in files) {
        if (now - f.lastModified() > IMAGE_CACHE_MAX_AGE_MS) f.delete()
        else kept.add(f)
    }

    var total = kept.sumOf { it.length() }
    if (total <= IMAGE_CACHE_MAX_BYTES) return
    kept.sortBy { it.lastModified() }
    for (f in kept) {
        if (total <= IMAGE_CACHE_MAX_BYTES) break
        total -= f.length()
        f.delete()
    }
}

private val _imageCacheInit = runCatching { cleanImageCache() }

/** load image from disk cache or download and cache it. */
@Composable
fun NetworkImage(url: String, modifier: Modifier = Modifier) {
    var bitmap by remember(url) { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(url) {
        bitmap = withContext(Dispatchers.IO) {
            try {
                val cacheFile = cacheFileFor(url)
                if (cacheFile.exists()) {
                    cacheFile.inputStream().buffered().use { loadImageBitmap(it) }
                } else {
                    imageCacheDir.mkdirs()
                    val bytes = URL(url).openStream().buffered().use { it.readBytes() }
                    cacheFile.outputStream().use { it.write(bytes) }
                    loadImageBitmap(bytes.inputStream().buffered())
                }
            } catch (_: Exception) { null }
        }
    }
    bitmap?.let {
        Image(
            painter = BitmapPainter(it),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    }
}

private fun cacheFileFor(url: String): File {
    val hash = MessageDigest.getInstance("MD5").digest(url.toByteArray())
        .joinToString("") { "%02x".format(it) }
    return File(imageCacheDir, hash)
}
