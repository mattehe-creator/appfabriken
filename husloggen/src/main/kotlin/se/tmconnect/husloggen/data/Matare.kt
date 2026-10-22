package se.tmconnect.husloggen.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "matare",
    foreignKeys = [
        ForeignKey(
            entity = Bostad::class,
            parentColumns = ["id"],
            childColumns = ["bostad_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("bostad_id")],
)
data class Matare(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "bostad_id")
    val bostadId: Long = 1L,
    val namn: String,
    val enhet: String,
    val anteckning: String? = null,
    @ColumnInfo(name = "pris_ore_per_enhet")
    val prisOrePerEnhet: Long? = null,
    val skapad: Long = System.currentTimeMillis(),
)
