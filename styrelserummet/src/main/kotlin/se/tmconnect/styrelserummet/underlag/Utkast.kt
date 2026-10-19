package se.tmconnect.styrelserummet.underlag

import se.tmconnect.styrelserummet.modell.Forening
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet
import se.tmconnect.styrelserummet.regler.Avstamning
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
        val byggnadsar = forening.byggnadsar ?: arsData.byggnadsar ?: nuAr
        val tabell = lasNyttjandeperioderFranResurs()

        val sidorLista = if (maskadPlan.contains('\u000C')) {
            maskadPlan.split('\u000C')
        } else {
            listOf(maskadPlan)
        }
        val sidor = delaUpp(sidorLista, maxTeckenPerAnrop)
        val anmarkningar = mutableListOf<String>()
        val forslag = mutableListOf<ForslagKomponent>()

        sidor.forEach { del ->
            val json = tolkare.foreslaKomponenter(
                underhallsplanUtdrag = del.text,
                sidor = del.sidnummer,
                filnamn = underhallsplanFilnamn,
            ).getOrElse { fel ->
                anmarkningar += "Tolkning misslyckades för ${del.sidnummer}: ${fel.message ?: "okänt fel"}."
                return@forEach
            }
            val resultat = tolkaForslag(json, underhallsplanFilnamn)
            anmarkningar += resultat.anmarkningar
            forslag += resultat.komponenter
        }

        var komponenter = forslag.mapNotNull { f ->
            val grupp = f.grupp ?: return@mapNotNull null
            val nyttjandeperiod = f.nyttjandeperiodAr ?: tabell.find { it.grupp == grupp }?.forslagAr ?: run {
                anmarkningar += "Komponenten \"${f.namn}\" saknar nyttjandeperiod."
                return@mapNotNull null
            }
            val anskaffningsar = f.anskaffningsar ?: byggnadsar
            val varde = f.anskaffningsvarde
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
        )
        return utkast.copy(anmarkningar = anmarkningarFor(utkast, tabell))
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
