package se.tmconnect.rostfangst.regler

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class TolkaSvarTest {
    @Test
    fun giltigJson() {
        val json = """{"typ":"ATT_GORA","titel":"Köp mjölk","start":null,"slut":null,"plats":null,"svar":null}"""
        val f = tolkaSvar(json, "original")
        assertEquals(ForslagTyp.ATT_GORA, f.typ)
        assertEquals("Köp mjölk", f.titel)
    }

    @Test
    fun ogiltigJsonBlirAnteckning() {
        val f = tolkaSvar("inte json", "min text")
        assertEquals(ForslagTyp.ANTECKNING, f.typ)
        assertEquals("min text", f.titel)
    }
}
