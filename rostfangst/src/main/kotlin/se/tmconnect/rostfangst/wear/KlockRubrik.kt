package se.tmconnect.rostfangst.wear

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private const val MAX_TECKEN = 40

fun klockRubrik(
    typ: String,
    titel: String,
    start: Instant?,
    zon: ZoneId,
    idag: LocalDate = LocalDate.now(zon),
): String {
    val prefix = when (typ) {
        "KALENDER" -> "Kalender"
        "PAMINNELSE" -> "Påminnelse"
        "ATT_GORA" -> "Att göra"
        "ANTECKNING" -> "Anteckning"
        "FRAGA" -> "Fråga"
        "HEM" -> "Hem"
        else -> "Förslag"
    }

    val tidsdel = if (start != null) {
        val lokal = start.atZone(zon)
        val klockslag = lokal.format(DateTimeFormatter.ofPattern("HH:mm"))
        val datum = lokal.toLocalDate()
        when {
            datum == idag -> klockslag
            datum == idag.plusDays(1) -> "i morgon $klockslag"
            else -> "${datum.dayOfMonth}/${datum.monthValue} $klockslag"
        }
    } else {
        null
    }

    val huvud = "$prefix: "
    val svans = if (tidsdel != null) ", $tidsdel" else ""
    val hel = huvud + titel + svans
    if (hel.length <= MAX_TECKEN) return hel

    val platsForTitel = MAX_TECKEN - huvud.length - svans.length
    if (platsForTitel <= 0) return hel.take(MAX_TECKEN)
    return huvud + titel.take(platsForTitel - 1) + "…" + svans
}
