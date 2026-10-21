package se.tmconnect.styrelserummet.underlag

import se.tmconnect.styrelserummet.modell.Komponentgrupp

private data class NyckelordMatch(val ord: String, val grupp: Komponentgrupp)

private val nyckelord = listOf(
    NyckelordMatch("yttertak", Komponentgrupp.TAK),
    NyckelordMatch("takpapp", Komponentgrupp.TAK),
    NyckelordMatch("tegel", Komponentgrupp.TAK),
    NyckelordMatch("fasadtegel", Komponentgrupp.FASAD),
    NyckelordMatch("fasad", Komponentgrupp.FASAD),
    NyckelordMatch("puts", Komponentgrupp.FASAD),
    NyckelordMatch("balkongdörr", Komponentgrupp.FONSTER_DORRAR),
    NyckelordMatch("entrédörr", Komponentgrupp.FONSTER_DORRAR),
    NyckelordMatch("fönster", Komponentgrupp.FONSTER_DORRAR),
    NyckelordMatch("stammar", Komponentgrupp.STAMMAR),
    NyckelordMatch("avlopp", Komponentgrupp.STAMMAR),
    NyckelordMatch("vattenledning", Komponentgrupp.STAMMAR),
    NyckelordMatch("ventilation", Komponentgrupp.VENTILATION),
    NyckelordMatch("ftx", Komponentgrupp.VENTILATION),
    NyckelordMatch("frånluft", Komponentgrupp.VENTILATION),
    NyckelordMatch("tilluft", Komponentgrupp.VENTILATION),
    NyckelordMatch("elcentral", Komponentgrupp.EL),
    NyckelordMatch("elstammar", Komponentgrupp.EL),
    NyckelordMatch("värme", Komponentgrupp.VARME),
    NyckelordMatch("fjärrvärme", Komponentgrupp.VARME),
    NyckelordMatch("radiator", Komponentgrupp.VARME),
    NyckelordMatch("undercentral", Komponentgrupp.VARME),
    NyckelordMatch("tvättstuga", Komponentgrupp.TVATTSTUGA),
    NyckelordMatch("hiss", Komponentgrupp.HISS),
    NyckelordMatch("garage", Komponentgrupp.MARK_OCH_GARAGE),
    NyckelordMatch("asfalt", Komponentgrupp.MARK_OCH_GARAGE),
)

private fun indexSomEgetOrd(text: String, ord: String): Int {
    var start = 0
    while (start <= text.length - ord.length) {
        val index = text.indexOf(ord, start)
        if (index < 0) return -1
        val fore = index == 0 || !text[index - 1].isLetter()
        val efterIndex = index + ord.length
        val efter = efterIndex >= text.length || !text[efterIndex].isLetter()
        if (fore && efter) return index
        start = index + 1
    }
    return -1
}

fun gissaGrupp(namn: String): Komponentgrupp? {
    val lower = namn.lowercase()
    val traeffar = nyckelord.mapNotNull { nyckel ->
        val index = lower.indexOf(nyckel.ord)
        if (index < 0) null else Triple(index, nyckel.ord.length, nyckel.grupp)
    }.toMutableList()
    val elIndex = indexSomEgetOrd(lower, "el")
    if (elIndex >= 0) {
        traeffar.add(Triple(elIndex, 2, Komponentgrupp.EL))
    }
    for (ord in listOf("gård", "mark")) {
        val index = indexSomEgetOrd(lower, ord)
        if (index >= 0) {
            traeffar.add(Triple(index, ord.length, Komponentgrupp.MARK_OCH_GARAGE))
        }
    }
    val basta = traeffar.minWithOrNull(
        compareBy<Triple<Int, Int, Komponentgrupp>>({ it.first }).thenByDescending { it.second },
    ) ?: return null
    return basta.third
}
