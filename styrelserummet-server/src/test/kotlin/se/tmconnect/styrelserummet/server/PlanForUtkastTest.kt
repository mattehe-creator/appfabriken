package se.tmconnect.styrelserummet.server

import org.junit.Assert.assertEquals
import org.junit.Test

class PlanForUtkastTest {

    private fun exempelUtkast(komponenter: List<KomponentDto>) =
        UtkastDto(
            forening = "Testförening",
            arsslut = 2025,
            byggnadensVarde = 1_000_000,
            komponenter = komponenter,
            avstamning = AvstamningDto(
                summa = 0,
                byggnadensVarde = 1_000_000,
                differens = 0,
                stammer = false,
            ),
            anmarkningar = emptyList(),
        )

    private fun exempelKomponenter() =
        listOf(
            KomponentDto(
                namn = "A",
                grupp = "BYGGNAD",
                anskaffningsvarde = 1_200_000,
                anskaffningsar = 2010,
                nyttjandeperiodAr = 40,
                restvarde = 0,
                kalla = "Testkälla",
                sakerhet = "HOGER",
            ),
            KomponentDto(
                namn = "B",
                grupp = "BYGGNAD",
                anskaffningsvarde = 300_000,
                anskaffningsar = 2020,
                nyttjandeperiodAr = 10,
                restvarde = 0,
                kalla = "Testkälla",
                sakerhet = "HOGER",
            ),
        )

    @Test
    fun planForUtkastFemAr() {
        val utkast = exempelUtkast(exempelKomponenter())
        val result = planForUtkast(utkast, 5)

        assertEquals(5, result.size)
        assertEquals(2026, result[0].ar)
        assertEquals(60_000, result[0].avskrivning)
        assertEquals(780_000, result[0].bokfortVarde)
        assertEquals(2027, result[1].ar)
        assertEquals(60_000, result[1].avskrivning)
        assertEquals(720_000, result[1].bokfortVarde)
        assertEquals(2028, result[2].ar)
        assertEquals(60_000, result[2].avskrivning)
        assertEquals(660_000, result[2].bokfortVarde)
        assertEquals(2029, result[3].ar)
        assertEquals(60_000, result[3].avskrivning)
        assertEquals(600_000, result[3].bokfortVarde)
        assertEquals(2030, result[4].ar)
        assertEquals(30_000, result[4].avskrivning)
        assertEquals(570_000, result[4].bokfortVarde)
    }

    @Test
    fun planForUtkastStandardAntalAr() {
        val utkast = exempelUtkast(exempelKomponenter())
        val result = planForUtkast(utkast)

        assertEquals(10, result.size)
        assertEquals(2026, result.first().ar)
        assertEquals(2035, result.last().ar)
    }

    @Test
    fun planForUtkastUtanKomponenter() {
        val utkast = exempelUtkast(emptyList())
        val result = planForUtkast(utkast, 2)

        assertEquals(2, result.size)
        assertEquals(2026, result[0].ar)
        assertEquals(0, result[0].avskrivning)
        assertEquals(0, result[0].bokfortVarde)
        assertEquals(2027, result[1].ar)
        assertEquals(0, result[1].avskrivning)
        assertEquals(0, result[1].bokfortVarde)
    }
}
