package se.tmconnect.flextid.regler

import java.time.LocalDate

private fun arPlanerbarArbetsdag(datum: LocalDate, arbetsdagarMask: Int, helgdagar: Boolean): Boolean =
    arArbetsdag(datum, arbetsdagarMask) && !arLedigDag(datum, helgdagar)

private fun nastaPlanerbarArbetsdag(efter: LocalDate, arbetsdagarMask: Int, helgdagar: Boolean): LocalDate {
    var dag = efter.plusDays(1)
    while (!arPlanerbarArbetsdag(dag, arbetsdagarMask, helgdagar)) {
        dag = dag.plusDays(1)
    }
    return dag
}

private fun kanTaUtDagar(
    start: LocalDate,
    antalDagar: Int,
    arbetsdagarMask: Int,
    helgdagar: Boolean,
): Boolean {
    if (!arPlanerbarArbetsdag(start, arbetsdagarMask, helgdagar)) return false
    var dag = start
    repeat(antalDagar - 1) {
        dag = nastaPlanerbarArbetsdag(dag, arbetsdagarMask, helgdagar)
    }
    return true
}

/**
 * Första datumet från och med imorgon då [antalDagar] arbetsdagar i följd kan tas ut.
 * [helgdagar] motsvarar inställningen aftnar lediga (röda dagar räknas alltid som lediga).
 */
fun tidigasteLedigDag(
    idag: LocalDate,
    antalDagar: Int,
    arbetsdagarMask: Int,
    helgdagar: Boolean,
): LocalDate? {
    if (antalDagar <= 0) return null
    var kandidat = idag.plusDays(1)
    while (true) {
        if (kanTaUtDagar(kandidat, antalDagar, arbetsdagarMask, helgdagar)) {
            return kandidat
        }
        kandidat = nastaPlanerbarArbetsdag(kandidat, arbetsdagarMask, helgdagar)
    }
}
