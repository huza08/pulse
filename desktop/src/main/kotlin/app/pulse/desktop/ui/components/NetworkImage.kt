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
private const val MAX_THUMB_SIZE = 9999

/**
 * Request highest available resolution for Google-hosted thumbnails.
 * Google's CDN caps at the original upload resolution.
 */
private fun highResUrl(original: String): String = when {
    original.startsWith("https://lh3.googleusercontent.com") ->
        "${original.substringBeforeLast('=')}=w$MAX_THUMB_SIZE-h$MAX_THUMB_SIZE-p-rj-nu"
    original.startsWith("https://yt3.ggpht.com") ->
        "${original.substringBeforeLast('=')}=s$MAX_THUMB_SIZE-p-rj-nu"
    else -> original
}

/** load image from disk cache or download and cache it. */
@Composable
fun NetworkImage(url: String, modifier: Modifier = Modifier) {
    val hiRes = remember(url) { highResUrl(url) }
    var bitmap by remember(hiRes) { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(hiRes) {
        bitmap = withContext(Dispatchers.IO) {
            try {
                val cacheFile = cacheFileFor(hiRes)
                if (cacheFile.exists()) {
                    cacheFile.inputStream().buffered().use { loadImageBitmap(it) }
                } else {
                    imageCacheDir.mkdirs()
                    val bytes = URL(hiRes).openStream().buffered().use { it.readBytes() }
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
    val hash = url.hashCode().toUInt().toString(16)
    return File(imageCacheDir, hash)
}
