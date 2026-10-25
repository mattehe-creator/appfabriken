package se.tmconnect.rostfangst.wear

import se.tmconnect.rostfangst.regler.ForslagTyp
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

    val startZonedDateTime = start?.atZone(zon)
    val datumOchTid = if (startZonedDateTime != null) {
        val startDatum = startZonedDateTime.toLocalDate()
        val startTid = startZonedDateTime.toLocalTime()

        when {
            startDatum.isAfter(idag) -> {
                val formatter = DateTimeFormatter.ofPattern("d/M HH:mm")
                "${startDatum.format(formatter)} ${startTid.format(DateTimeFormatter.ofPattern("HH:mm"))}"
            }
            startDatum.isBefore(idag) -> {
                val formatter = DateTimeFormatter.ofPattern("d/M HH:mm")
                "${startDatum.format(formatter)} ${startTid.format(DateTimeFormatter.ofPattern("HH:mm"))}"
            }
            else -> {
                // Samma dag som idag
                startTid.format(DateTimeFormatter.ofPattern("HH:mm"))
            }
        }
    } else {
        ""
    }

    val prefixOchTitel = if (datumOchTid.isNotEmpty()) {
        "$typPrefix: $titel, $datumOchTid"
    } else {
        "$typPrefix: $titel"
    }

    // Om längden är mer än 40 tecken, kör kortning
    if (prefixOchTitel.length <= 40) {
        return prefixOchTitel
    }

    val maxTitelLängd = 40 - (typPrefix.length + 2 + 2) // +2 för ": " och ", " + 2 för ellips
    val kortadTitel = if (maxTitelLängd > 0) {
        titel.take(maxTitelLängd - 1) + "…"
    } else {
        ""
    }

    return if (datumOchTid.isNotEmpty()) {
        "$typPrefix: $kortadTitel, $datumOchTid"
    } else {
        "$typPrefix: $kortadTitel"
    }
}
