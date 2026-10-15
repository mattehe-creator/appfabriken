package se.tmconnect.husloggen.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "uppgift")
data class Uppgift(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
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
