package se.tmconnect.rostfangst.ha

import org.junit.Assert.assertEquals
import org.junit.Test

class HaAssistSvarTest {
    @Test
    fun giltigJson() {
        val json = """{"response":{"speech":{"plain":{"speech":"Lampan i vardagsrummet är tänd."}}}}"""
        val svar = lasAssistSvar(json)
        assertEquals("Lampan i vardagsrummet är tänd.", svar)
    }

    @Test
    fun ogiltigJson() {
        val svar = lasAssistSvar("inte json")
        assertEquals(null, svar)
    }

    @Test
    fun saknarResponse() {
        val json = """{"annat":"värde"}"""
        val svar = lasAssistSvar(json)
        assertEquals(null, svar)
    }

    @Test
    fun saknarSpeech() {
        val json = """{"response":{"annat":"värde"}}"""
        val svar = lasAssistSvar(json)
        assertEquals(null, svar)
    }

    @Test
    fun saknarPlain() {
        val json = """{"response":{"speech":{}}}"""
        val svar = lasAssistSvar(json)
        assertEquals(null, svar)
    }

    @Test
    fun tomText() {
        val json = """{"response":{"speech":{"plain":{"speech":"  "}}}}"""
        val svar = lasAssistSvar(json)
        assertEquals(null, svar)
    }
}
