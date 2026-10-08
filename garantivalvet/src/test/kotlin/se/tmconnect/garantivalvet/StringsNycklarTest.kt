package se.tmconnect.garantivalvet

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File
import java.util.regex.Pattern

class StringsNycklarTest {
    @Test
    fun svenskaOchEngelskaHarSammaNycklar() {
        val modulRot = File("src/main/res")
        val engelska = modulRot.resolve("values/strings.xml")
        val svenska = modulRot.resolve("values-sv/strings.xml")
        assertEquals(nycklar(engelska), nycklar(svenska))
    }

    private fun nycklar(fil: File): Set<String> {
        val innehall = fil.readText()
        val monster = Pattern.compile("""<string\s+name="([^"]+)"""")
        val nycklar = mutableSetOf<String>()
        val matcher = monster.matcher(innehall)
        while (matcher.find()) {
            nycklar.add(matcher.group(1)!!)
        }
        return nycklar
    }
}
