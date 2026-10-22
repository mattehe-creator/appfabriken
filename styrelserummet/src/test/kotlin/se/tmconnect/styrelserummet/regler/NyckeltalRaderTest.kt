package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Test

class NyckeltalRaderTest {

    private fun basUnderlag() = NyckeltalUnderlag(
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
    fun sjuRaderMedFormateradeVarden() {
        val forvantade = listOf(
            NyckeltalRad("Årsavgift per kvm upplåten med bostadsrätt", "808 kr/kvm"),
            NyckeltalRad("Skuldsättning per kvm", "6\u00A0909 kr/kvm"),
            NyckeltalRad("Skuldsättning per kvm upplåten med bostadsrätt", "7\u00A0308 kr/kvm"),
            NyckeltalRad("Sparande per kvm", "309 kr/kvm"),
            NyckeltalRad("Räntekänslighet", "9,0"),
            NyckeltalRad("Energikostnad per kvm", "209 kr/kvm"),
            NyckeltalRad("Årsavgifternas andel av totala rörelseintäkter", "91,3 %"),
        )
        assertEquals(forvantade, nyckeltalRader(basUnderlag()))
    }

    @Test
    fun nollYtaOchIntakterGerStreck() {
        val u = basUnderlag().copy(
            kvmBostadsratt = 0,
            kvmHyresratt = 0,
            arsavgifter = 0,
            rorelseintakter = 0,
        )
        val rader = nyckeltalRader(u)
        assertEquals(7, rader.size)
        rader.forEach { rad ->
            assertEquals("\u2013", rad.varde)
        }
    }
}
