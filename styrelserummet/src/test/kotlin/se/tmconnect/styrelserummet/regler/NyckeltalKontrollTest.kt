package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Test

class NyckeltalKontrollTest {

    private fun standardUnderlag() = NyckeltalUnderlag(
        arsavgifter = 4_200_000,
        rantebarandeSkulder = 38_000_000,
        aretsResultat = -350_000,
        avskrivningar = 1_600_000,
        utrangeringar = 0,
        planeratUnderhall = 450_000,
        justeringar = 0,
        energikostnad = 1_150_000,
        rorelseintakter = 4_600_000,
        kvmBostadsratt = 5_200,
        kvmHyresratt = 300,
    )

    @Test
    fun standardUnderlagGerIngaMeddelanden() {
        assertEquals(emptyList<String>(), kontrolleraUnderlag(standardUnderlag()))
    }

    @Test
    fun arsavgifterITusentalGerMeddelandeOmLagAvgiftPerKvm() {
        val u = standardUnderlag().copy(arsavgifter = 4_200)
        assertEquals(
            listOf(
                "Årsavgiften är under 100 kr per kvm och år. Är beloppet angivet i tusental kronor?",
            ),
            kontrolleraUnderlag(u),
        )
    }

    @Test
    fun saknadBostadsrattsytaGerMeddelandeNarDetFinnsArsavgifter() {
        val u = standardUnderlag().copy(kvmBostadsratt = 0, kvmHyresratt = 0)
        assertEquals(
            listOf("Ytan upplåten med bostadsrätt saknas, men det finns årsavgifter."),
            kontrolleraUnderlag(u),
        )
    }

    @Test
    fun lagreRorelseintakterGerMeddelandenOmAvgiftOchEnergi() {
        val u = standardUnderlag().copy(rorelseintakter = 1_000_000)
        assertEquals(
            listOf(
                "Årsavgifterna är större än de totala rörelseintäkterna. Kontrollera beloppen.",
                "Energikostnaden är större än de totala rörelseintäkterna. Kontrollera beloppen.",
            ),
            kontrolleraUnderlag(u),
        )
    }
}
