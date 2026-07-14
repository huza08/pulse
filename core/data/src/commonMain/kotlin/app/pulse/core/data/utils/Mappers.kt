package app.pulse.core.data.utils

import app.pulse.core.data.models.Song
import app.pulse.providers.innertube.Innertube

fun Innertube.SongItem.toSong() = Song(
    id = key,
    title = info?.name ?: "Untitled",
    artistsText = authors?.joinToString("") { it.name.orEmpty() },
    durationText = durationText,
    thumbnailUrl = thumbnail?.url
)
