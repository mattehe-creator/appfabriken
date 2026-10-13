package se.tmconnect.garantivalvet.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.util.Locale

class AiPromptTest {
    private val texter = ReklamationsTexter(
        amneMall = "", hej = "", intro = "", vara = "Vara", butik = "Butik", kopdatum = "Köpdatum", pris = "Pris",
        kvittoBifogas = "", fel = "Fel", upptacktes = "Upptäcktes", reklamationsdatum = "Idag",
        kravMall = "Jag kräver: %1\$s", kravAvhjalp = "reparation", kravPrisavdrag = "prisavdrag", kravHava = "hävning",
        bekrafta = "", avslut = "", halsning = "",
    )
    private val sv = Locale.forLanguageTag("sv-SE")

    private fun bygg(butik: String? = "Elgiganten", fel: String = "Skärmen flimrar", pris: Long? = 499_900L) =
        byggAiPrompt(
            mall = "Hjälp mig.\n%1\$s\nSlut.",
            vad = "TV",
            butik = butik,
            kopdatum = LocalDate.of(2025, 3, 1),
            prisOre = pris,
            felBeskrivning = fel,
            felUpptackt = LocalDate.of(2026, 10, 1),
            krav = Krav.PRISAVDRAG,
            texter = texter,
            locale = sv,
            idag = LocalDate.of(2026, 10, 13),
        )

    @Test
    fun faktaRutanInnehallerKopetOchKravet() {
        val p = bygg()
        assertTrue(p.startsWith("Hjälp mig.\n- Vara: TV\n- Butik: Elgiganten\n- Köpdatum: "))
        assertTrue(p.contains("- Fel: Skärmen flimrar"))
        assertTrue(p.contains("- Jag kräver: prisavdrag"))
        assertTrue(p.endsWith("\nSlut."))
        assertFalse(p.contains("%1"))
    }

    @Test
    fun saknadeFaltUtelamnasUtanTommaRader() {
        val p = bygg(butik = null, fel = "…", pris = null)
        assertFalse(p.contains("Butik:"))
        assertFalse(p.contains("Pris:"))
        assertFalse(p.contains("Fel:"))
        assertEquals(0, p.lines().count { it.trim() == "-" })
    }
}
