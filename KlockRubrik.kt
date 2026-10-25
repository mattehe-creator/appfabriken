package se.tmconnect.rostfangst.wear

import se.tmconnect.rostfangst.regler.ForslagTyp
import java.time.*
import java.time.format.DateTimeFormatter

fun klockRubrik(typ: String, titel: String, start: Instant?, zon: ZoneId, idag: LocalDate = LocalDate.now(zon)): String {
    // Typmappning
    val typPrefix = when (typ) {
        "KALENDER" -> "Kalender"
        "PAMINNELSE" -> "Påminnelse"
        "ATT_GORA" -> "Att göra"
        "ANTECKNING" -> "Anteckning"
        "FRAGA" -> "Fråga"
        "HEM" -> "Hem"
        else -> "Förslag"
    }
    
    // Om ingen starttid, returnera bara typ och titel
    if (start == null) {
        val rubrik = "$typPrefix: $titel"
        return if (rubrik.length <= 40) {
            rubrik
        } else {
            // Korta titeln så att hela strängen blir 40 tecken
            val titelMaxLängd = 40 - typPrefix.length - 2 // -2 för ": "
            if (titelMaxLängd <= 0) {
                ""
            } else {
                val kortTitel = if (titel.length > titelMaxLängd) {
                    titel.substring(0, titelMaxLängd - 1) + "…"
                } else {
                    titel
                }
                "$typPrefix: $kortTitel"
            }
        }
    }
    
    // Beräkna starttid i lokal tid
    val startLokal = start.atZone(zon)
    val startDatum = startLokal.toLocalDate()
    val startTid = startLokal.toLocalTime()
    
    // Formatera tiden baserat på datum
    val tidStr = when {
        startDatum.isAfter(idag) -> {
            if (startDatum.isEqual(idag.plusDays(1))) {
                "i morgon ${startTid.format(DateTimeFormatter.ofPattern("HH:mm"))}"
            } else {
                "${startDatum.dayOfMonth}/${startDatum.monthValue} ${startTid.format(DateTimeFormatter.ofPattern("HH:mm"))}"
            }
        }
        startDatum.isEqual(idag) -> {
            startTid.format(DateTimeFormatter.ofPattern("HH:mm"))
        }
        else -> {
            // Om startdatum är före idag, använd datumformat
            "${startDatum.dayOfMonth}/${startDatum.monthValue} ${startTid.format(DateTimeFormatter.ofPattern("HH:mm"))}"
        }
    }
    
    // Skapa basrubrik
    val basRubrik = "$typPrefix: $titel, $tidStr"
    
    // Om basrubriken är för lång, kör avkortning
    if (basRubrik.length <= 40) {
        return basRubrik
    }
    
    // Korta titeln så att hela strängen blir 40 tecken
    val maxTitelLängd = 40 - typPrefix.length - 2 - 2 - tidStr.length // -2 för ": ", -2 för ", "
    if (maxTitelLängd <= 0) {
        return "$typPrefix: …, $tidStr"
    }
    
    val kortTitel = if (titel.length > maxTitelLängd) {
        titel.substring(0, maxTitelLängd - 1) + "…"
    } else {
        titel
    }
    
    return "$typPrefix: $kortTitel, $tidStr"
}
