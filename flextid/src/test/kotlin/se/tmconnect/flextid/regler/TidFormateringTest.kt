package se.tmconnect.flextid.regler

import java.time.LocalTime
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TidFormateringTest {
    @Test
    fun minuterTillLocalTime_exempel510() {
        assertEquals(LocalTime.of(8, 30), minuterTillLocalTime(510))
    }

    @Test
    fun localTimeTillMinuter_exempel1645() {
        assertEquals(1005, localTimeTillMinuter(LocalTime.of(16, 45)))
    }

    @Test
    fun formateraTid_nullGerTomStrang() {
        assertEquals("", formateraTid(null, Locale.ROOT))
    }

    @Test
    fun formateraTid_exempel0805() {
        assertEquals("08:05", formateraTid(LocalTime.of(8, 5), Locale.ROOT))
    }

    @Test
    fun formateraMinuterSomTid_negativ75() {
        assertEquals("-1:15", formateraMinuterSomTid(-75, Locale.ROOT))
    }

    @Test
    fun formateraMinuterSomTid_noll() {
        assertEquals("0:00", formateraMinuterSomTid(0, Locale.ROOT))
    }

    @Test
    fun formateraMinuterSomTid_605() {
        assertEquals("10:05", formateraMinuterSomTid(605, Locale.ROOT))
    }

    @Test
    fun formateraSaldoMinuter_positiv75() {
        assertEquals("+1:15", formateraSaldoMinuter(75, Locale.ROOT))
    }

    @Test
    fun formateraSaldoMinuter_negativ75() {
        assertEquals("-1:15", formateraSaldoMinuter(-75, Locale.ROOT))
    }

    @Test
    fun formateraSaldoMinuter_noll() {
        assertEquals("0:00", formateraSaldoMinuter(0, Locale.ROOT))
    }

    @Test
    fun tolkaTidText_exempel805() {
        assertEquals(LocalTime.of(8, 5), tolkaTidText("8:05"))
    }

    @Test
    fun tolkaTidText_trimmarBlanksteg() {
        assertEquals(LocalTime.of(7, 30), tolkaTidText(" 07:30 "))
    }

    @Test
    fun tolkaTidText_2400GerNull() {
        assertNull(tolkaTidText("24:00"))
    }

    @Test
    fun tolkaTidText_baraTimmeGerNull() {
        assertNull(tolkaTidText("8"))
    }

    @Test
    fun tolkaTidText_tomGerNull() {
        assertNull(tolkaTidText(""))
    }
}
