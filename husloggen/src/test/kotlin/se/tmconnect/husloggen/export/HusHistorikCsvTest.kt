package se.tmconnect.husloggen.export

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.PostTyp
import java.time.LocalDate

class HusHistorikCsvTest {
    @Test
    fun csvInnehallerPosterOchSumma() {
        val poster = listOf(
            Post(datum = LocalDate.of(2025, 5, 1), vad = "Målning", typ = PostTyp.UNDERHALL, beloppOre = 500_000L),
        )
        val data = byggHusHistorik(
            poster = poster,
            forsaljningsar = 2026,
            localeLand = "SE",
            typEtikett = { "Typ" },
            datumEtikett = { it.datum.toString() },
            beloppEtikett = { "100 kr" },
            forbattringEtikett = { "Ja" },
            gransJa = "Ja grans",
            gransNej = "Nej grans",
        )
        val texter = HusHistorikTexter(
            rubrik = "H",
            kolumnDatum = "Datum",
            kolumnBeskrivning = "Beskrivning",
            kolumnTyp = "Typ",
            kolumnBelopp = "Belopp",
            kolumnForbattring = "Förbättring",
            arSummaRubrik = { "År $it" },
            typSummaRad = { t, b -> "$t: $b" },
            totalt = "Totalt",
            gransJa = "Ja",
            gransNej = "Nej",
            skattRubrik = "Skatt",
            skattText = "Info",
            kallorRubrik = "Källa",
            kallorText = "Skatteverket",
        )
        val csv = husHistorikCsv(data, texter)
        assertTrue(csv.contains("Målning"))
        assertTrue(csv.contains("5000.00"))
    }

    @Test
    fun csvUtanSkattKolumnUtanforSverige() {
        val poster = listOf(
            Post(datum = LocalDate.of(2025, 5, 1), vad = "X", typ = PostTyp.UNDERHALL, beloppOre = 1_00L),
        )
        val data = byggHusHistorik(
            poster = poster,
            forsaljningsar = 2026,
            localeLand = "US",
            typEtikett = { "T" },
            datumEtikett = { it.datum.toString() },
            beloppEtikett = { "" },
            forbattringEtikett = { "" },
            gransJa = "",
            gransNej = "",
        )
        assertFalse(data.inkluderaSkatteAvsnitt)
    }
}
