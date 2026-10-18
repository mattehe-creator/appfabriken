package se.tmconnect.husloggen.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "uppgift",
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
data class Uppgift(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "bostad_id")
    val bostadId: Long = 1L,
    val namn: String,
    @ColumnInfo(name = "intervall_manader")
    val intervallManader: Int,
    @ColumnInfo(name = "start_datum")
    val startDatum: LocalDate,
    @ColumnInfo(name = "senast_gjord")
    val senastGjord: LocalDate? = null,
    val paminnelse: Boolean = true,
    val anteckning: String? = null,
    val skapad: Long = System.currentTimeMillis(),
)
