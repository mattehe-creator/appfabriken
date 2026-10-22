package se.tmconnect.styrelserummet.underlag

import org.junit.Assert.assertEquals
import org.junit.Test

class TolkaAtgarderTest {
    @Test
    fun lasesOchOrimligaRaderHoppasOver() {
        val json = """
            {"komponenter":[],"anmarkningar":[],
             "atgarder":[
               {"ar":2028,"belopp":600000,"beskrivning":"Byte av fönster"},
               {"ar":"2030","belopp":"200 000","beskrivning":"Ventilation"},
               {"ar":2031,"belopp":null},
               {"ar":1800,"belopp":5},
               {"ar":2029,"belopp":-1},
               "inte ett objekt"
             ]}
        """.trimIndent()
        val r = tolkaForslag(json, "plan.pdf")
        assertEquals(
            listOf(ForslagAtgard(2028, 600_000, "Byte av fönster"), ForslagAtgard(2030, 200_000, "Ventilation")),
            r.atgarder,
        )
    }

    @Test
    fun saknasFaltetBlirTomLista() {
        assertEquals(emptyList<ForslagAtgard>(), tolkaForslag("""{"komponenter":[],"anmarkningar":[]}""", "p").atgarder)
    }
}
