package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class RantescenarioTest {
    private val s = 38_000_000L
    private val a = 4_200_000L

    @Test
    fun steg() {
        assertEquals(190_000L, extraRantekostnadPerAr(s, 5))
        assertEquals(3L, extraPerKvmOchManad(190_000, 5_200))
        assertEquals(45L, avgiftshojningTiondelsProcent(s, a, 5))
        assertEquals(1_140_000L, extraRantekostnadPerAr(s, 30))
        assertEquals(18L, extraPerKvmOchManad(1_140_000, 5_200))
        assertEquals(271L, avgiftshojningTiondelsProcent(s, a, 30))
        assertNull(extraPerKvmOchManad(1, 0))
        assertNull(avgiftshojningTiondelsProcent(s, 0, 10))
        assertThrows(IllegalArgumentException::class.java) { extraRantekostnadPerAr(s, 201) }
    }

    @Test
    fun rader() {
        val r = rantescenarioRader(s, a, 5_200)
        assertEquals(RantescenarioRad("+0,5 procentenhet", "190 000 kr", "3 kr/kvm/mån", "+4,5 %"), r[0])
        assertEquals(RantescenarioRad("+1,0 procentenhet", "380 000 kr", "6 kr/kvm/mån", "+9,0 %"), r[1])
        assertEquals(RantescenarioRad("+3,0 procentenheter", "1 140 000 kr", "18 kr/kvm/mån", "+27,1 %"), r[3])
        assertEquals("–", rantescenarioRader(s, 0, 5_200)[0].avgiftshojning)
    }
}
