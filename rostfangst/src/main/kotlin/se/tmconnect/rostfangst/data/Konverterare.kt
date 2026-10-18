package se.tmconnect.rostfangst.data

import androidx.room.TypeConverter
import se.tmconnect.rostfangst.regler.ForslagTyp

class Konverterare {
    @TypeConverter
    fun franTyp(value: String?): ForslagTyp? = value?.let { ForslagTyp.valueOf(it) }

    @TypeConverter
    fun tillTyp(typ: ForslagTyp?): String? = typ?.name
}
