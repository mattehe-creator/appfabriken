package se.tmconnect.garantivalvet.export

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.garantivalvet.data.Kop
import java.time.LocalDate

class SakerhetskopiaJsonTest {
    @Test
    fun rundturSkrivLas() {
        val innehall =
            SakerhetskopiaInnehall(
                version = 1,
                skapad = "2026-10-11T10:00:00Z",
                kop =
                    listOf(
                        SakerhetskopiaKop(
                            vad = "Kaffebryggare",
                            varKopt = "Elgiganten",
                            kopdatum = LocalDate.of(2024, 3, 1),
                            garantiManader = 24,
                            prisOre = 999_00L,
                            anteckning = "Kvitto sparat",
                            kvittoFil = "abc.jpg",
                        ),
                    ),
            )
        val json = skrivSakerhetskopiaJson(innehall)
        val las = lasSakerhetskopiaJson(json)
        assertTrue(las is SakerhetskopiaJsonLasResult.Ok)
        val ok = las as SakerhetskopiaJsonLasResult.Ok
        assertEquals(innehall, ok.innehall)
    }

    @Test
    fun kopUtanValfriaFalt() {
        val innehall =
            SakerhetskopiaInnehall(
                version = 1,
                skapad = "2026-10-11",
                kop =
                    listOf(
                        SakerhetskopiaKop(
                            vad = "Minimal",
                            kopdatum = LocalDate.of(2025, 1, 1),
                            garantiManader = 12,
                        ),
                    ),
            )
        val las = lasSakerhetskopiaJson(skrivSakerhetskopiaJson(innehall))
        assertTrue(las is SakerhetskopiaJsonLasResult.Ok)
        val kop = (las as SakerhetskopiaJsonLasResult.Ok).innehall.kop.single()
        assertEquals(null, kop.varKopt)
        assertEquals(null, kop.prisOre)
        assertEquals(null, kop.anteckning)
        assertEquals(null, kop.kvittoFil)
    }

    @Test
    fun specialteckenOchEmoji() {
        val text = "Citat \" och \\ backslash\nrad\nåäö 🛒"
        val innehall =
            SakerhetskopiaInnehall(
                version = 1,
                skapad = "2026-10-11",
                kop =
                    listOf(
                        SakerhetskopiaKop(
                            vad = text,
                            kopdatum = LocalDate.of(2025, 6, 1),
                            garantiManader = 6,
                            anteckning = text,
                        ),
                    ),
            )
        val las = lasSakerhetskopiaJson(skrivSakerhetskopiaJson(innehall))
        assertTrue(las is SakerhetskopiaJsonLasResult.Ok)
        val kop = (las as SakerhetskopiaJsonLasResult.Ok).innehall.kop.single()
        assertEquals(text, kop.vad)
        assertEquals(text, kop.anteckning)
    }

    @Test
    fun version2Avvisas() {
        val json = """{"version":2,"skapad":"2026-10-11","kop":[]}"""
        val las = lasSakerhetskopiaJson(json)
        assertTrue(las is SakerhetskopiaJsonLasResult.Fel.OkandVersion)
    }

    @Test
    fun trasigJsonAvvisas() {
        assertTrue(lasSakerhetskopiaJson("{") is SakerhetskopiaJsonLasResult.Fel.TrasigJson)
        assertTrue(lasSakerhetskopiaJson("""{"version":1}""") is SakerhetskopiaJsonLasResult.Fel.TrasigJson)
    }

    @Test
    fun dubblettfunktion() {
        val export =
            SakerhetskopiaKop(
                vad = "A",
                varKopt = "B",
                kopdatum = LocalDate.of(2024, 1, 1),
                garantiManader = 12,
                prisOre = 100L,
            )
        val lik =
            Kop(
                id = 99,
                vad = "A",
                varKopt = "B",
                kopdatum = LocalDate.of(2024, 1, 1),
                garantiManader = 12,
                prisOre = 100L,
            )
        val olik = lik.copy(vad = "Annan")
        assertTrue(arSakerhetskopiaDubblett(export, lik))
        assertFalse(arSakerhetskopiaDubblett(export, olik))
    }

    @Test
    fun oppetKopOchVarutypGarRundtur() {
        val innehall =
            SakerhetskopiaInnehall(
                version = 1,
                skapad = "2026-10-12",
                kop =
                    listOf(
                        SakerhetskopiaKop(
                            vad = "Kabel",
                            kopdatum = LocalDate.of(2026, 9, 1),
                            garantiManader = 24,
                            oppetKopDagar = 30,
                            varutyp = "ELEKTRONIK",
                        ),
                    ),
            )
        val las = lasSakerhetskopiaJson(skrivSakerhetskopiaJson(innehall)) as SakerhetskopiaJsonLasResult.Ok
        assertEquals(innehall, las.innehall)
    }

    @Test
    fun gammalFilUtanOppetKopLasesSomNull() {
        val json = """{"version":1,"skapad":"x","kop":[{"vad":"A","kopdatum":"2025-01-01","garantiManader":12}]}"""
        val las = lasSakerhetskopiaJson(json) as SakerhetskopiaJsonLasResult.Ok
        assertEquals(null, las.innehall.kop.single().oppetKopDagar)
        assertEquals(null, las.innehall.kop.single().varutyp)
    }

    @Test
    fun ogiltigtOppetKopOchOkandVarutypRensasVidImport() {
        val kop = sakerhetskopiaKopTillKop(
            SakerhetskopiaKop(
                vad = "A",
                kopdatum = LocalDate.of(2025, 1, 1),
                garantiManader = 12,
                oppetKopDagar = 9999,
                varutyp = "UPPHITTAD",
            ),
        )
        assertEquals(null, kop.oppetKopDagar)
        assertEquals(null, kop.varutyp)
    }
}
