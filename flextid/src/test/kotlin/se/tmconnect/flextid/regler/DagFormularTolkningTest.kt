package se.tmconnect.flextid.regler

import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Test

class DagFormularTolkningTest {
    @Test
    fun giltigaTiderTolkas() {
        assertEquals(
            DagFormularTolkning.Ok(LocalTime.of(8, 0), LocalTime.of(17, 0), 45),
            tolkaDagFormular("08:00", "17:00", "45", standardRastMin = 30),
        )
    }

    @Test
    fun tomRastGerStandardrast() {
        assertEquals(
            DagFormularTolkning.Ok(LocalTime.of(8, 0), null, 30),
            tolkaDagFormular("08:00", "", "", standardRastMin = 30),
        )
    }

    @Test
    fun tommaTiderArTillatna() {
        assertEquals(
            DagFormularTolkning.Ok(null, null, 0),
            tolkaDagFormular("", " ", "0", standardRastMin = 30),
        )
    }

    @Test
    fun otolkbarStarttidGerFelIStalletForTomTid() {
        // Förut sparades "8.30" som ingen starttid, och en registrerad starttid raderades.
        assertEquals(
            DagFormularTolkning.Fel(TidValideringFel.OgiltigTid),
            tolkaDagFormular("8.30", "17:00", "30", standardRastMin = 30),
        )
        assertEquals(
            DagFormularTolkning.Fel(TidValideringFel.OgiltigTid),
            tolkaDagFormular("08:00", "25:00", "30", standardRastMin = 30),
        )
    }

    @Test
    fun otolkbarRastGerFel() {
        assertEquals(
            DagFormularTolkning.Fel(TidValideringFel.OgiltigTid),
            tolkaDagFormular("08:00", "17:00", "30 min", standardRastMin = 30),
        )
        assertEquals(
            DagFormularTolkning.Fel(TidValideringFel.OgiltigTid),
            tolkaDagFormular("08:00", "17:00", "-30", standardRastMin = 30),
        )
    }

    @Test
    fun slutForeStartGerFel() {
        assertEquals(
            DagFormularTolkning.Fel(TidValideringFel.SlutForeStart),
            tolkaDagFormular("22:00", "02:00", "0", standardRastMin = 30),
        )
    }

    @Test
    fun rastLangreAnArbetstidenGerFel() {
        // 08:00–09:00 med 300 minuters rast (skrivfel för 30) gav tyst 0 arbetad tid.
        assertEquals(
            DagFormularTolkning.Fel(TidValideringFel.RastLangreAnArbetstid),
            tolkaDagFormular("08:00", "09:00", "300", standardRastMin = 30),
        )
    }

    @Test
    fun rastLikaMedArbetstidenArTillaten() {
        assertEquals(
            DagFormularTolkning.Ok(LocalTime.of(8, 0), LocalTime.of(9, 0), 60),
            tolkaDagFormular("08:00", "09:00", "60", standardRastMin = 30),
        )
    }
}
