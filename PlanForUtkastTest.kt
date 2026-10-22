package se.tmconnect.styrelserummet.server

import kotlin.test.Test
import kotlin.test.assertEquals

class PlanForUtkastTest {

    @Test
    fun `test planForUtkast with example data`() {
        val utkast = UtkastDto(
            forening = "Testförening",
            arsslut = 2025,
            byggnadensVarde = 1000000,
            komponenter = listOf(
                KomponentDto(
                    namn = "A",
                    grupp = "BYGGNAD",
                    anskaffningsvarde = 200000,
                    anskaffningsar = 2010,
                    nyttjandeperiodAr = 40,
                    restvarde = 0,
                    kalla = "Testkälla",
                    sakerhet = "HOGER"
                ),
                KomponentDto(
                    namn = "B",
                    grupp = "BYGGNAD",
                    anskaffningsvarde = 300000,
                    anskaffningsar = 2020,
                    nyttjandeperiodAr = 10,
                    restvarde = 0,
                    kalla = "Testkälla",
                    sakerhet = "HOGER"
                )
            ),
            avstamning = AvstamningDto(
                summa = 0,
                byggnadensVarde = 1000000,
                differens = 0,
                stammer = false
            ),
            anmarkningar = emptyList()
        )

        val result = planForUtkast(utkast, 5)
        
        assertEquals(5, result.size)
        assertEquals(2026, result[0].ar)
        assertEquals(60000, result[0].avskrivning)
        assertEquals(780000, result[0].bokfortVarde)
        assertEquals(2027, result[1].ar)
        assertEquals(60000, result[1].avskrivning)
        assertEquals(720000, result[1].bokfortVarde)
        assertEquals(2028, result[2].ar)
        assertEquals(60000, result[2].avskrivning)
        assertEquals(660000, result[2].bokfortVarde)
        assertEquals(2029, result[3].ar)
        assertEquals(60000, result[3].avskrivning)
        assertEquals(600000, result[3].bokfortVarde)
        assertEquals(2030, result[4].ar)
        assertEquals(30000, result[4].avskrivning)
        assertEquals(570000, result[4].bokfortVarde)
    }

    @Test
    fun `test planForUtkast with default parameters`() {
        val utkast = UtkastDto(
            forening = "Testförening",
            arsslut = 2025,
            byggnadensVarde = 1000000,
            komponenter = emptyList(),
            avstamning = AvstamningDto(
                summa = 0,
                byggnadensVarde = 1000000,
                differens = 0,
                stammer = false
            ),
            anmarkningar = emptyList()
        )

        val result = planForUtkast(utkast)
        
        assertEquals(10, result.size)
        assertEquals(2026, result[0].ar)
        assertEquals(0, result[0].avskrivning)
        assertEquals(0, result[0].bokfortVarde)
        assertEquals(2035, result[9].ar)
        assertEquals(0, result[9].avskrivning)
        assertEquals(0, result[9].bokfortVarde)
    }
}
