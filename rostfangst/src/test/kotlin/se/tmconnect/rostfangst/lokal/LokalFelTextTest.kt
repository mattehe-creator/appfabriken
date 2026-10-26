package se.tmconnect.rostfangst.lokal

import org.junit.Test
import org.junit.Assert.*

class LokalFelTextTest {

    @Test
    fun testAllCombinations() {
        // TOLKNING tests
        assertEquals("Datorn svarade inte. Kontrollera att den är på och att adressen stämmer.", 
            lokalFelText(LokalFel.NATFEL, Kalla.TOLKNING))
        assertEquals("Datorn tog för lång tid på sig att svara.", 
            lokalFelText(LokalFel.TIMEOUT, Kalla.TOLKNING))
        assertEquals("Datorn gav ett svar som inte gick att tolka.", 
            lokalFelText(LokalFel.OGILTIGT_SVAR, Kalla.TOLKNING))
        
        // TALIGENKANNING tests
        assertEquals("Taligenkänningen på datorn svarade inte. Kontrollera att den är på och att adressen stämmer.", 
            lokalFelText(LokalFel.NATFEL, Kalla.TALIGENKANNING))
        assertEquals("Taligenkänningen på datorn tog för lång tid på sig.", 
            lokalFelText(LokalFel.TIMEOUT, Kalla.TALIGENKANNING))
        assertEquals("Taligenkänningen på datorn gav inget läsbart svar.", 
            lokalFelText(LokalFel.OGILTIGT_SVAR, Kalla.TALIGENKANNING))
    }
}
