package app.pulse.android.models

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import app.pulse.core.data.models.SongEntity

@Immutable
@Entity(
    primaryKeys = ["songId", "artistId"],
    foreignKeys = [
        ForeignKey(
            entity = SongEntity::class,
            parentColumns = ["id"],
            childColumns = ["songId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Artist::class,
            parentColumns = ["id"],
            childColumns = ["artistId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SongArtistMap(
    @ColumnInfo(index = true) val songId: String,
    @ColumnInfo(index = true) val artistId: String
)
