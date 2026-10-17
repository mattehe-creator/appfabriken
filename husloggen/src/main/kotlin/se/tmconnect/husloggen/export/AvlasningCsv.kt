package se.tmconnect.husloggen.export

import se.tmconnect.husloggen.regler.AvlasningMedForbrukning
import se.tmconnect.husloggen.regler.formateraMatareVarde
import se.tmconnect.karna.export.csvDokument
import se.tmconnect.karna.export.csvRad

data class AvlasningCsvTexter(
    val kolumnDatum: String,
    val kolumnVarde: String,
    val kolumnEnhet: String,
    val kolumnForbrukning: String,
    val kolumnEnhetKort: String,
)

fun avlasningCsv(
    matareNamn: String,
    enhet: String,
    rader: List<AvlasningMedForbrukning>,
    texter: AvlasningCsvTexter,
    datumEtikett: (AvlasningMedForbrukning) -> String,
): String {
    val csvRader = mutableListOf<String>()
    csvRader += csvRad(
        listOf(
            texter.kolumnDatum,
            texter.kolumnVarde,
            texter.kolumnEnhet,
            texter.kolumnForbrukning,
            texter.kolumnEnhetKort,
        ),
    )
    for (rad in rader) {
        csvRader +=
            csvRad(
                listOf(
                    datumEtikett(rad),
                    formateraMatareVarde(rad.avlasning.varde),
                    enhet,
                    rad.periodForbrukning?.let { formateraMatareVarde(it) }.orEmpty(),
                    enhet,
                ),
            )
    }
    return csvDokument(csvRader)
}

fun avlasningCsvFilnamn(matareNamn: String, idag: java.time.LocalDate): String {
    val saker = matareNamn.lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-')
    val bas = if (saker.isEmpty()) "matare" else saker
    return "husloggen-$bas-${idag}.csv"
}
