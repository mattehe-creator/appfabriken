package se.tmconnect.styrelserummet.underlag.exempel

import org.junit.Assert
import org.junit.Test
import se.tmconnect.styrelserummet.underlag.hittaByggnadensVarde

class ExempelTexterArTest {

    @Test
    fun arsredovisningInnehallerByggnaderOchMark() {
        val sammanslagenText = arsredovisningSidor().joinToString("\n")

        Assert.assertTrue(sammanslagenText.contains("Byggnader och mark"))
        Assert.assertTrue(sammanslagenText.contains("27 000 000"))
        Assert.assertEquals(BYGGNADENS_VARDE, hittaByggnadensVarde(sammanslagenText)?.kronor)
    }
}
