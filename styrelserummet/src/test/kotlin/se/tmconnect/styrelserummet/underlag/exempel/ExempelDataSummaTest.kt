package se.tmconnect.styrelserummet.underlag.exempel

import org.junit.Assert
import org.junit.Test

class ExempelDataSummaTest {

    @Test
    fun summaOchByggnadsvVardeStammer() {
        Assert.assertEquals(14, EXEMPEL_ATGARDER.size)

        Assert.assertEquals(
            1,
            EXEMPEL_ATGARDER.count { it.kostnadKronor == null },
        )

        val balkonger = EXEMPEL_ATGARDER.first { it.namn == "Balkonger" }
        Assert.assertNull(balkonger.kostnadKronor)

        Assert.assertEquals(BYGGNADENS_VARDE, summaAtgarderMedKostnad())

        val atgardsnamn = EXEMPEL_ATGARDER.map { it.namn }
        Assert.assertTrue(atgardsnamn.any { it.contains("tak", ignoreCase = true) })
        Assert.assertTrue(atgardsnamn.any { it.contains("fasad", ignoreCase = true) })
        Assert.assertTrue(atgardsnamn.any { it.contains("fönster", ignoreCase = true) })
        Assert.assertTrue(atgardsnamn.any { it.contains("stammar", ignoreCase = true) })
        Assert.assertTrue(atgardsnamn.any { it.contains("el", ignoreCase = true) })
        Assert.assertTrue(atgardsnamn.any { it.contains("värm", ignoreCase = true) })
        Assert.assertTrue(atgardsnamn.any { it.contains("ventilation", ignoreCase = true) })
        Assert.assertTrue(atgardsnamn.any { it.contains("hiss", ignoreCase = true) })
        Assert.assertTrue(atgardsnamn.any { it.contains("tvättstuga", ignoreCase = true) })
        Assert.assertTrue(atgardsnamn.any { it.contains("mark", ignoreCase = true) })
    }
}
