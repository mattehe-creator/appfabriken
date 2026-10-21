package se.tmconnect.flextid.regler

import java.time.LocalDate
import java.time.YearMonth

fun arbetsdagarIManad(
    ar: Int,
    manad: Int,
    arbetsdagarMask: Int,
    uteslutRodaDagar: Boolean,
    uteslutAftnar: Boolean,
): Int {
    val ym = YearMonth.of(ar, manad)
    val aftnar = svenskaAftnar(ar).map { it.datum }.toSet()
    val roda = svenskaHelgdagar(ar).map { it.datum }.toSet()
    return (1..ym.lengthOfMonth()).count { d ->
        val datum = ym.atDay(d)
        arArbetsdag(datum, arbetsdagarMask) &&
            !(uteslutRodaDagar && datum in roda) &&
            !(uteslutAftnar && datum in aftnar)
    }
}

fun normaltidForManadMin(
    ar: Int,
    manad: Int,
    arbetsdagarMask: Int,
    normaltidMinPerDag: Int,
    uteslutRodaDagar: Boolean,
    uteslutAftnar: Boolean,
): Int = arbetsdagarIManad(ar, manad, arbetsdagarMask, uteslutRodaDagar, uteslutAftnar) * normaltidMinPerDag

fun arbetsdagarIIntervall(fran: LocalDate, till: LocalDate, arbetsdagarMask: Int, aftnarLediga: Boolean): Int {
    if (fran.isAfter(till)) return 0
    
    var count = 0
    var datum = fran
    
    while (!datum.isAfter(till)) {
        if (arArbetsdag(datum, arbetsdagarMask) && !arLedigDag(datum, aftnarLediga)) {
            count++
        }
        datum = datum.plusDays(1)
    }
    
    return count
}

fun normaltidForIntervallMin(fran: LocalDate, till: LocalDate, arbetsdagarMask: Int, normaltidMinPerDag: Int, aftnarLediga: Boolean): Int {
    return arbetsdagarIIntervall(fran, till, arbetsdagarMask, aftnarLediga) * normaltidMinPerDag
}
