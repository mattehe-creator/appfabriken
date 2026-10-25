package se.tmconnect.rostfangst.export

import org.junit.jupiter.api.Test
import se.tmconnect.rostfangst.data.FangstRad
import se.tmconnect.rostfangst.regler.ForslagTyp
import java.time.Instant
import java.time.ZoneOffset

class HistorikCsvTest {

    @Test
    fun `skriver ut tom lista som csv`() {
        val rader = emptyList<FangstRad>()
        val resultat = historikSomCsv(rader)
        assert(resultat == "tid,typ,titel,original,plats,svar\n")
    }

    @Test
    fun `skriver ut en rad som csv`() {
        val rad = FangstRad(
            id = 1,
            originalText = "Hej världen",
            typ = ForslagTyp.FORSLAG,
            titel = "Forslagsrubrik",
            plats = null,
            svar = null,
            skapadIso = "2023-01-01T00:00:00Z"
        )
        val resultat = historikSomCsv(listOf(rad))
        assert(resultat == """tid,typ,titel,original,plats,svar
"2023-01-01T00:00:00Z",FORSLAG,Forslagsrubrik,Hej världen,,"
""")
    }

    @Test
    fun `skriver ut rad med kommatecken som csv`() {
        val rad = FangstRad(
            id = 1,
            originalText = "Hej, världen",
            typ = ForslagTyp.FORSLAG,
            titel = "Forslagsrubrik",
            plats = null,
            svar = null,
            skapadIso = "2023-01-01T00:00:00Z"
        )
        val resultat = historikSomCsv(listOf(rad))
        assert(resultat == """tid,typ,titel,original,plats,svar
"2023-01-01T00:00:00Z",FORSLAG,Forslagsrubrik,"Hej, världen",,"
""")
    }
}
