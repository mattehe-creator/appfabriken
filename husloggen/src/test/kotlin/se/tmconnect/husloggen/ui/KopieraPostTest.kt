package se.tmconnect.husloggen.ui

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.PostTyp
import se.tmconnect.husloggen.regler.beloppOreTillFaltText

class KopieraPostTest {
    @Test
    fun kopianFarDagensDatumUtanBilderOchUppgift() {
        val post = Post(
            id = 12,
            datum = LocalDate.parse("2025-04-10"),
            vad = "Sotning",
            typ = PostTyp.REPARATION,
            beloppOre = 123_450,
            rotRutAvdragOre = 20_000,
            utforare = "Sotarn",
            anteckning = "Skorsten",
            bilder = "a.jpg",
            uppgiftId = 3,
        )
        val idag = LocalDate.parse("2026-10-22")
        val f = kopieraPostTillFormular(post, idag)
        assertEquals(0L, f.redigerarId)
        assertEquals(idag, f.datum)
        assertEquals("Sotning", f.vad)
        assertEquals(PostTyp.REPARATION, f.typ)
        assertEquals(beloppOreTillFaltText(123_450), f.beloppText)
        assertEquals(beloppOreTillFaltText(20_000), f.rotRutAvdragText)
        assertEquals("Sotarn", f.utforare)
        assertEquals("Skorsten", f.anteckning)
        assertNull(f.uppgiftId)
        assertTrue(f.bildFilnamn.isEmpty())
    }

    @Test
    fun tommaBeloppBlirTommaFalt() {
        val post = Post(datum = LocalDate.parse("2025-04-10"), vad = "Rensa rännor", typ = PostTyp.UNDERHALL)
        val f = kopieraPostTillFormular(post, LocalDate.parse("2026-10-22"))
        assertEquals("", f.beloppText)
        assertEquals("", f.rotRutAvdragText)
    }
}
