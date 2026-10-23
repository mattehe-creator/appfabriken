package se.tmconnect.husloggen.export

import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.husloggen.regler.KommandeUnderhallArGrupp
import se.tmconnect.husloggen.regler.KommandeUnderhallRad
import java.time.LocalDate

class KommandeUnderhallCsvTest {

    @Test
    fun csvInnehallerRaderOchArssumma() {
        val grupper =
            listOf(
                KommandeUnderhallArGrupp(
                    ar = 2027,
                    rader =
                        listOf(
                            KommandeUnderhallRad(
                                uppgiftId = 1L,
                                uppgiftNamn = "Tak",
                                datum = LocalDate.of(2027, 5, 1),
                                uppskattadKostnadOre = 50_000L,
                            ),
                        ),
                    summaOre = 50_000L,
                    harOkandKostnad = false,
                ),
            )
        val texter =
            KommandeUnderhallCsvTexter(
                kolumnAr = "År",
                kolumnUppgift = "Uppgift",
                kolumnDatum = "Datum",
                kolumnKostnad = "Kostnad",
                arSummaRubrik = { "Summa $it" },
                okandKostnad = "Okänd",
            )
        val csv =
            kommandeUnderhallCsv(
                grupper = grupper,
                texter = texter,
                datumEtikett = { it.toString() },
                kostnadEtikett = { "%.2f".format(it / 100.0) },
            )
        assertTrue(csv.contains("Tak"))
        assertTrue(csv.contains("500.00"))
        assertTrue(csv.contains("Summa 2027"))
    }

    @Test
    fun csvOkandKostnad() {
        val grupper =
            listOf(
                KommandeUnderhallArGrupp(
                    ar = 2026,
                    rader =
                        listOf(
                            KommandeUnderhallRad(
                                uppgiftId = 2L,
                                uppgiftNamn = "Sotning",
                                datum = LocalDate.of(2026, 3, 1),
                                uppskattadKostnadOre = null,
                            ),
                        ),
                    summaOre = 0L,
                    harOkandKostnad = true,
                ),
            )
        val texter =
            KommandeUnderhallCsvTexter(
                kolumnAr = "Y",
                kolumnUppgift = "U",
                kolumnDatum = "D",
                kolumnKostnad = "K",
                arSummaRubrik = { "S$it" },
                okandKostnad = "Okänd",
            )
        val csv =
            kommandeUnderhallCsv(
                grupper = grupper,
                texter = texter,
                datumEtikett = { it.toString() },
                kostnadEtikett = { "" },
            )
        assertTrue(csv.contains("Okänd"))
    }
}
