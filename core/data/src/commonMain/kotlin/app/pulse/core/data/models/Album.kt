package app.pulse.core.data.models

data class Album(
    val id: String,
    val title: String? = null,
    val thumbnailUrl: String? = null,
    val authorsText: String? = null,
    val year: Int? = null
)
