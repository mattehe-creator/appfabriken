package se.tmconnect.flextid.butik

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ButikTexterTest {
    @Test
    fun butikstexterFinnsOchHallerGranser() {
        val butikRot = File("butik")
        val falt = listOf("titel.txt", "kort.txt", "lang.txt")
        val granser =
            mapOf(
                "titel.txt" to 30,
                "kort.txt" to 80,
                "lang.txt" to 4_000,
            )

        for (sprak in listOf("sv", "en")) {
            for (filnamn in falt) {
                val fil = butikRot.resolve("$sprak/$filnamn")
                assertTrue("Saknas: ${fil.path}", fil.isFile)
                val text = fil.readText().trim()
                assertTrue("Tom fil: ${fil.path}", text.isNotEmpty())
                val max = granser.getValue(filnamn)
                assertTrue(
                    "${fil.path} har ${text.length} tecken, max $max",
                    text.length <= max,
                )
                assertFalse("${fil.path} får inte innehålla http", text.contains("http", ignoreCase = true))
                assertFalse("${fil.path} får inte innehålla www", text.contains("www", ignoreCase = true))
            }
        }
    }
}
