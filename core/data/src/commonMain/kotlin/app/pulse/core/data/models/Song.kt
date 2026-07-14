package app.pulse.core.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Song(
    val id: String,
    val title: String,
    val artistsText: String? = null,
    val durationText: String? = null,
    val thumbnailUrl: String? = null,
    val likedAt: Long? = null,
    val totalPlayTimeMs: Long = 0,
    val blacklisted: Boolean = false,
    val explicit: Boolean = false
)
