package se.tmconnect.flextid.regler

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.MonthDay
import java.time.YearMonth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.flextid.data.Dag
import se.tmconnect.flextid.data.Installning
import se.tmconnect.flextid.export.SakerhetskopiaInnehall
import se.tmconnect.flextid.export.SakerhetskopiaJsonLasResult
import se.tmconnect.flextid.export.installningTillSakerhetskopia
import se.tmconnect.flextid.export.lasSakerhetskopiaJson
import se.tmconnect.flextid.export.skrivSakerhetskopiaJson

class ArbetstidTest {
    private val sommar = SommarNormaltid(MonthDay.of(6, 1), MonthDay.of(8, 31), 420)
    private val fredag7 = mapOf(DayOfWeek.FRIDAY to 420)
    private val bas = Arbetstid(normaltidMinPerDag = 480, arbetsdagarMask = ARBETSDAGAR_MANDAG_FREDAG)

    @Test
    fun standardNormaltidUtanUndantag() {
        assertEquals(480, bas.normaltidFor(LocalDate.of(2026, 10, 21))) // onsdag
        assertEquals(0, bas.normaltidFor(LocalDate.of(2026, 10, 24))) // lördag
        assertEquals(0, bas.normaltidFor(LocalDate.of(2026, 12, 25))) // juldagen
    }

    @Test
    fun fredagMedEgenNormaltid() {
        val at = bas.copy(perVeckodag = fredag7)
        assertEquals(420, at.normaltidFor(LocalDate.of(2026, 10, 23)))
        assertEquals(480, at.normaltidFor(LocalDate.of(2026, 10, 22)))
    }

    @Test
    fun sommartidGallerForeVeckodag() {
        val at = bas.copy(perVeckodag = mapOf(DayOfWeek.FRIDAY to 360), sommar = sommar)
        assertEquals(420, at.normaltidFor(LocalDate.of(2026, 6, 1)))
        assertEquals(420, at.normaltidFor(LocalDate.of(2026, 8, 31)))
        assertEquals(420, at.normaltidFor(LocalDate.of(2026, 7, 3))) // fredag i sommartid
        assertEquals(360, at.normaltidFor(LocalDate.of(2026, 9, 4))) // fredag efter
        assertEquals(480, at.normaltidFor(LocalDate.of(2026, 5, 28))) // torsdag
    }

    @Test
    fun sommartidGaldsInteRodDag() {
        val at = bas.copy(sommar = sommar)
        assertEquals(0, at.normaltidFor(LocalDate.of(2026, 6, 6))) // nationaldagen, lördag
        assertEquals(0, at.normaltidFor(LocalDate.of(2026, 6, 19))) // midsommarafton, ledig
        assertEquals(420, at.copy(aftnarLediga = false).normaltidFor(LocalDate.of(2026, 6, 19)))
    }

    @Test
    fun periodOverArsskiftet() {
        val vinter = SommarNormaltid(MonthDay.of(12, 1), MonthDay.of(2, 28), 450)
        assertTrue(vinter.omfattar(LocalDate.of(2026, 12, 15)))
        assertTrue(vinter.omfattar(LocalDate.of(2027, 1, 10)))
        assertFalse(vinter.omfattar(LocalDate.of(2027, 3, 1)))
        assertFalse(vinter.omfattar(LocalDate.of(2026, 11, 30)))
    }

    @Test
    fun utfallMedKortFredag() {
        val at = bas.copy(perVeckodag = fredag7)
        val fredag = Dag(datum = LocalDate.of(2026, 10, 23), start = LocalTime.of(8, 0), slut = LocalTime.of(15, 30), rastMin = 30)
        assertEquals(0, dagensUtfall(fredag, at))
        assertEquals(-60, dagensUtfall(fredag, bas))
    }

    @Test
    fun manadsrapportensNormaltidSummerarPerDag() {
        // Juni 2026 med sommartid 7 h: 22 vardagar minus nationaldagen (lördag, ingen påverkan)
        // och midsommarafton (ledig) = 21 arbetsdagar, alla i sommartid.
        val at = bas.copy(sommar = sommar)
        val juni = YearMonth.of(2026, 6)
        assertEquals(21 * 420, at.normaltidForIntervall(juni.atDay(1), juni.atEndOfMonth()))
    }

    @Test
    fun tolkaLangd() {
        assertEquals(420, tolkaLangdText("7:00"))
        assertEquals(450, tolkaLangdText("7,5"))
        assertEquals(450, tolkaLangdText("7.5"))
        assertEquals(480, tolkaLangdText("8"))
        assertEquals(465, tolkaLangdText(" 7:45 "))
        assertNull(tolkaLangdText(""))
        assertNull(tolkaLangdText("7:5"))
        assertNull(tolkaLangdText("7:60"))
        assertNull(tolkaLangdText("25"))
        assertNull(tolkaLangdText("abc"))
    }

    @Test
    fun tolkaDagManad() {
        assertEquals(MonthDay.of(6, 1), tolkaDagManadText("1/6"))
        assertEquals(MonthDay.of(8, 31), tolkaDagManadText("31-08"))
        assertEquals(MonthDay.of(2, 29), tolkaDagManadText("29/2"))
        assertNull(tolkaDagManadText("31/6"))
        assertNull(tolkaDagManadText("1"))
    }

    @Test
    fun perVeckodagTextFramOchTillbaka() {
        val karta = mapOf(DayOfWeek.FRIDAY to 420, DayOfWeek.MONDAY to 510)
        val text = formateraPerVeckodag(karta)
        assertEquals("MONDAY=510;FRIDAY=420", text)
        assertEquals(karta, tolkaPerVeckodag(text))
        assertEquals(mapOf(DayOfWeek.MONDAY to 480), tolkaPerVeckodag("MONDAY=480;FOO=1;TUESDAY=x;WEDNESDAY=99999"))
        assertEquals(emptyMap<DayOfWeek, Int>(), tolkaPerVeckodag(null))
    }

    @Test
    fun sakerhetskopiaBevararUndantag() {
        val undantag = NormaltidUndantag(perVeckodag = fredag7, sommar = sommar)
        val innehall =
            SakerhetskopiaInnehall(
                version = 1,
                skapad = "2026-10-24T00:00:00Z",
                installning = installningTillSakerhetskopia(Installning(), 480).copy(normaltidUndantag = undantag),
                dagar = emptyList(),
            )
        val las = lasSakerhetskopiaJson(skrivSakerhetskopiaJson(innehall))
        assertTrue(las is SakerhetskopiaJsonLasResult.Ok)
        assertEquals(undantag, (las as SakerhetskopiaJsonLasResult.Ok).innehall.installning.normaltidUndantag)
    }

    @Test
    fun aldreSakerhetskopiaUtanUndantag() {
        val json = """{"version":1,"skapad":"x","installning":{"normaltidMinPerDag":480},"dagar":[]}"""
        val las = lasSakerhetskopiaJson(json) as SakerhetskopiaJsonLasResult.Ok
        assertEquals(NormaltidUndantag(), las.innehall.installning.normaltidUndantag)
    }
}
