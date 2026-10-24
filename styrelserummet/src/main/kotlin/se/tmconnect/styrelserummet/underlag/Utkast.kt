package se.tmconnect.styrelserummet.underlag

import se.tmconnect.styrelserummet.modell.Forening
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet
import se.tmconnect.styrelserummet.regler.Avstamning
import se.tmconnect.styrelserummet.regler.PlaneradAtgard
import se.tmconnect.styrelserummet.regler.fordela
import se.tmconnect.styrelserummet.regler.lasNyttjandeperioderFranResurs
import se.tmconnect.styrelserummet.regler.stamAv
import se.tmconnect.styrelserummet.regler.validera
import se.tmconnect.styrelserummet.tolking.Tolkare
import java.time.Year

const val UTKAST_RAD = "UTKAST. Beslutas av förening och revisor."

data class Utkast(
    val forening: Forening,
    val komponenter: List<Komponent>,
    val byggnadensVarde: Long?,
    val arsredovisning: ArsredovisningUtdrag,
    val avstamning: Avstamning?,
    val anmarkningar: List<String>,
    val atgarder: List<PlaneradAtgard> = emptyList(),
)

class ByggUtkast(
    private val tolkare: Tolkare,
    private val maxTeckenPerAnrop: Int = 12_000,
    private val nuAr: Int = Year.now().value,
) {
    fun byggUtkast(
        underhallsplanText: String,
        arsredovisningText: String,
        forening: Forening,
        underhallsplanFilnamn: String = "underhallsplan.pdf",
    ): Utkast {
        val maskadPlan = maskaPersonuppgifter(underhallsplanText)
        val maskadAr = maskaPersonuppgifter(arsredovisningText)
        val arsData = lasArsredovisning(maskadAr)
        val byggnadsar = (forening.byggnadsar ?: arsData.byggnadsar)?.takeIf { it in 1800..nuAr } ?: nuAr
        val tabell = lasNyttjandeperioderFranResurs()

        val sidorLista = if (maskadPlan.contains('\u000C')) {
            maskadPlan.split('\u000C')
        } else {
            listOf(maskadPlan)
        }
        val sidor = delaUpp(sidorLista, maxTeckenPerAnrop)
        val anmarkningar = mutableListOf<String>()
        val forslag = mutableListOf<ForslagKomponent>()
        val atgarder = linkedSetOf<PlaneradAtgard>()

        sidor.forEach { del ->
            val json = tolkare.foreslaKomponenter(
                underhallsplanUtdrag = del.text,
                sidor = del.sidnummer,
                filnamn = underhallsplanFilnamn,
            ).getOrElse { fel ->
                anmarkningar += "Tolkning misslyckades för sidorna ${del.sidnummer.joinToString(", ")}: ${tolkningsFelText(fel.message)}."
                return@forEach
            }
            val resultat = tolkaForslag(json, underhallsplanFilnamn)
            anmarkningar += resultat.anmarkningar
            forslag += resultat.komponenter
            resultat.atgarder.forEach { atgarder += PlaneradAtgard(it.ar, it.belopp, it.beskrivning) }
        }

        var komponenter = forslag.mapNotNull { f ->
            val grupp = f.grupp ?: return@mapNotNull null
            // Värden från tolkningen som inte är rimliga ersätts: nyttjandeperiod 1–150 år och
            // anskaffningsår 1800 till nästa år. Annars kan räkningen bli fel eller mycket långsam.
            val tolkadPeriod = f.nyttjandeperiodAr?.takeIf { it in 1..150 }
            if (f.nyttjandeperiodAr != null && tolkadPeriod == null) {
                anmarkningar += "Komponenten \"${f.namn}\": nyttjandeperioden ${f.nyttjandeperiodAr} år är orimlig och har ersatts med standardtabellens förslag."
            }
            val nyttjandeperiod = tolkadPeriod ?: tabell.find { it.grupp == grupp }?.forslagAr ?: run {
                anmarkningar += "Komponenten \"${f.namn}\" saknar nyttjandeperiod."
                return@mapNotNull null
            }
            val tolkatAr = f.anskaffningsar?.takeIf { it in 1800..(nuAr + 1) }
            if (f.anskaffningsar != null && tolkatAr == null) {
                anmarkningar += "Komponenten \"${f.namn}\": anskaffningsåret ${f.anskaffningsar} är orimligt och har ersatts med byggnadsåret."
            }
            val anskaffningsar = tolkatAr ?: byggnadsar
            val varde = f.anskaffningsvarde?.takeIf { it <= 1_000_000_000_000L }
            if (f.anskaffningsvarde != null && varde == null) {
                anmarkningar += "Komponenten \"${f.namn}\": beloppet är orimligt stort och har tagits bort."
            }
            if (varde == null) {
                null
            } else {
                Komponent(
                    namn = f.namn,
                    grupp = grupp,
                    anskaffningsvarde = varde,
                    anskaffningsar = anskaffningsar,
                    nyttjandeperiodAr = nyttjandeperiod,
                    kalla = f.kalla,
                    sakerhet = f.sakerhet,
                )
            }
        }.toMutableList()

        val byggnadensVarde = arsData.byggnadensVarde
        if (byggnadensVarde != null) {
            val saknarVarde = forslag.filter { it.anskaffningsvarde == null && it.grupp != null }
            if (saknarVarde.isNotEmpty()) {
                val redanSum = komponenter.sumOf { it.anskaffningsvarde }
                val attFordela = (byggnadensVarde - redanSum).coerceAtLeast(0)
                val perGrupp = saknarVarde.groupBy { it.grupp!! }
                val andelar = perGrupp.keys.map { grupp ->
                    grupp to (tabell.find { it.grupp == grupp }?.standardandelProcent ?: 0)
                }.filter { it.second > 0 }
                if (andelar.isNotEmpty() && attFordela > 0) {
                    val normaliserade = normaliseraAndelar(andelar, 100.0 / andelar.sumOf { it.second })
                    val fördelat = fordela(attFordela, normaliserade)
                    perGrupp.forEach { (grupp, lista) ->
                        val gruppsumma = fördelat[grupp] ?: 0L
                        if (gruppsumma <= 0L) return@forEach
                        val perKomponent = gruppsumma / lista.size
                        val rest = gruppsumma - perKomponent * lista.size
                        lista.forEachIndexed { index, f ->
                            val belopp = perKomponent + if (index < rest) 1L else 0L
                            komponenter += Komponent(
                                namn = f.namn,
                                grupp = grupp,
                                anskaffningsvarde = belopp,
                                anskaffningsar = f.anskaffningsar ?: byggnadsar,
                                nyttjandeperiodAr = f.nyttjandeperiodAr ?: tabell.find { it.grupp == grupp }?.forslagAr ?: 1,
                                kalla = f.kalla,
                                sakerhet = Sakerhet.UPPSKATTNING,
                            )
                        }
                    }
                } else if (andelar.isNotEmpty()) {
                    perGrupp.forEach { (grupp, lista) ->
                        lista.distinctBy { it.namn }.forEach { f ->
                            komponenter += Komponent(
                                namn = f.namn,
                                grupp = grupp,
                                anskaffningsvarde = 0L,
                                anskaffningsar = f.anskaffningsar ?: byggnadsar,
                                nyttjandeperiodAr = f.nyttjandeperiodAr ?: tabell.find { it.grupp == grupp }?.forslagAr ?: 1,
                                kalla = f.kalla,
                                sakerhet = Sakerhet.UPPSKATTNING,
                            )
                        }
                    }
                }
            }
        }

        komponenter.forEach { k ->
            val fel = validera(k, nuAr)
            if (fel.isNotEmpty()) {
                anmarkningar += fel.map { "${k.namn}: ${it.meddelande}" }
            }
        }

        val avstamning = byggnadensVarde?.let { stamAv(komponenter, it) }
        val utkast = Utkast(
            forening = forening,
            komponenter = komponenter,
            byggnadensVarde = byggnadensVarde,
            arsredovisning = arsData,
            avstamning = avstamning,
            anmarkningar = anmarkningar,
            atgarder = atgarder.sortedBy { it.ar }.take(MAX_ATGARDER_PER_SVAR),
        )
        return utkast.copy(anmarkningar = anmarkningarFor(utkast, tabell, nuAr))
    }
}

fun byggUtkast(
    underhallsplanText: String,
    arsredovisningText: String,
    forening: Forening,
    tolkare: Tolkare,
    nuAr: Int = Year.now().value,
): Utkast = ByggUtkast(tolkare, nuAr = nuAr).byggUtkast(
    underhallsplanText,
    arsredovisningText,
    forening,
)

private fun normaliseraAndelar(
    andelar: List<Pair<Komponentgrupp, Int>>,
    skala: Double,
): List<Pair<Komponentgrupp, Int>> {
    val skalade = andelar.map { (grupp, procent) -> grupp to kotlin.math.round(procent * skala).toInt() }
    val summa = skalade.sumOf { it.second }
    if (summa == 100) return skalade
    val diff = 100 - summa
    val index = skalade.indices.maxByOrNull { skalade[it].second } ?: 0
    return skalade.mapIndexed { i, (g, p) -> if (i == index) g to (p + diff) else g to p }
}

const val TOLKNING_FOR_LANG_TEXT = "svaret blev för långt"

/** Orsakskoden från tolkaren som läsbar text i anmärkningen. */
fun tolkningsFelText(kod: String?): String =
    when {
        kod == null -> "okänt fel"
        kod == "svaret_for_langt" -> "$TOLKNING_FOR_LANG_TEXT, dela upp handlingen"
        kod == "tidsgrans" -> "tjänsten svarade inte i tid"
        kod == "natverk" -> "tjänsten gick inte att nå"
        kod == "saknar_api_nyckel" -> "tjänsten saknar nyckel"
        kod == "saknar_svar" -> "tomt svar"
        kod == "http_429" || kod == "http_529" -> "tjänsten var överbelastad"
        kod.startsWith("http_") -> "fel från tjänsten (${kod.removePrefix("http_")})"
        else -> kod
    }

