package se.tmconnect.styrelserummet.underlag.exempel

import org.junit.Assert
import org.junit.Test

class ExempelDataForeningTest {

    @Test
    fun foreningKonstanterArPahittade() {
        Assert.assertEquals("Brf Exempelgården", FORENING_NAMN)
        Assert.assertEquals("0000000000", ORGANISATIONSNUMMER)
        Assert.assertEquals("Exempelvägen 1, 100 00 Teststad", ADRESS)
        Assert.assertEquals(1974, BYGGNADSAR)
        Assert.assertEquals(2025, RAKENSKAPSAR)
        
        // Kontrollera att organisationsnumret inte liknar riktigt format
        Assert.assertFalse(ORGANISATIONSNUMMER.matches(Regex("\\d{6}-\\d{4}")))
    }
}
