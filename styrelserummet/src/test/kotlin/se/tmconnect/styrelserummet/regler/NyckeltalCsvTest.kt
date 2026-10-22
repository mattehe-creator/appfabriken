package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.styrelserummet.underlag.UTKAST_RAD

class NyckeltalCsvTest {

    @Test
    fun tvaArGerRubrikOchVardePerEtikett() {
        val perAr = listOf(
            2025 to listOf(NyckeltalRad("Sparande per kvm", "309 kr/kvm")),
            2024 to listOf(NyckeltalRad("Sparande per kvm", "280 kr/kvm")),
        )
        val csv = nyckeltalCsv(perAr)
        assertEquals(
            "nyckeltal;2025;2024\nSparande per kvm;309 kr/kvm;280 kr/kvm\n$UTKAST_RAD\n",
            csv,
        )
    }

    @Test
    fun semikolonIVardeCiteras() {
        val perAr = listOf(
            2025 to listOf(NyckeltalRad("Test", "a;b")),
        )
        val csv = nyckeltalCsv(perAr)
        assertEquals(
            "nyckeltal;2025\nTest;\"a;b\"\n$UTKAST_RAD\n",
            csv,
        )
    }

    @Test
    fun tomListaHarRubrikOchUtkastRad() {
        val csv = nyckeltalCsv(emptyList())
        assertEquals(
            "nyckeltal\n$UTKAST_RAD\n",
            csv,
        )
    }
}
