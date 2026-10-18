package se.tmconnect.flextid.export

import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.flextid.regler.DagTyp

class SakerhetskopiaJsonTest {
    @Test
    fun rundturSkrivLas() {
        val innehall =
            SakerhetskopiaInnehall(
                version = 1,
                skapad = "2026-10-18T08:00:00Z",
                installning =
                    SakerhetskopiaInstallning(
                        normaltidMinPerDag = 480,
                        standardRastMin = 30,
                        ingangssaldoMin = -120,
                        arbetsdagarMask = 31,
                        stamplaInPaminnelseMinEfterMidnatt = 390,
                    ),
                dagar =
                    listOf(
                        SakerhetskopiaDag(
                            datum = LocalDate.of(2026, 10, 13),
                            start = LocalTime.of(8, 0),
                            slut = LocalTime.of(16, 30),
                            rastMin = 30,
                            typ = DagTyp.ARBETE.name,
                        ),
                    ),
            )
        val json = skrivSakerhetskopiaJson(innehall)
        val las = lasSakerhetskopiaJson(json)
        assertTrue(las is SakerhetskopiaJsonLasResult.Ok)
        assertEquals(innehall, (las as SakerhetskopiaJsonLasResult.Ok).innehall)
    }

    @Test
    fun aldreVersion0UtanInstallning() {
        val json =
            """
            {"version":0,"skapad":"2026-01-01","dagar":[{"datum":"2026-10-13","start":"08:00","slut":"16:30","rastMin":30,"typ":"ARBETE"}]}
            """.trimIndent()
        val las = lasSakerhetskopiaJson(json) as SakerhetskopiaJsonLasResult.Ok
        assertEquals(480, las.innehall.installning.normaltidMinPerDag)
        assertEquals(1, las.innehall.dagar.size)
    }

    @Test
    fun saknadStamplaInPaminnelseMinGer0800() {
        val json =
            """
            {"version":1,"skapad":"2026-01-01","installning":{"normaltidMinPerDag":480,"standardRastMin":30,"ingangssaldoMin":0,"arbetsdagarMask":31},"dagar":[]}
            """.trimIndent()
        val las = lasSakerhetskopiaJson(json) as SakerhetskopiaJsonLasResult.Ok
        assertEquals(480, las.innehall.installning.stamplaInPaminnelseMinEfterMidnatt)
    }

    @Test
    fun ogiltigStamplaInPaminnelseMinGer0800() {
        val json =
            """
            {"version":1,"skapad":"2026-01-01","installning":{"normaltidMinPerDag":480,"standardRastMin":30,"ingangssaldoMin":0,"arbetsdagarMask":31,"stamplaInPaminnelseMinEfterMidnatt":90},"dagar":[]}
            """.trimIndent()
        val las = lasSakerhetskopiaJson(json) as SakerhetskopiaJsonLasResult.Ok
        assertEquals(480, las.innehall.installning.stamplaInPaminnelseMinEfterMidnatt)
    }

    @Test
    fun okandFramtidaVersionAvvisas() {
        val json = """{"version":99,"skapad":"2026-01-01","installning":{},"dagar":[]}"""
        assertTrue(lasSakerhetskopiaJson(json) is SakerhetskopiaJsonLasResult.Fel.OkandVersion)
    }
}
