package se.tmconnect.husloggen.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "planerad_kostnad",
    foreignKeys = [
        ForeignKey(
            entity = Bostad::class,
            parentColumns = ["id"],
            childColumns = ["bostad_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("bostad_id"), Index("planerat_ar")],
)
data class PlaneradKostnad(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "bostad_id")
    val bostadId: Long = 1L,
    val titel: String,
    @ColumnInfo(name = "belopp_ore")
    val beloppOre: Long,
    @ColumnInfo(name = "planerat_ar")
    val planeratAr: Int,
    val anteckning: String? = null,
)
