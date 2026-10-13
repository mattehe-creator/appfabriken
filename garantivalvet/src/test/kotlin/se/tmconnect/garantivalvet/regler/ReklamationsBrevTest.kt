package se.tmconnect.garantivalvet.regler

import java.time.LocalDate
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReklamationsBrevTest {
    private val sv = Locale.forLanguageTag("sv-SE")
    private val texter = ReklamationsTexter(
        amneMall = "Reklamation: %1\$s (köpt %2\$s)",
        hej = "Hej,",
        intro = "Jag reklamerar en vara som jag har köpt hos er, enligt konsumentköplagen.",
        vara = "Vara",
        butik = "Köpt hos",
        kopdatum = "Köpdatum",
        pris = "Pris",
        kvittoBifogas = "Kvittot är bifogat.",
        fel = "Fel",
        upptacktes = "Felet upptäcktes",
        reklamationsdatum = "Reklamationen skickas",
        kravMall = "Jag begär att ni %1\$s.",
        kravAvhjalp = "åtgärdar felet utan kostnad för mig, genom reparation eller en ny vara",
        kravPrisavdrag = "ger mig ett prisavdrag",
        kravHava = "hävar köpet och betalar tillbaka pengarna",
        bekrafta = "Vänligen bekräfta att ni har tagit emot reklamationen och meddela hur ni vill gå vidare.",
        avslut = "Jag ser fram emot ert svar.",
        halsning = "Med vänliga hälsningar",
    )

    private fun brev(krav: Krav, pris: Long? = 129_900L, kvitto: Boolean = true) =
        byggReklamationsbrev(
            vad = "Kaffebryggare",
            butik = "Elgiganten",
            kopdatum = LocalDate.of(2026, 9, 1),
            prisOre = pris,
            kvittoFinns = kvitto,
            felBeskrivning = "  Den läcker vatten  ",
            felUpptackt = LocalDate.of(2026, 10, 1),
            krav = krav,
            texter = texter,
            locale = sv,
            idag = LocalDate.of(2026, 10, 3),
        )

    @Test
    fun amneInnehallerVaraOchKopdatum() {
        assertEquals("Reklamation: Kaffebryggare (köpt ${formateraDatum(LocalDate.of(2026, 9, 1), sv)})", brev(Krav.AVHJALP).amne)
    }

    @Test
    fun textInnehallerAllaUppgifterIRattOrdning() {
        val rader = brev(Krav.AVHJALP).text.lines()
        assertEquals("Hej,", rader[0])
        assertTrue(rader.contains("Vara: Kaffebryggare"))
        assertTrue(rader.contains("Köpdatum: ${formateraDatum(LocalDate.of(2026, 9, 1), sv)}"))
        assertTrue(rader.any { it.startsWith("Pris: ") })
        assertTrue(rader.contains("Kvittot är bifogat."))
        assertTrue(rader.contains("Fel: Den läcker vatten"))
        assertTrue(rader.contains("Felet upptäcktes: ${formateraDatum(LocalDate.of(2026, 10, 1), sv)}"))
        assertTrue(rader.contains("Köpt hos: Elgiganten"))
        assertTrue(rader.contains("Reklamationen skickas: ${formateraDatum(LocalDate.of(2026, 10, 3), sv)}"))
        assertTrue(rader.contains("Vänligen bekräfta att ni har tagit emot reklamationen och meddela hur ni vill gå vidare."))
        // Slutar med hälsning och en tom rad där användaren skriver sitt namn i mejlappen.
        assertEquals("Med vänliga hälsningar", rader[rader.size - 2])
        assertEquals("", rader.last())
    }

    @Test
    fun kravetFarRattFormulering() {
        assertTrue(brev(Krav.AVHJALP).text.contains("Jag begär att ni åtgärdar felet utan kostnad för mig, genom reparation eller en ny vara."))
        assertTrue(brev(Krav.PRISAVDRAG).text.contains("Jag begär att ni ger mig ett prisavdrag."))
        assertTrue(brev(Krav.HAVA).text.contains("Jag begär att ni hävar köpet och betalar tillbaka pengarna."))
    }

    @Test
    fun butikUtelamnasNarDenSaknas() {
        val utan = byggReklamationsbrev(
            vad = "Kaffebryggare",
            butik = "  ",
            kopdatum = LocalDate.of(2026, 9, 1),
            prisOre = null,
            kvittoFinns = false,
            felBeskrivning = "Läcker",
            felUpptackt = LocalDate.of(2026, 10, 1),
            krav = Krav.AVHJALP,
            texter = texter,
            locale = sv,
            idag = LocalDate.of(2026, 10, 3),
        ).text
        assertFalse(utan.contains("Köpt hos"))
    }

    @Test
    fun priskadernaOchKvittoutanFalt() {
        val utan = brev(Krav.AVHJALP, pris = null, kvitto = false).text
        assertFalse(utan.contains("Pris:"))
        assertFalse(utan.contains("Kvittot är bifogat."))
    }
}
