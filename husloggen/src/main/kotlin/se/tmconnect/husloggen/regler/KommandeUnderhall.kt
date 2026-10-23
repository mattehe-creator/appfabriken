package se.tmconnect.husloggen.regler

import java.time.LocalDate
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.Uppgift

data class KommandeUnderhallRad(
    val uppgiftId: Long,
    val uppgiftNamn: String,
    val datum: LocalDate,
    val uppskattadKostnadOre: Long?,
)

data class KommandeUnderhallArGrupp(
    val ar: Int,
    val rader: List<KommandeUnderhallRad>,
    val summaOre: Long,
    val harOkandKostnad: Boolean,
)

fun kommandeUnderhall(
    uppgifter: List<Uppgift>,
    loggposter: List<Post>,
    idag: LocalDate,
    antalAr: Int = 5,
): List<KommandeUnderhallArGrupp> {
    val senasteKostnad = senasteKostnadPerUppgift(loggposter)
    val slut = idag.plusYears(antalAr.toLong()).minusDays(1)
    val allaRader = mutableListOf<KommandeUnderhallRad>()

    for (uppgift in uppgifter) {
        if (uppgift.intervallManader < 1) {
            continue
        }
        val kostnad = senasteKostnad[uppgift.id]
        val datumLista =
            forekomster(
                uppgift.senastGjord,
                uppgift.startDatum,
                uppgift.intervallManader,
                idag,
                slut,
            )
        for (datum in datumLista) {
            allaRader.add(
                KommandeUnderhallRad(
                    uppgiftId = uppgift.id,
                    uppgiftNamn = uppgift.namn,
                    datum = datum,
                    uppskattadKostnadOre = kostnad,
                ),
            )
        }
    }

    return allaRader
        .groupBy { it.datum.year }
        .toSortedMap()
        .map { (ar, rader) ->
            val sorterade = rader.sortedWith(compareBy({ it.datum }, { it.uppgiftNamn }))
            KommandeUnderhallArGrupp(
                ar = ar,
                rader = sorterade,
                summaOre = sorterade.sumOf { it.uppskattadKostnadOre ?: 0L },
                harOkandKostnad = sorterade.any { it.uppskattadKostnadOre == null },
            )
        }
}
