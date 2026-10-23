package se.tmconnect.styrelserummet.underlag.exempel

import org.junit.Assert
import org.junit.Test

class ExempelTexterPlanTest {

    @Test
    fun underhallsplanHarTreSidorOchTabell() {
        val sidor = underhallsplanSidor()

        Assert.assertEquals(3, sidor.size)

        val sida1 = sidor[0]
        Assert.assertTrue(sida1.contains(FORENING_NAMN))
        Assert.assertTrue(sida1.contains(ORGANISATIONSNUMMER))

        val sida2 = sidor[1]
        Assert.assertTrue(sida2.contains("Åtgärd"))
        Assert.assertTrue(sida2.contains("Yttertak"))
        Assert.assertTrue(sida2.contains("Balkonger"))
        Assert.assertTrue(sida2.contains("5 000 000"))

        val sida3 = sidor[2]
        Assert.assertTrue(sida3.contains("tkr", ignoreCase = true))
    }
}
