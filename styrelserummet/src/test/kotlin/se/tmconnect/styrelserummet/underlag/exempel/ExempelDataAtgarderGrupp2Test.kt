package se.tmconnect.styrelserummet.underlag.exempel

import org.junit.Assert
import org.junit.Test

class ExempelDataAtgarderGrupp2Test {

    @Test
    fun grupp2TackerElVarmeVentilationHiss() {
        val elstammar = EXEMPEL_ATGARDER.first { it.namn == "Elstammar och elcentral" }
        Assert.assertEquals(2029, elstammar.ar)
        Assert.assertEquals(2_500_000L, elstammar.kostnadKronor)

        val fjarrvarmecentral = EXEMPEL_ATGARDER.first { it.namn == "Fjärrvärmecentral" }
        Assert.assertEquals(2027, fjarrvarmecentral.ar)
        Assert.assertEquals(2_000_000L, fjarrvarmecentral.kostnadKronor)

        val ventilation = EXEMPEL_ATGARDER.first { it.namn == "Ventilation FTX" }
        Assert.assertEquals(2034, ventilation.ar)
        Assert.assertEquals(1_800_000L, ventilation.kostnadKronor)

        val hiss = EXEMPEL_ATGARDER.first { it.namn == "Hiss" }
        Assert.assertEquals(2031, hiss.ar)
        Assert.assertEquals(1_500_000L, hiss.kostnadKronor)

        Assert.assertEquals(23_300_000L, EXEMPEL_ATGARDER.sumOf { it.kostnadKronor!! })
    }
}
