package se.tmconnect.flextid.regler

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import se.tmconnect.flextid.data.Dag

/** Minuter efter midnatt för påminnelse om att stämpla in; standard 08:00. */
const val STANDARD_STAMPLA_IN_PAMINNELSE_MIN_EFTER_MIDNATT = 8 * 60

private const val MIN_STAMPLA_IN_PAMINNELSE_MIN = 5 * 60
private const val MAX_STAMPLA_IN_PAMINNELSE_MIN = 12 * 60

fun giltigaStamplaInPaminnelseMinEfterMidnatt(): List<Int> =
    generateSequence(MIN_STAMPLA_IN_PAMINNELSE_MIN) { it + 30 }
        .takeWhile { it <= MAX_STAMPLA_IN_PAMINNELSE_MIN }
        .toList()

fun arGiltigtStamplaInPaminnelseMinEfterMidnatt(min: Int): Boolean =
    min in MIN_STAMPLA_IN_PAMINNELSE_MIN..MAX_STAMPLA_IN_PAMINNELSE_MIN && min % 30 == 0

fun normaliseraStamplaInPaminnelseMinEfterMidnatt(sparad: Int?): Int {
    if (sparad == null) return STANDARD_STAMPLA_IN_PAMINNELSE_MIN_EFTER_MIDNATT
    if (!arGiltigtStamplaInPaminnelseMinEfterMidnatt(sparad)) {
        return STANDARD_STAMPLA_IN_PAMINNELSE_MIN_EFTER_MIDNATT
    }
    return sparad
}

fun stamplaInPaminnelseKlockan(minEfterMidnatt: Int): LocalTime =
    minuterTillLocalTime(normaliseraStamplaInPaminnelseMinEfterMidnatt(minEfterMidnatt))

fun nastaArbetsdagEfter(efter: LocalDate, arbetsdagarMask: Int): LocalDate {
    var dag = efter.plusDays(1)
    while (!arArbetsdag(dag, arbetsdagarMask)) {
        dag = dag.plusDays(1)
    }
    return dag
}

/**
 * Nästa tidpunkt för påminnelsen "stämpla in" på en arbetsdag utan registrerad start.
 * [dag] är dagens post om den finns (används bara för idag).
 */
fun beraknaNastaStamplaInPaminnelse(
    idag: LocalDate,
    nu: LocalTime,
    dag: Dag?,
    arbetsdagarMask: Int,
    paminnelseMinEfterMidnatt: Int,
): LocalDateTime {
    val klockslag = stamplaInPaminnelseKlockan(paminnelseMinEfterMidnatt)

    if (dag?.start != null) {
        val nastaDag = nastaArbetsdagEfter(idag, arbetsdagarMask)
        return LocalDateTime.of(nastaDag, klockslag)
    }

    if (arArbetsdag(idag, arbetsdagarMask)) {
        if (nu.isBefore(klockslag)) {
            return LocalDateTime.of(idag, klockslag)
        }
        val nastaDag = nastaArbetsdagEfter(idag, arbetsdagarMask)
        return LocalDateTime.of(nastaDag, klockslag)
    }

    var kandidat = idag
    while (!arArbetsdag(kandidat, arbetsdagarMask)) {
        kandidat = kandidat.plusDays(1)
    }
    return LocalDateTime.of(kandidat, klockslag)
}
