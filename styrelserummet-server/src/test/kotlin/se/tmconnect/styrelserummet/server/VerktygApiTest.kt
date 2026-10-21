package se.tmconnect.styrelserummet.server

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.io.path.createTempDirectory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class VerktygApiTest {
    private val exempel = NyckeltalArDto(
        ar = 2025,
        arsavgifter = 4_200_000,
        rantebarandeSkulder = 38_000_000,
        aretsResultat = -350_000,
        avskrivningar = 1_600_000,
        planeratUnderhall = 450_000,
        energikostnad = 1_150_000,
        rorelseintakter = 4_600_000,
        kvmBostadsratt = 5_200,
        kvmHyresratt = 300,
    )

    @Test
    fun nyckeltalForEttAr() {
        val svar = beraknaNyckeltalSvar(NyckeltalRequest(listOf(exempel)))
        val rader = svar.ar.single().rader.associate { it.etikett to it.varde }
        assertEquals("808 kr/kvm", rader["Årsavgift per kvm upplåten med bostadsrätt"])
        assertEquals("309 kr/kvm", rader["Sparande per kvm"])
        assertEquals("9,0", rader["Räntekänslighet"])
        assertEquals("91,3 %", rader["Årsavgifternas andel av totala rörelseintäkter"])
    }

    @Test
    fun nyckeltalSorterasNyastForstOchKontrolleras() {
        val svar = beraknaNyckeltalSvar(NyckeltalRequest(listOf(exempel.copy(ar = 2023), exempel)))
        assertEquals(listOf(2025, 2023), svar.ar.map { it.ar })
        assertThrows(VerktygFel::class.java) { beraknaNyckeltalSvar(NyckeltalRequest(emptyList())) }
        assertThrows(VerktygFel::class.java) { beraknaNyckeltalSvar(NyckeltalRequest(listOf(exempel, exempel))) }
        assertThrows(VerktygFel::class.java) {
            beraknaNyckeltalSvar(NyckeltalRequest((2020..2024).map { exempel.copy(ar = it) }))
        }
        assertThrows(VerktygFel::class.java) {
            beraknaNyckeltalSvar(NyckeltalRequest(listOf(exempel.copy(arsavgifter = -1))))
        }
    }

    private val atgarder = listOf(
        AtgardDto(2026, 100_000, "Takvård"),
        AtgardDto(2028, 600_000, "Fönster"),
        AtgardDto(2028, 300_000, "Balkonger"),
        AtgardDto(2030, 200_000, "Ventilation"),
    )

    @Test
    fun fondplanMinstaAvsattning() {
        val svar = beraknaFondplanSvar(FondplanRequest(2026, 5, 300_000, null, atgarder))
        assertEquals(kronorText(233_334), svar.minstaAvsattning)
        assertEquals(kronorText(180_000), svar.jamnAvsattning)
        assertNull(svar.forstaUnderskottsar)
        assertEquals(5, svar.utveckling.size)
        assertTrue(svar.csv.contains("UTKAST"))
    }

    @Test
    fun fondplanProvadAvsattningMedUnderskott() {
        val svar = beraknaFondplanSvar(FondplanRequest(2026, 5, 300_000, 180_000, atgarder))
        assertEquals(2028, svar.forstaUnderskottsar)
        assertTrue(svar.utveckling.single { it.ar == 2028 }.underskott)
        assertThrows(VerktygFel::class.java) { beraknaFondplanSvar(FondplanRequest(2026, 0)) }
        assertThrows(VerktygFel::class.java) { beraknaFondplanSvar(FondplanRequest(2026, 5, arligAvsattning = -1)) }
    }

    @Test
    fun rutterna() = testApplication {
        application {
            module(ServerMiljo(dataRot = createTempDirectory("verktyg"), port = 0, tolkare = FalskTolkare(), synkronJobb = true))
        }
        assertEquals(HttpStatusCode.OK, client.get("/nyckeltal").status)
        assertEquals(HttpStatusCode.OK, client.get("/fondplan").status)
        val ok = client.post("/api/fondplan") {
            contentType(ContentType.Application.Json)
            setBody("""{"franAr":2026,"antalAr":5,"ingaendeFond":300000,"atgarder":[{"ar":2028,"belopp":900000}]}""")
        }
        assertEquals(HttpStatusCode.OK, ok.status)
        assertTrue(ok.bodyAsText().contains("minstaAvsattning"))
        val fel = client.post("/api/nyckeltal") {
            contentType(ContentType.Application.Json)
            setBody("""{"ar":[]}""")
        }
        assertEquals(HttpStatusCode.BadRequest, fel.status)
        assertTrue(fel.bodyAsText().contains("Ange minst ett år"))
        val trasig = client.post("/api/fondplan") {
            contentType(ContentType.Application.Json)
            setBody("inte json")
        }
        assertEquals(HttpStatusCode.BadRequest, trasig.status)
    }

    private fun kronorText(v: Long) = se.tmconnect.styrelserummet.regler.kronor(v)
}
