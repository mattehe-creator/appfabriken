package se.tmconnect.karna.bild

import org.junit.Test
import kotlin.test.assertEquals

class BildSkalningTest {

    @Test
    fun testBeraknaNedskaladStorlek() {
        // 4000×3000 → 2000×1500
        assertEquals(2000 to 1500, beraknaNedskaladStorlek(4000, 3000))

        // 1000×800 → 1000×800
        assertEquals(1000 to 800, beraknaNedskaladStorlek(1000, 800))

        // 3000×3000 → 2000×2000
        assertEquals(2000 to 2000, beraknaNedskaladStorlek(3000, 3000))

        // 0×500 → 0×500 (ogiltiga mått oförändrade)
        assertEquals(0 to 500, beraknaNedskaladStorlek(0, 500))
        assertEquals(500 to 0, beraknaNedskaladStorlek(500, 0))
    }

    @Test
    fun testBeraknaInSampleSize() {
        // beraknaInSampleSize(1000, 800) → 1
        assertEquals(1, beraknaInSampleSize(1000, 800))

        // beraknaInSampleSize(4000, 3000) → 2
        assertEquals(2, beraknaInSampleSize(4000, 3000))

        // beraknaInSampleSize(8000, 6000) → 4
        assertEquals(4, beraknaInSampleSize(8000, 6000))
    }
}
