package se.tmconnect.garantivalvet.regler

import java.time.LocalDate
import org.junit.Test
import org.junit.Assert.*

class TidslinjeTest {

    @Test
    fun testBeraknaTidslinje() {
        // Testfall: Köp 2024-01-01, garanti 12 mån, idag 2024-06-01
        val kopdatum = LocalDate.of(2024, 1, 1)
        val idag = LocalDate.of(2024, 6, 1)
        val result = beraknaTidslinje(kopdatum, 12, idag)
        
        assertNotNull(result)
        assertEquals(0.33f, result!!.garantiSlutAndel, 0.02f)
        assertTrue(result.idagAndel in 0f..1f)
        assertTrue(result.idagAndel < result.garantiSlutAndel)
        assertTrue(result.garantiSlutAndel < result.presumtionSlutAndel)
    }

    @Test
    fun testBeraknaTidslinjeFöreIkraft() {
        // Köp före 2022-05-01 ska returnera null
        val kopdatum = LocalDate.of(2021, 1, 1)
        val idag = LocalDate.of(2024, 1, 1)
        val result = beraknaTidslinje(kopdatum, 12, idag)
        
        assertNull(result)
    }

    @Test
    fun testBeraknaTidslinjeIngenGaranti() {
        // Testa med noll månader garanti
        val kopdatum = LocalDate.of(2024, 1, 1)
        val idag = LocalDate.of(2024, 1, 1)
        val result = beraknaTidslinje(kopdatum, 0, idag)
        
        assertNotNull(result)
        // Garantins slut bör vara samma som köpdatum
        assertEquals(0f, result!!.garantiSlutAndel, 0.01f)
    }
}
