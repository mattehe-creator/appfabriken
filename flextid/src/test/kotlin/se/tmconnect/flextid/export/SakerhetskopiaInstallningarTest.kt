package se.tmconnect.flextid.export

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SakerhetskopiaInstallningarTest {
    @Test
    fun skrivLasMedFalt() {
        val original =
            SakerhetskopiaInstallning(
                normaltidMinPerDag = 480,
                standardRastMin = 30,
                ingangssaldoMin = 0,
                arbetsdagarMask = 31,
                stamplaInPaminnelseMinEfterMidnatt = 15,
                aftnarLediga = false,
                semesterdagarPerAr = 30,
                semesterarStartmanad = 1,
            )
        val json =
            skrivSakerhetskopiaJson(
                SakerhetskopiaInnehall(
                    version = 1,
                    skapad = "2023-01-01",
                    installning = original,
                    dagar = emptyList(),
                ),
            )
        val las = lasSakerhetskopiaJson(json) as SakerhetskopiaJsonLasResult.Ok
        assertEquals(original.aftnarLediga, las.innehall.installning.aftnarLediga)
        assertEquals(original.semesterdagarPerAr, las.innehall.installning.semesterdagarPerAr)
        assertEquals(original.semesterarStartmanad, las.innehall.installning.semesterarStartmanad)
    }

    @Test
    fun lasUtanNyaFaltGerStandard() {
        val json =
            """
            {"version":1,"skapad":"2023-01-01","installning":{"normaltidMinPerDag":480,"standardRastMin":30,"ingangssaldoMin":0,"arbetsdagarMask":31,"stamplaInPaminnelseMinEfterMidnatt":15},"dagar":[]}
            """.trimIndent()
        val las = lasSakerhetskopiaJson(json) as SakerhetskopiaJsonLasResult.Ok
        assertTrue(las.innehall.installning.aftnarLediga)
        assertEquals(25, las.innehall.installning.semesterdagarPerAr)
        assertEquals(4, las.innehall.installning.semesterarStartmanad)
    }

    @Test
    fun lasBegransarSemesterdagarOchStartmanad() {
        val json =
            """
            {"version":1,"skapad":"2023-01-01","installning":{"normaltidMinPerDag":480,"standardRastMin":30,"ingangssaldoMin":0,"arbetsdagarMask":31,"stamplaInPaminnelseMinEfterMidnatt":15,"aftnarLediga":true,"semesterdagarPerAr":500,"semesterarStartmanad":7},"dagar":[]}
            """.trimIndent()
        val las = lasSakerhetskopiaJson(json) as SakerhetskopiaJsonLasResult.Ok
        assertEquals(60, las.innehall.installning.semesterdagarPerAr)
        assertEquals(4, las.innehall.installning.semesterarStartmanad)
    }

    @Test
    fun lasAftnarLedigaFalse() {
        val json =
            """
            {"version":1,"skapad":"2023-01-01","installning":{"normaltidMinPerDag":480,"standardRastMin":30,"ingangssaldoMin":0,"arbetsdagarMask":31,"stamplaInPaminnelseMinEfterMidnatt":15,"aftnarLediga":false,"semesterdagarPerAr":30,"semesterarStartmanad":1},"dagar":[]}
            """.trimIndent()
        val las = lasSakerhetskopiaJson(json) as SakerhetskopiaJsonLasResult.Ok
        assertFalse(las.innehall.installning.aftnarLediga)
    }
}
