package se.tmconnect.flextid.export

import org.junit.Test
import org.junit.Assert.*

class SakerhetskopiaInstallningarTest {

    @Test
    fun testLasInstallningMedFalt() {
        val json = """
            {
                "version": 1,
                "skapad": "2023-01-01",
                "installning": {
                    "normaltidMinPerDag": 480,
                    "standardRastMin": 30,
                    "ingangssaldoMin": 0,
                    "arbetsdagarMask": 31,
                    "stamplaInPaminnelseMinEfterMidnatt": 15,
                    "aftnarLediga": false,
                    "semesterdagarPerAr": 30,
                    "semesterarStartmanad": 1
                },
                "dagar": []
            }
        """.trimIndent()

        val result = lasSakerhetskopiaJson(json)
        assertTrue(result is SakerhetskopiaJsonLasResult.Ok)
        
        val installning = result.innehall.installning
        assertFalse(installning.aftnarLediga)
        assertEquals(30, installning.semesterdagarPerAr)
        assertEquals(1, installning.semesterarStartmanad)
    }

    @Test
    fun testLasInstallningUtanFalt() {
        val json = """
            {
                "version": 1,
                "skapad": "2023-01-01",
                "installning": {
                    "normaltidMinPerDag": 480,
                    "standardRastMin": 30,
                    "ingangssaldoMin": 0,
                    "arbetsdagarMask": 31,
                    "stamplaInPaminnelseMinEfterMidnatt": 15
                },
                "dagar": []
            }
        """.trimIndent()

        val result = lasSakerhetskopiaJson(json)
        assertTrue(result is SakerhetskopiaJsonLasResult.Ok)
        
        val installning = result.innehall.installning
        assertTrue(installning.aftnarLediga)
        assertEquals(25, installning.semesterdagarPerAr)
        assertEquals(4, installning.semesterarStartmanad)
    }

    @Test
    fun testLasInstallningMedBegransadeVarden() {
        val json = """
            {
                "version": 1,
                "skapad": "2023-01-01",
                "installning": {
                    "normaltidMinPerDag": 480,
                    "standardRastMin": 30,
                    "ingangssaldoMin": 0,
                    "arbetsdagarMask": 31,
                    "stamplaInPaminnelseMinEfterMidnatt": 15,
                    "aftnarLediga": true,
                    "semesterdagarPerAr": 500,
                    "semesterarStartmanad": 7
                },
                "dagar": []
            }
        """.trimIndent()

        val result = lasSakerhetskopiaJson(json)
        assertTrue(result is SakerhetskopiaJsonLasResult.Ok)
        
        val installning = result.innehall.installning
        assertEquals(60, installning.semesterdagarPerAr) // Begränsad till max 60
        assertEquals(4, installning.semesterarStartmanad) // Om inte 1, blir 4
    }

    @Test
    fun testSkrivLasInstallning() {
        val original = SakerhetskopiaInstallning(
            normaltidMinPerDag = 480,
            standardRastMin = 30,
            ingangssaldoMin = 0,
            arbetsdagarMask = 31,
            stamplaInPaminnelseMinEfterMidnatt = 15,
            aftnarLediga = false,
            semesterdagarPerAr = 30,
            semesterarStartmanad = 1
        )

        val json = skrivSakerhetskopiaJson(
            SakerhetskopiaInnehall(
                version = 1,
                skapad = "2023-01-01",
                installning = original,
                dagar = emptyList()
            )
        )

        val result = lasSakerhetskopiaJson(json)
        assertTrue(result is SakerhetskopiaJsonLasResult.Ok)
        
        val restored = result.innehall.installning
        assertEquals(original.normaltidMinPerDag, restored.normaltidMinPerDag)
        assertEquals(original.standardRastMin, restored.standardRastMin)
        assertEquals(original.ingangssaldoMin, restored.ingangssaldoMin)
        assertEquals(original.arbetsdagarMask, restored.arbetsdagarMask)
        assertEquals(original.stamplaInPaminnelseMinEfterMidnatt, restored.stamplaInPaminnelseMinEfterMidnatt)
        assertEquals(original.aftnarLediga, restored.aftnarLediga)
        assertEquals(original.semesterdagarPerAr, restored.semesterdagarPerAr)
        assertEquals(original.semesterarStartmanad, restored.semesterarStartmanad)
    }
}
