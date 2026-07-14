package app.pulse.core.data.models

data class Playlist(
    val id: Long,
    val name: String,
    val thumbnailUrl: String? = null
)
