package se.tmconnect.styrelserummet.underlag

data class TolkningsForslag(
    val komponenter: List<RadForslag>,
    val anmarkningar: List<String>,
)

data class RadForslag(
    val namn: String,
    val grupp: String,
    val anskaffningsvarde: Long?,
    val anskaffningsar: Int?,
    val nyttjandeperiodAr: Int?,
    val kalla: String,
    val sakerhet: String,
)

interface Tolkare {
    fun foreslaKomponenter(underhallsplanText: String, filnamn: String): Result<TolkningsForslag>
}

class TolkningMisslyckades(val orsak: String) : Exception(orsak)
