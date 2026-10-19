package se.tmconnect.styrelserummet.modell

/**
 * En byggnadskomponent enligt K3-utkastet.
 *
 * @param anskaffningsvarde Hela kronor.
 * @param anskaffningsar Första avskrivningsår (kalenderår).
 * @param nyttjandeperiodAr Total nyttjandeperiod i hela år.
 * @param restvarde Hela kronor; standard 0 om inget anges.
 * @param kalla Kort text om var uppgiften kommer ifrån (handling, rad i plan).
 */
data class Komponent(
    val namn: String,
    val grupp: Komponentgrupp,
    val anskaffningsvarde: Long,
    val anskaffningsar: Int,
    val nyttjandeperiodAr: Int,
    val restvarde: Long = 0,
    val kalla: String,
    val sakerhet: Sakerhet,
)
