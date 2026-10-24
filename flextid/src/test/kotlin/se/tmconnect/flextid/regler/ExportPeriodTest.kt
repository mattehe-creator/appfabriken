package se.tmconnect.flextid.regler

import java.time.YearMonth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ExportPeriodTest {
    @Test
    fun giltigtArOchManad() {
        assertEquals(YearMonth.of(2026, 3), tolkaExportArManad("2026", "3"))
        assertEquals(YearMonth.of(2025, 12), tolkaExportArManad(" 2025 ", "12"))
    }

    @Test
    fun manad13Och0GerNull() {
        assertNull(tolkaExportArManad("2026", "13"))
        assertNull(tolkaExportArManad("2026", "0"))
    }

    @Test
    fun ar0OchForStortArGerNull() {
        assertNull(tolkaExportArManad("0", "1"))
        // YearMonth.of kraschar över 999 999 999; fältet tog emot sådana värden.
        assertNull(tolkaExportArManad("1000000000", "1"))
        assertNull(tolkaExportArManad("10000", "1"))
    }

    @Test
    fun tomtFaltGerNull() {
        assertNull(tolkaExportArManad("", "5"))
        assertNull(tolkaExportArManad("2026", ""))
    }
}
