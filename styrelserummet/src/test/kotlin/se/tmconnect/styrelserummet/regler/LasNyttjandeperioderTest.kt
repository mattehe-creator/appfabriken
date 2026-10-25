package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Komponentgrupp

class LasNyttjandeperioderTest {

    private fun las(rad: String) = lasNyttjandeperioder(listOf("grupp;intervall;forslag;andel", rad))

    @Test
    fun stommeMedExtraKolumner() {
        assertEquals(
            listOf(NyttjandeperiodRad(Komponentgrupp.STOMME, 75..120, 100, 63)),
            las("STOMME;75-120;100;63;FAR tabell;nej"),
        )
    }

    @Test
    fun stammarEnstakaAr() {
        assertEquals(
            listOf(NyttjandeperiodRad(Komponentgrupp.STAMMAR, null, 50, 6)),
            las("STAMMAR;50;50;6"),
        )
    }

    @Test
    fun markOchGarageNollor() {
        assertEquals(
            listOf(NyttjandeperiodRad(Komponentgrupp.MARK_OCH_GARAGE, null, 0, 0)),
            las("MARK_OCH_GARAGE;0;0;0"),
        )
    }

    @Test
    fun fasadIntervallMedMellanrum() {
        assertEquals(
            listOf(NyttjandeperiodRad(Komponentgrupp.FASAD, 40..100, 40, 5)),
            las("FASAD; 40 - 100 ;40;5"),
        )
    }

    @Test
    fun takMedInledandeOchAvslutandeMellanrum() {
        assertEquals(
            listOf(NyttjandeperiodRad(Komponentgrupp.TAK, 30..50, 40, 4)),
            las(" TAK ;30-50;40;4"),
        )
    }

    @Test
    fun varmeTomtIntervall() {
        assertEquals(
            listOf(NyttjandeperiodRad(Komponentgrupp.VARME, null, 80, 6)),
            las("VARME;;80;6"),
        )
    }

    @Test
    fun okandGruppGemener() {
        assertEquals(emptyList<NyttjandeperiodRad>(), las("tak;30-50;40;4"))
    }

    @Test
    fun forFaDelar() {
        assertEquals(emptyList<NyttjandeperiodRad>(), las("TAK;30-50;40"))
    }

    @Test
    fun ickeHeltalForslag() {
        assertEquals(emptyList<NyttjandeperiodRad>(), las("HISS;25-30;tjugo;2"))
    }

    @Test
    fun tomRad() {
        assertEquals(emptyList<NyttjandeperiodRad>(), las(""))
    }

    @Test
    fun tomIndataLista() {
        assertEquals(emptyList<NyttjandeperiodRad>(), lasNyttjandeperioder(emptyList()))
    }

    @Test
    fun endaRadenArRubrik() {
        assertEquals(emptyList<NyttjandeperiodRad>(), lasNyttjandeperioder(listOf("STOMME;75-120;100;63")))
    }
}
