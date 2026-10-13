package se.tmconnect.garantivalvet.regler

import java.time.LocalDate
import java.util.Locale

/**
 * Bygger frågan som användaren själv kan dela till sin egen AI-app. Innehåller bara köpets fakta (vara, butik,
 * datum, pris, fel och krav), aldrig namn, adress eller kvitto. Appen skickar ingenting själv.
 * [mall] kommer ur `strings.xml`; `%1$s` ersätts med faktarutan.
 */
fun byggAiPrompt(
    mall: String,
    vad: String,
    butik: String?,
    kopdatum: LocalDate,
    prisOre: Long?,
    felBeskrivning: String,
    felUpptackt: LocalDate,
    krav: Krav,
    texter: ReklamationsTexter,
    locale: Locale,
    idag: LocalDate = LocalDate.now(),
): String {
    val kravText = when (krav) {
        Krav.AVHJALP -> texter.kravAvhjalp
        Krav.PRISAVDRAG -> texter.kravPrisavdrag
        Krav.HAVA -> texter.kravHava
    }
    val fakta = buildList {
        add("${texter.vara}: ${vad.trim()}")
        butik?.trim()?.takeIf { it.isNotEmpty() }?.let { add("${texter.butik}: $it") }
        add("${texter.kopdatum}: ${formateraDatum(kopdatum, locale)}")
        prisOre?.let { add("${texter.pris}: ${formateraPrisKr(it, locale)}") }
        felBeskrivning.trim().takeIf { it.isNotEmpty() && it != "…" }?.let { add("${texter.fel}: $it") }
        add("${texter.upptacktes}: ${formateraDatum(felUpptackt, locale)}")
        add(texter.kravMall.replace("%1\$s", kravText))
        add("${texter.reklamationsdatum}: ${formateraDatum(idag, locale)}")
    }.joinToString("\n") { "- $it" }
    return mall.replace("%1\$s", fakta)
}
