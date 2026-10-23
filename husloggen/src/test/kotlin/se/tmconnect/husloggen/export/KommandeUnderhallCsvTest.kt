package se.tmconnect.husloggen.export

import java.time.LocalDate
import java.util.Locale
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.husloggen.regler.KommandeUnderhallArGrupp
import se.tmconnect.husloggen.regler.KommandeUnderhallRad
import se.tmconnect.karna.format.formateraDatum

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
                                namn = "Filter",
                                datum = LocalDate.of(2027, 3, 15),
                                kostnadOre = 120_000L,
                            ),
                        ),
                    summaOre = 120_000L,
                    saknarKostnad = false,
                ),
            )
        val locale = Locale.forLanguageTag("sv-SE")
        val texter =
            KommandeUnderhallCsvTexter(
                kolumnAr = "År",
                kolumnUppgift = "Uppgift",
                kolumnDatum = "Datum",
                kolumnKostnad = "Uppskattad kostnad",
                arSummaRubrik = { ar -> "Summa $ar" },
                kostnadOkand = "Okänd",
            )

        val csv =
            kommandeUnderhallCsv(
                grupper,
                texter,
                datumEtikett = { formateraDatum(it, locale) },
            )

        assertTrue(csv.contains("Filter"))
        assertTrue(csv.contains("1200.00"))
        assertTrue(csv.contains("Summa 2027"))
    }

    @Test
    fun filnamnFormat() {
        assertTrue(
            kommandeUnderhallCsvFilnamn(LocalDate.of(2026, 10, 23))
                .startsWith("kommande-underhall-2026-10-23"),
        )
    }
}
