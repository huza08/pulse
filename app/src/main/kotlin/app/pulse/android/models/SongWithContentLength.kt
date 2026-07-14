package app.pulse.android.models

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import app.pulse.core.data.models.SongEntity

@Immutable
data class SongWithContentLength(
    @Embedded val song: SongEntity,
    val contentLength: Long?
)
