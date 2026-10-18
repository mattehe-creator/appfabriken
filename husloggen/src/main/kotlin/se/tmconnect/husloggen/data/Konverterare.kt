package se.tmconnect.husloggen.data

import androidx.room.TypeConverter
import java.time.LocalDate

class Konverterare {
    @TypeConverter
    fun franEpokdag(value: Long?): LocalDate? = value?.let { LocalDate.ofEpochDay(it) }

    @TypeConverter
    fun tillEpokdag(datum: LocalDate?): Long? = datum?.toEpochDay()

    @TypeConverter
    fun franPostTyp(value: String?): PostTyp? = value?.let { PostTyp.valueOf(it) }

    @TypeConverter
    fun tillPostTyp(typ: PostTyp?): String? = typ?.name

    @TypeConverter
    fun franDokumentKategori(value: String?): DokumentKategori? =
        value?.let { DokumentKategori.valueOf(it) }

    @TypeConverter
    fun tillDokumentKategori(kategori: DokumentKategori?): String? = kategori?.name
}
