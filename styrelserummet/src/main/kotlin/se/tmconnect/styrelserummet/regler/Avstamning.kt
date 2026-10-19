package se.tmconnect.styrelserummet.regler

import se.tmconnect.styrelserummet.modell.Avstamning
import se.tmconnect.styrelserummet.modell.AvstamningDto
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp

fun stamAv(komponenter: List<Komponent>, byggnadensVarde: Long): Avstamning {
    val summa = komponenter.sumOf { it.anskaffningsvarde }
    val differens = summa - byggnadensVarde
    return Avstamning(
        summa = summa,
        byggnadensVarde = byggnadensVarde,
        differens = differens,
        stammer = differens == 0L,
    )
}

fun fordela(total: Long, andelar: List<Pair<Komponentgrupp, Int>>): Map<Komponentgrupp, Long> {
    val summaProcent = andelar.sumOf { it.second }
    require(summaProcent == 100) {
        "Andelarna måste summera till 100 procent, fick $summaProcent."
    }
    if (total == 0L) return andelar.associate { it.first to 0L }
    val rader = andelar.map { (grupp, procent) ->
        grupp to (total * procent / 100)
    }.toMutableList()
    val diff = total - rader.sumOf { it.second }
    if (diff != 0L) {
        val storsta = rader.maxBy { it.second }
        val idx = rader.indexOf(storsta)
        rader[idx] = storsta.first to (storsta.second + diff)
    }
    return rader.toMap()
}

fun summeraPerGrupp(komponenter: List<Komponent>, arsslut: Int): List<se.tmconnect.styrelserummet.modell.GruppSumma> {
    val perGrupp = komponenter.groupBy { it.gruppEnum() }
    return Komponentgrupp.entries.mapNotNull { grupp ->
        val lista = perGrupp[grupp] ?: return@mapNotNull null
        val anskaffning = lista.sumOf { it.anskaffningsvarde }
        val avskrivningAret = lista.sumOf { avskrivningFor(it, arsslut) }
        val bokfort = lista.sumOf { bokfortVarde(it, arsslut) }
        se.tmconnect.styrelserummet.modell.GruppSumma(grupp, anskaffning, avskrivningAret, bokfort)
    }
}

fun tillAvstamningDto(a: Avstamning) = AvstamningDto(
    summa = a.summa,
    byggnadensVarde = a.byggnadensVarde,
    differens = a.differens,
    stammer = a.stammer,
)
