package se.tmconnect.husloggen.widget

import java.time.LocalDate
import se.tmconnect.husloggen.data.Uppgift
import se.tmconnect.husloggen.regler.nastaDatum
import se.tmconnect.husloggen.regler.sorteraUppgifter

/** En rad i widgeten: uppgiftens namn, nästa datum och om den är försenad. */
data class WidgetUppgiftRad(val namn: String, val datum: LocalDate, val forsenad: Boolean)

/** De [max] närmaste uppgifterna för bostaden, i datumordning. */
fun kommandeWidgetRader(
    uppgifter: List<Uppgift>,
    bostadId: Long?,
    idag: LocalDate,
    max: Int = 3,
): List<WidgetUppgiftRad> =
    sorteraUppgifter(uppgifter.filter { bostadId == null || it.bostadId == bostadId }, idag)
        .take(max.coerceAtLeast(0))
        .map {
            val datum = nastaDatum(it.senastGjord, it.startDatum, it.intervallManader)
            WidgetUppgiftRad(it.namn, datum, datum.isBefore(idag))
        }
