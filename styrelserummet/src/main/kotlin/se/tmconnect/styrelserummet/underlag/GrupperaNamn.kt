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
)

fun gissaGrupp(namn: String): Komponentgrupp? {
    val lower = namn.lowercase()
    val basta = nyckelord.mapNotNull { nyckel ->
        val index = lower.indexOf(nyckel.ord)
        if (index < 0) null else Triple(index, nyckel.ord.length, nyckel.grupp)
    }.minWithOrNull(compareBy<Triple<Int, Int, Komponentgrupp>>({ it.first }).thenByDescending { it.second })
        ?: return null
    return basta.third
}
