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
        val json = "{\"model\":\"m\",\"message\":{\"role\":\"assistant\",\"content\":\"{\\\"typ\\\":\\\"ANTECKNING\\\"}\"},\"done\":true}"
        assertEquals("{\"typ\":\"ANTECKNING\"}", lasOllamaSvar(json))
        assertNull(lasOllamaSvar("{\"message\":{}}"))
    }
}
