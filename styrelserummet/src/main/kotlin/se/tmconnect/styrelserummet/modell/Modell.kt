package se.tmconnect.styrelserummet.modell

import kotlinx.serialization.Serializable

data class Forening(
    val namn: String,
    val byggnadsar: Int? = null,
    val rakenskapsarStartManad: Int = 1,
)

enum class Komponentgrupp(val svensktNamn: String) {
    STOMME("Stomme"),
    FASAD("Fasad"),
    TAK("Tak"),
    FONSTER_DORRAR("Fönster och dörrar"),
    STAMMAR("Stammar"),
    EL("El"),
    VARME("Värme"),
    VENTILATION("Ventilation"),
    HISS("Hiss"),
    TVATTSTUGA("Tvättstuga"),
    MARK_OCH_GARAGE("Mark och garage"),
    OVRIGT("Övrigt"),
    ;

    companion object {
        fun franNamn(namn: String): Komponentgrupp? =
            entries.firstOrNull { it.name.equals(namn, ignoreCase = true) }
    }
}

enum class Sakerhet {
    HOG,
    MEDEL,
    LAG,
    UPPSKATTNING,
}

@Serializable
data class Komponent(
    val namn: String,
    val grupp: String,
    val anskaffningsvarde: Long,
    val anskaffningsar: Int,
    val nyttjandeperiodAr: Int,
    val restvarde: Long = 0,
    val kalla: String,
    val sakerhet: String,
) {
    fun gruppEnum(): Komponentgrupp = Komponentgrupp.franNamn(grupp) ?: Komponentgrupp.OVRIGT

    fun sakerhetEnum(): Sakerhet =
        runCatching { Sakerhet.valueOf(sakerhet) }.getOrDefault(Sakerhet.LAG)
}

data class Avstamning(
    val summa: Long,
    val byggnadensVarde: Long,
    val differens: Long,
    val stammer: Boolean,
)

@Serializable
data class Utkast(
    val forening: String,
    val arsslut: Int,
    val byggnadensVarde: Long,
    val komponenter: List<Komponent>,
    val avstamning: AvstamningDto,
    val anmarkningar: List<String>,
)

@Serializable
data class AvstamningDto(
    val summa: Long,
    val byggnadensVarde: Long,
    val differens: Long,
    val stammer: Boolean,
)

data class GruppSumma(
    val grupp: Komponentgrupp,
    val anskaffning: Long,
    val avskrivningAret: Long,
    val bokfort: Long,
)

data class Fel(val kod: String, val meddelande: String)
