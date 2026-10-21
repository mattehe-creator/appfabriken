package se.tmconnect.styrelserummet.server

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.lang.reflect.Modifier

class FelkoderTest {
    @Test
    fun kartlaggning() {
        assertTrue(Felkoder.FOR_MANGA_JOBB.isNotEmpty())
        assertEquals("FOR_MANGA_JOBB", Felkoder.FOR_MANGA_JOBB)

        val alla = Felkoder.allaKoder()
        assertTrue(alla.size >= 15)
        assertEquals(alla.size, alla.toSet().size)

        val franKonstanter = Felkoder::class.java.declaredFields
            .filter { Modifier.isStatic(it.modifiers) && it.type == String::class.java }
            .map { it.get(null) as String }
            .toSet()
        assertEquals(franKonstanter, alla)
    }
}
