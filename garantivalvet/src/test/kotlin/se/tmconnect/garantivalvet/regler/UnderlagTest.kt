package se.tmconnect.garantivalvet.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.garantivalvet.data.Kop
import java.time.LocalDate
import java.util.Locale

class UnderlagTest {
    private val etiketter = UnderlagEtiketter(
        vad = "Vad",
        varKopt = "Var",
        kopdatum = "Köpdatum",
        pris = "Pris",
        garantiManader = "Garanti (mån)",
        garantiSlut = "Garanti till",
        reklamationSlut = "Reklamation till",
        presumtionSlut = "Presumtion till",
        reklamationStatus = "Status",
        statusFelAntasFunnits = "Fel antas funnits",
        statusGaller = "Gäller",
        statusUtgangen = "Utgången",
        anteckning = "Anteckning",
        skapad = "Skapad",
    )

    @Test
    fun allaFaltMedKvitto() {
        val kop = Kop(
            id = 1,
            vad = "Laptop",
            varKopt = "Elgiganten",
            kopdatum = LocalDate.of(2024, 3, 15),
            garantiManader = 24,
            prisOre = 129_950,
            anteckning = "Extended warranty card in box",
            kvittoFil = "kvitton/1.jpg",
        )
        val skapad = LocalDate.of(2026, 10, 11)
        val underlag = byggUnderlag(kop, skapad, etiketter, "Garantivalvet", Locale.forLanguageTag("sv-SE"))

        assertEquals("Garantivalvet", underlag.dokumentRubrik)
        assertEquals("kvitton/1.jpg", underlag.kvittoFil)
        assertEquals(skapad, underlag.skapad)

        val etiketterLista = underlag.rader.map { it.etikett }
        assertTrue(etiketterLista.containsAll(listOf("Vad", "Var", "Pris", "Anteckning")))
        assertTrue(underlag.rader.any { it.etikett == "Reklamation till" && it.varde.contains("2027") })
        assertTrue(underlag.rader.any { it.etikett == "Presumtion till" && it.varde.contains("2026") })
        assertEquals(
            "Gäller",
            underlag.rader.first { it.etikett == "Status" }.varde,
        )
    }

    @Test
    fun utelamnarTommaValfriaFalt() {
        val kop = Kop(
            vad = "Mus",
            kopdatum = LocalDate.of(2025, 1, 10),
            garantiManader = 12,
        )
        val underlag = byggUnderlag(
            kop,
            LocalDate.of(2026, 1, 1),
            etiketter,
            "App",
            Locale.forLanguageTag("en-GB"),
        )

        val etiketterLista = underlag.rader.map { it.etikett }
        assertFalse(etiketterLista.contains("Var"))
        assertFalse(etiketterLista.contains("Pris"))
        assertFalse(etiketterLista.contains("Anteckning"))
        assertTrue(etiketterLista.contains("Vad"))
    }

    @Test
    fun kopForeKonsumentkopIngenReklamation() {
        val kop = Kop(
            vad = "Gammal sak",
            varKopt = "Butik",
            kopdatum = LocalDate.of(2022, 4, 30),
            garantiManader = 24,
            prisOre = 50_000,
        )
        val underlag = byggUnderlag(
            kop,
            LocalDate.of(2026, 6, 1),
            etiketter,
            "App",
        )

        val etiketterLista = underlag.rader.map { it.etikett }
        assertFalse(etiketterLista.contains("Reklamation till"))
        assertFalse(etiketterLista.contains("Presumtion till"))
        assertFalse(etiketterLista.contains("Status"))
    }
}
