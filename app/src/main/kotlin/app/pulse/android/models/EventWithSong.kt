package app.pulse.android.models

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import androidx.room.Relation
import app.pulse.core.data.models.SongEntity

@Immutable
data class EventWithSong(
    @Embedded val event: Event,
    @Relation(
        entity = SongEntity::class,
        parentColumn = "songId",
        entityColumn = "id"
    )
    val song: SongEntity
)
