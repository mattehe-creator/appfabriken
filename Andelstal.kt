package se.tmconnect.styrelserummet.regler

fun fordelaEfterAndelstal(totalKronor: Long, andelstal: List<Long>): List<Long>? {
    if (totalKronor < 0 || andelstal.isEmpty()) {
        return null
    }
    
    val summa = andelstal.sumOf { it.toLong() }
    
    if (summa <= 0) {
        return null
    }
    
    // Beräkna grundbelopp och rest för varje andel
    val grund = andelstal.map { totalKronor * it / summa }
    val rest = andelstal.map { totalKronor * it % summa }
    
    // Räkna ut hur mycket som saknas att fördela
    val kvarAttForda = totalKronor - grund.sum()
    
    // Skapa resultatlistan
    val resultat = grund.toMutableList()
    
    // Fördela kvarvarande kronor (1 krona åt gången) till de med störst rest
    val indexRestPar = rest.withIndex().sortedByDescending { it.value }
    for (i in 0 until kvarAttForda.toInt()) {
        val index = indexRestPar[i].index
        resultat[index] += 1
    }
    
    return resultat
}
