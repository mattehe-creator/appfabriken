package se.tmconnect.rostfangst.wear

import java.time.*
import java.time.format.DateTimeFormatter

fun klockRubrik(typ: String, titel: String, start: Instant?, zon: ZoneId, idag: LocalDate = LocalDate.now(zon)): String {
    val typPrefix = when (typ) {
        "KALENDER" -> "Kalender"
        "PAMINNELSE" -> "Påminnelse"
        "ATT_GORA" -> "Att göra"
        "ANTECKNING" -> "Anteckning"
        "FRAGA" -> "Fråga"
        "HEM" -> "Hem"
        else -> "Förslag"
    }

    val titelDel = if (start != null) {
        val startTid = start.atZone(zon)
        val datum = startTid.toLocalDate()
        val tid = startTid.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))

        when {
            datum.isEqual(idag.plusDays(1)) -> "i morgon $tid"
            datum.isEqual(idag) -> tid
            else -> "${datum.dayOfMonth}/${datum.monthValue} $tid"
        }
    } else {
        ""
    }

    val prefixTitel = if (titelDel.isNotEmpty()) {
        "$typPrefix: $titel, $titelDel"
    } else {
        "$typPrefix: $titel"
    }

    if (prefixTitel.length <= 40) {
        return prefixTitel
    }

    // Korta titeln så att hela strängen blir 40 tecken
    val maxTitelLängd = 40 - (typPrefix.length + 2 + 1 + titelDel.length) // +2 för ": ", +1 för ","

    if (maxTitelLängd <= 0) {
        return prefixTitel.substring(0, 40)
    }

    val kortTitel = if (titel.length > maxTitelLängd) {
        titel.substring(0, maxTitelLängd - 1) + "…"
    } else {
        titel
    }

    val nyPrefixTitel = if (titelDel.isNotEmpty()) {
        "$typPrefix: $kortTitel, $titelDel"
    } else {
        "$typPrefix: $kortTitel"
    }

    return nyPrefixTitel
}
