package se.tmconnect.rostfangst.ha

import org.junit.Assert.assertEquals
import org.junit.Test

class HaFelTextTest {
    @Test
    fun test401() {
        assertEquals("Token nekas, skapa en ny i Home Assistant.", haFelText(401, null))
    }

    @Test
    fun test404() {
        assertEquals("Hittar inte Home Assistant på adressen.", haFelText(404, null))
    }

    @Test
    fun test5xx() {
        assertEquals("Home Assistant svarade med fel.", haFelText(503, null))
        assertEquals("Home Assistant svarade med fel.", haFelText(599, null))
    }

    @Test
    fun testNullStatusWithException() {
        assertEquals("Når inte Home Assistant just nu.", haFelText(null, "SocketTimeoutException: http://192.168.1.10"))
    }

    @Test
    fun testOtherCases() {
        assertEquals("Något gick fel mot Home Assistant.", haFelText(418, null))
        assertEquals("Något gick fel mot Home Assistant.", haFelText(null, null))
    }
}
