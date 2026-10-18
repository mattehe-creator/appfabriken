package se.tmconnect.flextid

import java.io.File
import java.util.regex.Pattern
import org.junit.Assert.assertEquals
import org.junit.Test

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
        val nycklar = mutableSetOf<String>()
        val stringMonster = Pattern.compile("""<string\s+name="([^"]+)"""")
        val stringMatcher = stringMonster.matcher(innehall)
        while (stringMatcher.find()) {
            nycklar.add(stringMatcher.group(1)!!)
        }
        return nycklar
    }
}
