package se.tmconnect.flextid.widget

import java.time.LocalDate
import java.time.LocalTime
import se.tmconnect.flextid.data.Dag
import se.tmconnect.flextid.regler.Arbetstid
import se.tmconnect.flextid.regler.StampelLage
import se.tmconnect.flextid.regler.beraknaSaldo
import se.tmconnect.flextid.regler.stampelLage

/** Det widgeten visar: saldo, dagens läge och instämplingen om dagen pågår. */
data class WidgetTillstand(
    val saldoMin: Int,
    val lage: StampelLage,
    val start: LocalTime?,
)

fun widgetTillstand(
    dagar: List<Dag>,
    ingangssaldoMin: Int,
    arbetstid: Arbetstid,
    idag: LocalDate,
): WidgetTillstand {
    val dagensDag = dagar.firstOrNull { it.datum == idag }
    return WidgetTillstand(
        saldoMin = beraknaSaldo(dagar, ingangssaldoMin, arbetstid),
        lage = stampelLage(dagensDag),
        start = dagensDag?.start,
    )
}
