package se.tmconnect.rostfangst.lokal

import org.junit.Test
import org.junit.Assert.*

class SvarTest {

    @Test
    fun testLasTranskription() {
        assertEquals("påminn mig", lasTranskription("{\"text\": \" påminn mig \"}"))
        assertNull(lasTranskription("{\"text\": \"  \"}"))
        assertNull(lasTranskription("inte json"))
    }

    @Test
    fun testLasOllamaSvar() {
        assertEquals("""{"typ":"ANTECKNING"}""", lasOllamaSvar("{\"model\":\"m\",\"message\":{\"role\":\"assistant\",\"content\":\"{\\\"typ\\\":\\\"ANTECKNING\\\"}\"},\"done\":true}"))
        assertNull(lasOllamaSvar("{\"message\":{}}"))
    }
}
