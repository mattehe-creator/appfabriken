package se.tmconnect.rostfangst.export

import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.rostfangst.data.FangstRad
import se.tmconnect.rostfangst.regler.ForslagTyp

class HistorikCsvTest {

    private fun rad(titel: String, plats: String? = null, svar: String? = null) = FangstRad(
        id = 1,
        originalText = "text",
        typ = ForslagTyp.PAMINNELSE,
        titel = titel,
        plats = plats,
        svar = svar,
        skapadIso = "2023-01-01T00:00:00Z"
    )

    @Test
    fun `tom lista ger bara header`() {
        assertEquals("tid,typ,titel,original,plats,svar\n", historikSomCsv(emptyList()))
    }

    @Test
    fun `en rad ger header och rad`() {
        val resultat = historikSomCsv(listOf(rad("titel", "plats", "svar")))
        assertEquals("tid,typ,titel,original,plats,svar\n2023-01-01T00:00:00Z,PAMINNELSE,titel,text,plats,svar\n", resultat)
    }

    @Test
    fun `falt med kommatecken citeras`() {
        val resultat = historikSomCsv(listOf(rad("Hej, världen")))
        assertEquals("tid,typ,titel,original,plats,svar\n2023-01-01T00:00:00Z,PAMINNELSE,\"Hej, världen\",text,,\n", resultat)
    }

    @Test
    fun `falt med citattecken citeras och dubblas`() {
        val resultat = historikSomCsv(listOf(rad("Hej \"världen\"")))
        assertEquals("tid,typ,titel,original,plats,svar\n2023-01-01T00:00:00Z,PAMINNELSE,\"Hej \"\"världen\"\"\",text,,\n", resultat)
    }

    @Test
    fun `falt med radbrytning citeras`() {
        val resultat = historikSomCsv(listOf(rad("Hej\nvärlden")))
        assertEquals("tid,typ,titel,original,plats,svar\n2023-01-01T00:00:00Z,PAMINNELSE,\"Hej\nvärlden\",text,,\n", resultat)
    }
}
