package se.tmconnect.garantivalvet.regler

import java.time.LocalDate
import java.util.Locale

/**
 * Vad konsumenten begär. Konsumentverket: först kan man kräva att felet åtgärdas (reparation eller
 * ny vara); därefter prisavdrag och, i allvarliga fall, hävning av köpet.
 */
enum class Krav {
    AVHJALP,
    PRISAVDRAG,
    HAVA,
}

/** Alla texter kommer ur `strings.xml`, så att brevet följer appens språk. `%1$s` ersätts med värdet. */
data class ReklamationsTexter(
    val amneMall: String,
    val hej: String,
    val intro: String,
    val vara: String,
    val kopdatum: String,
    val pris: String,
    val kvittoBifogas: String,
    val fel: String,
    val upptacktes: String,
    val kravMall: String,
    val kravAvhjalp: String,
    val kravPrisavdrag: String,
    val kravHava: String,
    val avslut: String,
    val halsning: String,
)

data class Reklamationsbrev(
    val amne: String,
    val text: String,
)

fun byggReklamationsbrev(
    vad: String,
    kopdatum: LocalDate,
    prisOre: Long?,
    kvittoFinns: Boolean,
    felBeskrivning: String,
    felUpptackt: LocalDate,
    krav: Krav,
    texter: ReklamationsTexter,
    locale: Locale,
): Reklamationsbrev {
    val datum = formateraDatum(kopdatum, locale)
    val kravText = when (krav) {
        Krav.AVHJALP -> texter.kravAvhjalp
        Krav.PRISAVDRAG -> texter.kravPrisavdrag
        Krav.HAVA -> texter.kravHava
    }
    val rader = buildList {
        add(texter.hej)
        add("")
        add(texter.intro)
        add("")
        add("${texter.vara}: ${vad.trim()}")
        add("${texter.kopdatum}: $datum")
        prisOre?.let { add("${texter.pris}: ${formateraPrisKr(it, locale)}") }
        if (kvittoFinns) add(texter.kvittoBifogas)
        add("")
        add("${texter.fel}: ${felBeskrivning.trim()}")
        add("${texter.upptacktes}: ${formateraDatum(felUpptackt, locale)}")
        add("")
        add(texter.kravMall.replace("%1\$s", kravText))
        add("")
        add(texter.avslut)
        add(texter.halsning)
    }
    return Reklamationsbrev(
        amne = texter.amneMall.replace("%1\$s", vad.trim()).replace("%2\$s", datum),
        text = rader.joinToString("\n"),
    )
}
