package se.tmconnect.flextid.paminnelse

import java.time.LocalDate
import java.time.LocalTime
import se.tmconnect.flextid.data.Dag
import se.tmconnect.flextid.data.Installning
import se.tmconnect.flextid.regler.arArbetsdag
import se.tmconnect.flextid.regler.stamplaInPaminnelseKlockan
import se.tmconnect.karna.paminnelse.PaminnelseFrist

/**
 * Tidpunkt för påminnelse om att stämpla ut: instämplad start + normaltid + rast
 * (samma klockslagslogik som ett fullt normalarbetsdygn, t.ex. 08:00 + 8 h + 30 min → 16:30).
 */
fun beraknaStamplaUtPaminnelseTid(
    start: LocalTime,
    normaltidMinPerDag: Int,
    rastMin: Int,
): LocalTime = start.plusMinutes(normaltidMinPerDag.toLong() + rastMin)

fun skaPaminnelseStamplaUt(
    dag: Dag?,
    installning: Installning,
    nu: LocalTime,
): Boolean {
    val idagDag = dag ?: return false
    val start = idagDag.start ?: return false
    if (idagDag.slut != null) return false
    val grans = beraknaStamplaUtPaminnelseTid(start, installning.normaltidMinPerDag, idagDag.rastMin)
    return !nu.isBefore(grans)
}

fun skaPaminnelseStamplaIn(
    dag: Dag?,
    installning: Installning,
    idag: LocalDate,
    nu: LocalTime,
    stamplaInPaminnelseMinEfterMidnatt: Int,
): Boolean {
    if (!arArbetsdag(idag, installning.arbetsdagarMask)) return false
    if (dag?.start != null) return false
    val klockslag = stamplaInPaminnelseKlockan(stamplaInPaminnelseMinEfterMidnatt)
    return !nu.isBefore(klockslag)
}

fun paminnelseFristerForDag(
    idag: LocalDate,
    dag: Dag?,
    installning: Installning,
    nu: LocalTime,
    stamplaUtPa: Boolean,
    stamplaInPa: Boolean,
    stamplaInPaminnelseMinEfterMidnatt: Int,
): List<PaminnelseFrist> {
    val frister = mutableListOf<PaminnelseFrist>()
    if (stamplaUtPa && skaPaminnelseStamplaUt(dag, installning, nu)) {
        frister +=
            PaminnelseFrist(
                nyckel = "stampla_ut:$idag",
                slutdatum = idag,
            )
    }
    if (stamplaInPa && skaPaminnelseStamplaIn(dag, installning, idag, nu, stamplaInPaminnelseMinEfterMidnatt)) {
        frister +=
            PaminnelseFrist(
                nyckel = "stampla_in:$idag",
                slutdatum = idag,
            )
    }
    return frister
}
