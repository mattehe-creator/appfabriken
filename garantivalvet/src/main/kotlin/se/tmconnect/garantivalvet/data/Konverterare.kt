package se.tmconnect.garantivalvet.data

import androidx.room.TypeConverter
import java.time.LocalDate

class Konverterare {
    @TypeConverter
    fun franEpokdag(value: Long?): LocalDate? = value?.let { LocalDate.ofEpochDay(it) }

    @TypeConverter
    fun tillEpokdag(datum: LocalDate?): Long? = datum?.toEpochDay()
}
