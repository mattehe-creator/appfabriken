package se.tmconnect.styrelserummet.regler

fun fordelaEfterAndelstal(totalKronor: Long, andelstal: List<Long>): List<Long>? {
    if (totalKronor < 0 || andelstal.isEmpty()) {
        return null
    }
    if (andelstal.any { it < 0 }) {
        return null
    }

    val summa = andelstal.sum()
    if (summa == 0L) {
        return null
    }

    val grund = andelstal.map { totalKronor * it / summa }
    val rest = andelstal.map { totalKronor * it % summa }

    val kvarAttForda = totalKronor - grund.sum()
    val resultat = grund.toMutableList()

    val indexRestPar = rest.withIndex().sortedByDescending { it.value }
    for (i in 0 until kvarAttForda.toInt()) {
        val index = indexRestPar[i].index
        resultat[index] += 1
    }

    return resultat
}
