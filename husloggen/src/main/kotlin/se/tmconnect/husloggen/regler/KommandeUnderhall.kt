package se.tmconnect.husloggen.regler

import java.time.LocalDate
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.Uppgift

data class KommandeUnderhallRad(
    val uppgiftId: Long,
    val namn: String,
    val datum: LocalDate,
    val kostnadOre: Long?,
)

data class KommandeUnderhallArGrupp(
    val ar: Int,
    val rader: List<KommandeUnderhallRad>,
    val summaOre: Long,
    val saknarKostnad: Boolean,
)

fun kommandeUnderhall(
    uppgifter: List<Uppgift>,
    loggposter: List<Post>,
    idag: LocalDate,
    antalAr: Int = 5,
): List<KommandeUnderhallArGrupp> {
    val senasteKostnad = senasteKostnadPerUppgift(loggposter)
    val slut = idag.plusYears(antalAr.toLong())
    val perAr = mutableMapOf<Int, MutableList<KommandeUnderhallRad>>()

    for (uppgift in uppgifter) {
        if (uppgift.intervallManader < 1) continue
        val datumLista =
            forekomster(
                uppgift.senastGjord,
                uppgift.startDatum,
                uppgift.intervallManader,
                idag,
                slut,
            )
        for (datum in datumLista) {
            perAr.getOrPut(datum.year) { mutableListOf() }.add(
                KommandeUnderhallRad(
                    uppgiftId = uppgift.id,
                    namn = uppgift.namn,
                    datum = datum,
                    kostnadOre = senasteKostnad[uppgift.id],
                ),
            )
        }
    }

    return perAr.keys.sorted().map { ar ->
        val rader =
            perAr.getValue(ar).sortedWith(compareBy({ it.datum }, { it.namn }))
        val summaOre = rader.mapNotNull { it.kostnadOre }.sum()
        val saknarKostnad = rader.any { it.kostnadOre == null }
        KommandeUnderhallArGrupp(ar, rader, summaOre, saknarKostnad)
    }
}
