package se.tmconnect.husloggen.export

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import se.tmconnect.husloggen.regler.KommandeUnderhallArGrupp
import se.tmconnect.karna.export.csvDokument
import se.tmconnect.karna.export.csvRad
import se.tmconnect.karna.export.prisSomDecimal

data class KommandeUnderhallCsvTexter(
    val kolumnAr: String,
    val kolumnUppgift: String,
    val kolumnDatum: String,
    val kolumnKostnad: String,
    val arSummaRubrik: (Int) -> String,
    val okandKostnad: String,
)

fun kommandeUnderhallCsv(
    grupper: List<KommandeUnderhallArGrupp>,
    texter: KommandeUnderhallCsvTexter,
    datumEtikett: (LocalDate) -> String,
    kostnadEtikett: (Long) -> String,
): String {
    val rader = mutableListOf<String>()
    rader +=
        csvRad(
            listOf(
                texter.kolumnAr,
                texter.kolumnUppgift,
                texter.kolumnDatum,
                texter.kolumnKostnad,
            ),
        )
    for (grupp in grupper) {
        for (rad in grupp.rader) {
            rader +=
                csvRad(
                    listOf(
                        grupp.ar.toString(),
                        rad.uppgiftNamn,
                        datumEtikett(rad.datum),
                        rad.uppskattadKostnadOre?.let { kostnadEtikett(it) } ?: texter.okandKostnad,
                    ),
                )
        }
        rader +=
            csvRad(
                listOf(
                    texter.arSummaRubrik(grupp.ar),
                    "",
                    "",
                    if (grupp.summaOre > 0) prisSomDecimal(grupp.summaOre) else "",
                ),
            )
    }
    return csvDokument(rader)
}

private val filDatum = DateTimeFormatter.ofPattern("yyyy-MM-dd")

fun kommandeUnderhallCsvFilnamn(idag: LocalDate = LocalDate.now()): String =
    "husloggen-kommande-underhall-${idag.format(filDatum)}.csv"
