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
    val hash = url.hashCode().toUInt().toString(16)
    return File(imageCacheDir, hash)
}
