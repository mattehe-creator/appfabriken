package se.tmconnect.garantivalvet.regler

import se.tmconnect.garantivalvet.data.Kop
import java.time.LocalDate

/** Underlag till hemförsäkringen: alla köp med värde, dyrast först, och summan. */
data class HemforsakringRad(
    val vad: String,
    val butik: String?,
    val datum: LocalDate,
    val prisOre: Long?,
    val kvittoFil: String?,
)

data class HemforsakringSammanstallning(
    val rader: List<HemforsakringRad>,
    val summaOre: Long,
    val antalUtanPris: Int,
)

fun byggHemforsakring(kop: List<Kop>): HemforsakringSammanstallning {
    val rader = kop
        .map { HemforsakringRad(it.vad, it.varKopt?.takeIf { b -> b.isNotBlank() }, it.kopdatum, it.prisOre, it.kvittoFil) }
        .sortedWith(
            compareByDescending<HemforsakringRad> { it.prisOre ?: -1L }
                .thenBy { it.datum }
                .thenBy { it.vad.lowercase() },
        )
    return HemforsakringSammanstallning(
        rader = rader,
        summaOre = rader.sumOf { it.prisOre ?: 0L },
        antalUtanPris = rader.count { it.prisOre == null },
    )
}

/** Belopp som vanligt decimaltal med punkt (till exempel `1299.50`), så att kalkylprogram läser det oavsett språk. */
fun prisSomDecimal(prisOre: Long): String {
    val tecken = if (prisOre < 0) "-" else ""
    val absolut = kotlin.math.abs(prisOre)
    return "$tecken${absolut / 100}.${(absolut % 100).toString().padStart(2, '0')}"
}

/**
 * CSV med semikolon som skiljetecken (öppnas rakt i svenska Excel) och radbrytning CRLF. Celler som börjar med
 * `=`, `+`, `-` eller `@` får ett inledande `'` så att de inte körs som formler i kalkylprogram.
 */
fun hemforsakringCsv(
    sammanstallning: HemforsakringSammanstallning,
    rubriker: List<String>,
    summaRubrik: String,
): String {
    fun cell(text: String): String {
        val saker = if (text.isNotEmpty() && text[0] in "=+-@\t\r") "'$text" else text
        return if (saker.any { it == ';' || it == '"' || it == '\n' || it == '\r' }) {
            "\"" + saker.replace("\"", "\"\"") + "\""
        } else {
            saker
        }
    }
    val rader = mutableListOf(rubriker.joinToString(";") { cell(it) })
    for (r in sammanstallning.rader) {
        rader += listOf(
            cell(r.vad),
            cell(r.butik.orEmpty()),
            r.datum.toString(),
            r.prisOre?.let { prisSomDecimal(it) }.orEmpty(),
        ).joinToString(";")
    }
    rader += listOf(cell(summaRubrik), "", "", prisSomDecimal(sammanstallning.summaOre)).joinToString(";")
    return rader.joinToString("\r\n") + "\r\n"
}
