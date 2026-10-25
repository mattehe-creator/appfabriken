package se.tmconnect.rostfangst.export

import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.rostfangst.data.FangstRad
import se.tmconnect.rostfangst.regler.ForslagTyp

class HistorikCsvTest {

    @Test
    fun `tom lista ger bara header`() {
        val resultat = historikSomCsv(emptyList())
        assertEquals("tid,typ,titel,original,plats,svar\n", resultat)
    }

    @Test
    fun `en rad ger header och rad`() {
        val rad = FangstRad(
            id = 1,
            originalText = "text",
            typ = ForslagTyp.FORSLAG,
            titel = "titel",
            plats = "plats",
            svar = "svar",
            skapadIso = "2023-01-01T00:00:00Z"
        )
        val resultat = historikSomCsv(listOf(rad))
        assertEquals("tid,typ,titel,original,plats,svar\n2023-01-01T00:00:00Z,FORSLAG,titel,text,plats,svar\n", resultat)
    }

    @Test
    fun `fält med kommatecken citeras`() {
        val rad = FangstRad(
            id = 1,
            originalText = "text",
            typ = ForslagTyp.FORSLAG,
            titel = "Hej, världen",
            plats = null,
            svar = null,
            skapadIso = "2023-01-01T00:00:00Z"
        )
        val resultat = historikSomCsv(listOf(rad))
        assertEquals("tid,typ,titel,original,plats,svar\n2023-01-01T00:00:00Z,FORSLAG,\"Hej, världen\",text,,\n", resultat)
    }

    @Test
    fun `fält med citattecken citeras`() {
        val rad = FangstRad(
            id = 1,
            originalText = "text",
            typ = ForslagTyp.FORSLAG,
            titel = "Hej \"världen\"",
            plats = null,
            svar = null,
            skapadIso = "2023-01-01T00:00:00Z"
        )
        val resultat = historikSomCsv(listOf(rad))
        assertEquals("tid,typ,titel,original,plats,svar\n2023-01-01T00:00:00Z,FORSLAG,\"Hej \"\"världen\"\"\",text,,\n", resultat)
    }

    @Test
    fun `fält med radbrytning citeras`() {
        val rad = FangstRad(
            id = 1,
            originalText = "text",
            typ = ForslagTyp.FORSLAG,
            titel = "Hej\nvärlden",
            plats = null,
            svar = null,
            skapadIso = "2023-01-01T00:00:00Z"
        )
        val resultat = historikSomCsv(listOf(rad))
        assertEquals("tid,typ,titel,original,plats,svar\n2023-01-01T00:00:00Z,FORSLAG,\"Hej\nvärlden\",text,,\n", resultat)
    }
}
