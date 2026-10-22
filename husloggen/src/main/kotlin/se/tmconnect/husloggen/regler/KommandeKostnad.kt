package se.tmconnect.husloggen.regler

import java.time.YearMonth

data class KommandeKostnad(
    val summaOre: Long,
    val antalMedKostnad: Int,
    val antalUtanKostnad: Int,
)

fun uppskattadKostnadKommande(
    kommande: Map<YearMonth, List<KommandeJobb>>,
    senasteKostnad: Map<Long, Long>,
): KommandeKostnad {
    var summaOre = 0L
    var antalMedKostnad = 0
    var antalUtanKostnad = 0

    for (jobbLista in kommande.values) {
        for (jobb in jobbLista) {
            val belopp = senasteKostnad[jobb.uppgiftId]
            if (belopp != null) {
                summaOre += belopp
                antalMedKostnad++
            } else {
                antalUtanKostnad++
            }
        }
    }

    return KommandeKostnad(summaOre, antalMedKostnad, antalUtanKostnad)
}
