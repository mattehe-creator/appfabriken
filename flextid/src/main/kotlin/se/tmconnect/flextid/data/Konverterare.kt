package se.tmconnect.flextid.data

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime
import se.tmconnect.flextid.regler.DagTyp
import se.tmconnect.flextid.regler.localTimeTillMinuter
import se.tmconnect.flextid.regler.minuterTillLocalTime

class Konverterare {
    @TypeConverter
    fun franEpokdag(value: Long?): LocalDate? = value?.let { LocalDate.ofEpochDay(it) }

    @TypeConverter
    fun tillEpokdag(datum: LocalDate?): Long? = datum?.toEpochDay()

    @TypeConverter
    fun franMinuter(value: Int?): LocalTime? = value?.let { minuterTillLocalTime(it) }

    @TypeConverter
    fun tillMinuter(tid: LocalTime?): Int? = tid?.let { localTimeTillMinuter(it) }

    @TypeConverter
    fun franDagTyp(value: String?): DagTyp? = value?.let { DagTyp.valueOf(it) }

    @TypeConverter
    fun tillDagTyp(typ: DagTyp?): String? = typ?.name
}
