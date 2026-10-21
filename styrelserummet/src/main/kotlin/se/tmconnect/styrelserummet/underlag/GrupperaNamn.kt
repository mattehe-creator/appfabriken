package se.tmconnect.styrelserummet.underlag

import se.tmconnect.styrelserummet.modell.Komponentgrupp

fun gissaGrupp(namn: String): Komponentgrupp? {
    val namnL = namn.lowercase()
    
    // Definiera sökord för varje grupp
    val takNyckelord = listOf("yttertak", "takpapp", "tegel")
    val fasadNyckelord = listOf("fasad", "puts", "fasadtegel")
    val fonsterDorrNyckelord = listOf("fönster", "balkongdörr", "entrédörr")
    
    // Hitta första förekomsten av varje nyckelord
    val takIndex = findFirstIndex(namnL, takNyckelword)
    val fasadIndex = findFirstIndex(namnL, fasadNyckelword)
    val fonsterDorrIndex = findFirstIndex(namnL, fonsterDorrNyckelword)
    
    // Hitta lägsta index (först förekommande)
    val minIndex = listOf(takIndex, fasadIndex, fonsterDorrIndex)
        .filter { it != -1 }
        .minOrNull()
    
    // Returnera rätt grupp baserat på vilket nyckelord som kom först
    return when {
        minIndex == takIndex -> Komponentgrupp.TAK
        minIndex == fasadIndex -> Komponentgrupp.FASAD
        minIndex == fonsterDorrIndex -> Komponentgrupp.FONSTER_DORRAR
        else -> null
    }
}

private fun findFirstIndex(namn: String, nyckelord: List<String>): Int {
    val indices = nyckelord.map { namn.indexOf(it) }.filter { it != -1 }
    return if (indices.isNotEmpty()) indices.minOrNull()!! else -1
}
