package se.tmconnect.garantivalvet.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "kop")
data class Kop(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vad: String,
    @ColumnInfo(name = "var_kopt")
    val varKopt: String? = null,
    val kopdatum: LocalDate,
    @ColumnInfo(name = "garanti_manader")
    val garantiManader: Int,
    @ColumnInfo(name = "pris_ore")
    val prisOre: Long? = null,
    val anteckning: String? = null,
    @ColumnInfo(name = "kvitto_fil")
    val kvittoFil: String? = null,
    val skapad: Long = System.currentTimeMillis(),
)
