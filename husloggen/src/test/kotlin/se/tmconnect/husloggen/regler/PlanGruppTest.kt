package se.tmconnect.husloggen.regler

import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.husloggen.data.PlaneradKostnad

class PlanGruppTest {
    @Test
    fun summaPerAr2028() {
        val kostnader =
            listOf(
                PlaneradKostnad(id = 1, titel = "Tak", beloppOre = 4_000_000L, planeratAr = 2028),
                PlaneradKostnad(id = 2, titel = "Panna", beloppOre = 1_500_000L, planeratAr = 2028),
            )
        val grupper = grupperaPlaneradeKostnaderPerAr(kostnader)
        assertEquals(1, grupper.size)
        assertEquals(2028, grupper.single().ar)
        assertEquals(5_500_000L, grupper.single().summaOre)
    }
}
