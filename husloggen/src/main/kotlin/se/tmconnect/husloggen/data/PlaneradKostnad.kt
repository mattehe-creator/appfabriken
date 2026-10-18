package se.tmconnect.husloggen.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "planerad_kostnad",
    indices = [Index("planerat_ar")],
)
data class PlaneradKostnad(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titel: String,
    @ColumnInfo(name = "belopp_ore")
    val beloppOre: Long,
    @ColumnInfo(name = "planerat_ar")
    val planeratAr: Int,
    val anteckning: String? = null,
)
