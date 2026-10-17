package se.tmconnect.husloggen.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "avlasning",
    foreignKeys = [
        ForeignKey(
            entity = Matare::class,
            parentColumns = ["id"],
            childColumns = ["matare_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("matare_id"),
        Index(value = ["matare_id", "datum"], unique = true),
    ],
)
data class Avlasning(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "matare_id")
    val matareId: Long,
    val datum: LocalDate,
    val varde: Long,
    val skapad: Long = System.currentTimeMillis(),
)
