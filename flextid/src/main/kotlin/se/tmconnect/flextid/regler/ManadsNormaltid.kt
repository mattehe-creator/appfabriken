package se.tmconnect.flextid.regler

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
