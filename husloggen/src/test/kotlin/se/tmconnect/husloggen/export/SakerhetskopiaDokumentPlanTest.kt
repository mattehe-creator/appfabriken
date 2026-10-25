package se.tmconnect.husloggen.export

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import se.tmconnect.husloggen.data.Dokument
import se.tmconnect.husloggen.data.DokumentKategori
import se.tmconnect.husloggen.data.PlaneradKostnad

class SakerhetskopiaDokumentPlanTest {
    @Test
    fun dokumentTillSakerhetskopiaMedKategori() {
        val dokument =
            Dokument(
                id = 4,
                bostadId = 2,
                titel = "Ritning",
                kategori = DokumentKategori.RITNING,
                anteckning = "Plan 1",
                filnamn = "abc.pdf",
                mimeTyp = "application/pdf",
                tillagd = LocalDate.of(2026, 5, 1),
            )
        val expected =
            SakerhetskopiaDokument(
                titel = "Ritning",
                kategori = "RITNING",
                anteckning = "Plan 1",
                filnamn = "abc.pdf",
                mimeTyp = "application/pdf",
                tillagd = LocalDate.of(2026, 5, 1),
                bostadIndex = 1,
            )
        assertEquals(expected, dokumentTillSakerhetskopia(dokument, mapOf(2L to 1)))
    }

    @Test
    fun dokumentTillSakerhetskopiaUtanKategori() {
        val dokument =
            Dokument(
                id = 4,
                bostadId = 2,
                titel = "Ritning",
                kategori = null,
                anteckning = "Plan 1",
                filnamn = "abc.pdf",
                mimeTyp = "application/pdf",
                tillagd = LocalDate.of(2026, 5, 1),
            )
        val result = dokumentTillSakerhetskopia(dokument, mapOf(2L to 1))
        assertNull(result.kategori)
    }

    @Test
    fun sakerhetskopiaDokumentTillDokumentTrimmarOchByterFilnamn() {
        val export =
            SakerhetskopiaDokument(
                titel = " Avtal el ",
                kategori = "AVTAL",
                anteckning = " ",
                filnamn = "gammal.pdf",
                mimeTyp = "application/pdf",
                tillagd = LocalDate.of(2025, 12, 31),
            )
        val expected =
            Dokument(
                id = 0,
                bostadId = 3,
                titel = "Avtal el",
                kategori = DokumentKategori.AVTAL,
                anteckning = null,
                filnamn = "ny.pdf",
                mimeTyp = "application/pdf",
                tillagd = LocalDate.of(2025, 12, 31),
            )
        assertEquals(expected, sakerhetskopiaDokumentTillDokument(export, filnamn = "ny.pdf", bostadId = 3))
    }

    @Test
    fun sakerhetskopiaDokumentTillDokumentOkandKategoriBlirNull() {
        val export =
            SakerhetskopiaDokument(
                titel = " Avtal el ",
                kategori = "OKAND",
                anteckning = " ",
                filnamn = "gammal.pdf",
                mimeTyp = "application/pdf",
                tillagd = LocalDate.of(2025, 12, 31),
            )
        val result = sakerhetskopiaDokumentTillDokument(export, filnamn = "ny.pdf", bostadId = 3)
        assertNull(result.kategori)
    }

    @Test
    fun sakerhetskopiaDokumentTillDokumentGemenerIKategoriBlirNull() {
        val export =
            SakerhetskopiaDokument(
                titel = " Avtal el ",
                kategori = "avtal",
                anteckning = " ",
                filnamn = "gammal.pdf",
                mimeTyp = "application/pdf",
                tillagd = LocalDate.of(2025, 12, 31),
            )
        val result = sakerhetskopiaDokumentTillDokument(export, filnamn = "ny.pdf", bostadId = 3)
        assertNull(result.kategori)
    }

    @Test
    fun planeradKostnadTillSakerhetskopia() {
        val planeradKostnad =
            PlaneradKostnad(
                id = 9,
                bostadId = 1,
                titel = "Nytt tak",
                beloppOre = 25_000_000,
                planeratAr = 2030,
            )
        val expected =
            SakerhetskopiaPlaneradKostnad(
                titel = "Nytt tak",
                beloppOre = 25_000_000,
                planeratAr = 2030,
                anteckning = null,
                bostadIndex = 0,
            )
        assertEquals(expected, planeradKostnadTillSakerhetskopia(planeradKostnad, mapOf(1L to 0)))
    }

    @Test
    fun sakerhetskopiaPlaneradKostnadTillPlaneradKostnadTrimmar() {
        val export =
            SakerhetskopiaPlaneradKostnad(
                titel = " Fönster ",
                beloppOre = 8_000_000,
                planeratAr = 2028,
                anteckning = " byt alla ",
                bostadIndex = 0,
            )
        val expected =
            PlaneradKostnad(
                id = 0,
                bostadId = 5,
                titel = "Fönster",
                beloppOre = 8_000_000,
                planeratAr = 2028,
                anteckning = "byt alla",
            )
        assertEquals(expected, sakerhetskopiaPlaneradKostnadTillPlaneradKostnad(export, bostadId = 5))
    }

    @Test
    fun sakerhetskopiaPlaneradKostnadTillPlaneradKostnadTomAnteckningBlirNull() {
        val export =
            SakerhetskopiaPlaneradKostnad(
                titel = " Fönster ",
                beloppOre = 8_000_000,
                planeratAr = 2028,
                anteckning = "",
                bostadIndex = 0,
            )
        val result = sakerhetskopiaPlaneradKostnadTillPlaneradKostnad(export, bostadId = 5)
        assertNull(result.anteckning)
    }
}
