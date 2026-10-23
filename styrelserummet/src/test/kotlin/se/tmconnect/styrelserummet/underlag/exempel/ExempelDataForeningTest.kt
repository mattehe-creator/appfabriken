package se.tmconnect.styrelserummet.underlag.exempel

import org.junit.Assert
import org.junit.Test

class ExempelDataForeningTest {

    @Test
    fun foreningKonstanterArPahittade() {
        Assert.assertEquals("Brf Exempelgården", FORENING_NAMN)
        Assert.assertEquals("000000-0000", ORGANISATIONSNUMMER)
        Assert.assertEquals("Exempelvägen 1, 100 00 Teststad", ADRESS)
        Assert.assertEquals(1974, BYGGNADSAR)
        Assert.assertEquals(2025, RAKENSKAPSAR)
        Assert.assertTrue(ORGANISATIONSNUMMER.all { it == '0' || it == '-' })

        Assert.assertEquals("Brf Exempelgården", exempelForening().namn)
        Assert.assertEquals(1974, exempelForening().byggnadsar)
    }
}
