package se.tmconnect.styrelserummet.regler

import se.tmconnect.styrelserummet.modell.Komponentgrupp

fun fordela(total: Long, andelar: List<Pair<Komponentgrupp, Int>>): Map<Komponentgrupp, Long> {
    val summaProcent = andelar.sumOf { it.second }
    require(summaProcent == 100) {
        "Andelarna måste summera till 100 procent, fick $summaProcent."
    }
    if (total == 0L) {
        return andelar.associate { it.first to 0L }
    }
    val fördelat = andelar.map { (grupp, procent) ->
        grupp to (total * procent / 100)
    }
    val rest = total - fördelat.sumOf { it.second }
    if (rest == 0L) {
        return fördelat.toMap()
    }
    val störstaIndex = fördelat.indices.maxWithOrNull(
        compareBy<Int> { fördelat[it].second }.thenBy { -it },
    ) ?: 0
    return fördelat.mapIndexed { index, (grupp, belopp) ->
        if (index == störstaIndex) grupp to (belopp + rest) else grupp to belopp
    }.toMap()
}
