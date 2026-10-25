package se.tmconnect.rostfangst.export

import org.junit.Test
import se.tmconnect.rostfangst.data.FangstRad
import se.tmconnect.rostfangst.regler.ForslagTyp
import kotlin.test.assertEquals

class HistorikCsvTest {
    
    @Test
    fun `skal generera korrekt csv med rubrikrad`() {
        val fangstRader = listOf(
            FangstRad(
                id = 1,
                originalText = "Test text",
                typ = ForslagTyp.FORSLAG,
                titel = "Testtitel",
                plats = "Stockholm",
                svar = "Godkänt",
                skapadIso = "2023-01-01T10:00:00Z"
            )
        )
        
        val expected = """tid,typ,titel,original,plats,svar
"2023-01-01T10:00:00Z",FORSLAG,"Testtitel","Test text","Stockholm","Godkänt"

"""
        
        val result = historikSomCsv(fangstRader)
        assertEquals(expected, result)
    }
    
    @Test
    fun `skal citera fält med kommatecken`() {
        val fangstRader = listOf(
            FangstRad(
                id = 1,
                originalText = "Test text",
                typ = ForslagTyp.FORSLAG,
                titel = "Hej, världen",
                plats = null,
                svar = null,
                skapadIso = "2023-01-01T10:00:00Z"
            )
        )
        
        val expected = """tid,typ,titel,original,plats,svar
"2023-01-01T10:00:00Z",FORSLAG,"Hej, världen","Test text","",""

"""
        
        val result = historikSomCsv(fangstRader)
        assertEquals(expected, result)
    }
    
    @Test
    fun `skal hantera tomma fält`() {
        val fangstRader = listOf(
            FangstRad(
                id = 1,
                originalText = "",
                typ = ForslagTyp.FORSLAG,
                titel = "",
                plats = null,
                svar = null,
                skapadIso = "2023-01-01T10:00:00Z"
            )
        )
        
        val expected = """tid,typ,titel,original,plats,svar
"2023-01-01T10:00:00Z",FORSLAG,"","","","",""

"""
        
        val result = historikSomCsv(fangstRader)
        assertEquals(expected, result)
    }
}
