package se.tmconnect.flextid.regler

import java.time.LocalTime
import java.time.temporal.ChronoUnit

sealed interface DagFormularTolkning {
    data class Ok(val start: LocalTime?, val slut: LocalTime?, val rastMin: Int) : DagFormularTolkning

    data class Fel(val fel: TidValideringFel) : DagFormularTolkning
}

/**
 * Tolkar dagformulärets fält. Tomt fält betyder ingen tid (eller standardrast).
 * Text som inte går att tolka ger fel i stället för att tyst sparas som "ingen tid".
 */
fun tolkaDagFormular(
    startText: String,
    slutText: String,
    rastText: String,
    standardRastMin: Int,
): DagFormularTolkning {
    val start = tolkaTidText(startText)
    if (start == null && startText.isNotBlank()) return DagFormularTolkning.Fel(TidValideringFel.OgiltigTid)
    val slut = tolkaTidText(slutText)
    if (slut == null && slutText.isNotBlank()) return DagFormularTolkning.Fel(TidValideringFel.OgiltigTid)
    val rast =
        if (rastText.isBlank()) {
            standardRastMin.coerceAtLeast(0)
        } else {
            rastText.trim().toIntOrNull()?.takeIf { it >= 0 }
                ?: return DagFormularTolkning.Fel(TidValideringFel.OgiltigTid)
        }
    valideraTider(start, slut)?.let { return DagFormularTolkning.Fel(it) }
    if (start != null && slut != null && rast > ChronoUnit.MINUTES.between(start, slut)) {
        return DagFormularTolkning.Fel(TidValideringFel.RastLangreAnArbetstid)
    }
    return DagFormularTolkning.Ok(start, slut, rast)
}
