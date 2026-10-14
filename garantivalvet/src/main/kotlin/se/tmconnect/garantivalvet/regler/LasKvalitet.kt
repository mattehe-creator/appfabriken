package se.tmconnect.garantivalvet.regler

import java.time.LocalDate

private val ORD = Regex("""\p{L}{4,}""")
private val VOKALER = "aeiouyåäöAEIOUYÅÄÖ"
private const val FORSTA_FAR_VINNA_OM = 1.15

/**
 * Hur bra en avläsning verkar vara: ord som ser ut som ord (ett kvitto som ligger på sidan eller
 * upp och ned ger skräp), plus det tolkningen hittar ur texten.
 */
internal fun lasPoang(text: String, idag: LocalDate): Int {
    val ord = ORD.findAll(text).map { it.value }.toList()
    val riktigaOrd = ord.count { o ->
        val vokaler = o.count { it in VOKALER }
        vokaler >= 1 && vokaler * 5 >= o.length && o.length - vokaler <= 5 * vokaler.coerceAtLeast(1) &&
            (o == o.uppercase() || o == o.lowercase() || o.drop(1) == o.drop(1).lowercase())
    }
    val forslag = tolkaKvitto(text, idag)
    var poang = riktigaOrd
    if (hittaKandButik(text) != null) poang += 30
    if (forslag.datum != null) poang += 15
    if (forslag.prisOre != null) poang += 15
    if (forslag.oppetKopDagar != null) poang += 5
    return poang
}

/**
 * Väljer den bästa av avläsningarna av samma kvitto i olika rotationer (index 0 är bilden som den är).
 * Bilden som den är behålls om ingen annan är tydligt bättre, så att en vanlig avläsning inte byts i onödan.
 */
fun valjBastaLasning(lasningar: List<String>, idag: LocalDate): String? {
    val med = lasningar.filter { it.isNotBlank() }
    if (med.isEmpty()) return null
    val poang = med.map { lasPoang(it, idag) }
    val basta = poang.max()
    val forsta = med.first()
    return if (lasningar.first().isNotBlank() && poang.first() * FORSTA_FAR_VINNA_OM >= basta) forsta else med[poang.indexOf(basta)]
}
