package se.tmconnect.garantivalvet.regler

import java.time.LocalDate
import se.tmconnect.garantivalvet.data.Kop

/** Sök och filter i listan över köp. Tomt filter släpper igenom allt. */
data class KopFilter(
    val fraga: String = "",
    val grupp: ListaGrupp? = null,
    val varutyp: Varutyp? = null,
) {
    val arTomt: Boolean get() = fraga.isBlank() && grupp == null && varutyp == null
}

/** Visa sök och filter först när listan är lång nog för att behövas. */
const val SOK_FRAN_ANTAL = 5

/**
 * Köp som matchar [filter]. Frågan delas i ord och alla ord måste finnas någonstans i vara, butik, anteckning,
 * felbeskrivning eller butikens kontaktuppgifter, oavsett versaler.
 */
fun filtreraKop(lista: List<Kop>, filter: KopFilter, idag: LocalDate): List<Kop> {
    if (filter.arTomt) return lista
    val ord = filter.fraga.lowercase().split(Regex("""\s+""")).filter { it.isNotEmpty() }
    return lista.filter { kop ->
        (filter.grupp == null || listaGrupp(kop, idag) == filter.grupp) &&
            (filter.varutyp == null || Varutyp.franNamn(kop.varutyp) == filter.varutyp) &&
            ord.all { it in sokText(kop) }
    }
}

private fun sokText(kop: Kop): String =
    listOfNotNull(kop.vad, kop.varKopt, kop.anteckning, kop.felBeskrivning, kop.butikEpost, kop.butikTelefon, kop.butikOrgnr)
        .joinToString(" ")
        .lowercase()
