package se.tmconnect.flextid.paminnelse

import java.time.LocalDate
import java.time.LocalTime
import se.tmconnect.flextid.data.Dag
import se.tmconnect.flextid.data.Installning
import se.tmconnect.flextid.regler.Arbetstid
import se.tmconnect.flextid.regler.arArbetsdagMedHelger
import se.tmconnect.flextid.regler.arbetstid
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
    arbetstid: Arbetstid = installning.arbetstid(),
): Boolean {
    val idagDag = dag ?: return false
    val start = idagDag.start ?: return false
    if (idagDag.slut != null) return false
    // På en ledig dag (helg, röd dag) används standardnormaltiden, så att påminnelsen ändå kommer.
    val normaltid = arbetstid.normaltidFor(idagDag.datum).takeIf { it > 0 } ?: installning.normaltidMinPerDag
    val grans = beraknaStamplaUtPaminnelseTid(start, normaltid, idagDag.rastMin)
    return !nu.isBefore(grans)
}

fun skaPaminnelseStamplaIn(
    dag: Dag?,
    installning: Installning,
    idag: LocalDate,
    nu: LocalTime,
    stamplaInPaminnelseMinEfterMidnatt: Int,
    aftnarLediga: Boolean = true,
): Boolean {
    if (!arArbetsdagMedHelger(idag, installning.arbetsdagarMask, aftnarLediga)) return false
    if (dag?.start != null) return false
    // Semester, sjuk, flexledig och annan ledighet: ingen påminnelse om att stämpla in.
    if (dag != null && dag.typ != se.tmconnect.flextid.regler.DagTyp.ARBETE) return false
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
    aftnarLediga: Boolean = true,
    arbetstid: Arbetstid = installning.arbetstid(aftnarLediga),
): List<PaminnelseFrist> {
    val frister = mutableListOf<PaminnelseFrist>()
    if (stamplaUtPa && skaPaminnelseStamplaUt(dag, installning, nu, arbetstid)) {
        frister +=
            PaminnelseFrist(
                nyckel = "stampla_ut:$idag",
                slutdatum = idag,
            )
    }
    if (stamplaInPa && skaPaminnelseStamplaIn(dag, installning, idag, nu, stamplaInPaminnelseMinEfterMidnatt, aftnarLediga)) {
        frister +=
            PaminnelseFrist(
                nyckel = "stampla_in:$idag",
                slutdatum = idag,
            )
    }
    return frister
}
