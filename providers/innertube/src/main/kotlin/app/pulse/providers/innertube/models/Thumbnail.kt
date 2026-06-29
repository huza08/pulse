package app.pulse.providers.innertube.models

import kotlinx.serialization.Serializable

@Serializable
data class Thumbnail(
    val url: String,
    val height: Int?,
    val width: Int?
) {
    fun size(size: Int) = when {
        url.startsWith("https://lh3.googleusercontent.com") -> "${url.substringBeforeLast('=')}=w$size-h$size-p-rj-nu"
        url.startsWith("https://yt3.ggpht.com") -> "${url.substringBeforeLast('=')}=s$size-p-rj-nu"
        else -> url
    }
}
