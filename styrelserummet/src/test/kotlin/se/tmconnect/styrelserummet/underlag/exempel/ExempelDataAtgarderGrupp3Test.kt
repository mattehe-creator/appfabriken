package se.tmconnect.styrelserummet.underlag.exempel

import org.junit.Assert
import org.junit.Test

class ExempelDataAtgarderGrupp3Test {

    @Test
    fun grupp3TackerTvattMarkEntreGarage() {
        Assert.assertEquals(14, EXEMPEL_ATGARDER.size)

        val tvattstuga = EXEMPEL_ATGARDER.first { it.namn == "Tvättstuga" }
        Assert.assertEquals(2033, tvattstuga.ar)
        Assert.assertEquals(1_200_000L, tvattstuga.kostnadKronor)

        val mark = EXEMPEL_ATGARDER.first { it.namn == "Mark och gård" }
        Assert.assertEquals(2035, mark.ar)
        Assert.assertEquals(1_000_000L, mark.kostnadKronor)

        val entreparti = EXEMPEL_ATGARDER.first { it.namn == "Entréparti" }
        Assert.assertEquals(2037, entreparti.ar)
        Assert.assertEquals(800_000L, entreparti.kostnadKronor)

        val garageport = EXEMPEL_ATGARDER.first { it.namn == "Garageport" }
        Assert.assertEquals(2039, garageport.ar)
        Assert.assertEquals(500_000L, garageport.kostnadKronor)

        Assert.assertEquals(26_800_000L, EXEMPEL_ATGARDER.take(12).sumOf { it.kostnadKronor!! })
    }
}
