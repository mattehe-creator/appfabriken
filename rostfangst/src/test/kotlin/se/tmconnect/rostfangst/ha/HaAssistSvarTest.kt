package se.tmconnect.rostfangst.ha

import org.junit.Assert.assertEquals
import org.junit.Test

class HaAssistSvarTest {
    @Test
    fun giltigJson() {
        val json = """{"response":{"speech":{"plain":{"speech":"Lampan i vardagsrummet är tänd."}}}}"""
        val result = lasAssistSvar(json)
        assertEquals("Lampan i vardagsrummet är tänd.", result)
    }

    @Test
    fun ogiltigJsonBlirNull() {
        val result = lasAssistSvar("inte json")
        assertEquals(null, result)
    }

    @Test
    fun saknadSpeechPlainsBlirNull() {
        val json = """{"response":{"speech":{}}}"""
        val result = lasAssistSvar(json)
        assertEquals(null, result)
    }

    @Test
    fun tomTextBlirNull() {
        val json = """{"response":{"speech":{"plain":{"speech":"  "}}}}"""
        val result = lasAssistSvar(json)
        assertEquals(null, result)
    }
}
