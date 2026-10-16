package se.tmconnect.husloggen

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

    @Test
    fun forslagsNamnFinnsPaBadaSprak() {
        val modulRot = File("src/main/res")
        val engelska = nycklar(modulRot.resolve("values/strings.xml"))
        val svenska = nycklar(modulRot.resolve("values-sv/strings.xml"))
        val forslag = engelska.filter { it.startsWith("forslag_") && it != "forslag_rubrik" }.toSet()
        assertEquals(forslag, svenska.filter { it.startsWith("forslag_") && it != "forslag_rubrik" }.toSet())
        assertEquals(8, forslag.size)
    }

    private fun nycklar(fil: File): Set<String> {
        val innehall = fil.readText()
        val nycklar = mutableSetOf<String>()
        val stringMonster = Pattern.compile("""<string\s+name="([^"]+)"""")
        val stringMatcher = stringMonster.matcher(innehall)
        while (stringMatcher.find()) {
            nycklar.add(stringMatcher.group(1)!!)
        }
        val pluralMonster = Pattern.compile("""<plurals\s+name="([^"]+)"""")
        val pluralMatcher = pluralMonster.matcher(innehall)
        while (pluralMatcher.find()) {
            nycklar.add(pluralMatcher.group(1)!!)
        }
        return nycklar
    }
}
