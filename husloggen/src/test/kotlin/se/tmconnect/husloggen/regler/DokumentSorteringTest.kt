package se.tmconnect.husloggen.regler

import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.husloggen.data.Dokument
import se.tmconnect.husloggen.data.DokumentKategori
import java.time.LocalDate

class DokumentSorteringTest {

    private fun dok(id: Long, titel: String, kategori: DokumentKategori?) =
        Dokument(
            id = id,
            titel = titel,
            kategori = kategori,
            filnamn = "a.pdf",
            mimeTyp = "application/pdf",
            tillagd = LocalDate.of(2026, 1, 1),
        )

    private val d1 = dok(1, "Ritning plan 1", DokumentKategori.RITNING)
    private val d2 = dok(2, "manual diskmaskin", DokumentKategori.MANUAL)
    private val d3 = dok(3, "Avtal el", null)
    private val d4 = dok(4, "Manual ugn", DokumentKategori.MANUAL)
    private val d5 = dok(5, "Besiktning 2020", DokumentKategori.BESIKTNING)
    private val d6 = dok(6, "Garanti tak", DokumentKategori.OVRIGT)
    private val sex = listOf(d1, d2, d3, d4, d5, d6)

    @Test
    fun sorteraDokumentTomLista() {
        assertEquals(emptyList<Long>(), sorteraDokument(emptyList()).map { it.id })
    }

    @Test
    fun sorteraDokumentSexExempel() {
        assertEquals(listOf(2L, 4L, 1L, 5L, 6L, 3L), sorteraDokument(sex).map { it.id })
    }

    @Test
    fun sorteraDokumentUtanKategoriSist() {
        assertEquals(listOf(6L, 3L), sorteraDokument(listOf(d3, d6)).map { it.id })
    }

    @Test
    fun sorteraDokumentTitelCaseInsensitive() {
        val lista =
            listOf(
                dok(10, "b", DokumentKategori.MANUAL),
                dok(11, "A", DokumentKategori.MANUAL),
            )
        assertEquals(listOf(11L, 10L), sorteraDokument(lista).map { it.id })
    }

    @Test
    fun sorteraDokumentSammaTitelLagstIdForst() {
        val lista =
            listOf(
                dok(8, "kvitto", DokumentKategori.MANUAL),
                dok(7, "Kvitto", DokumentKategori.MANUAL),
            )
        assertEquals(listOf(7L, 8L), sorteraDokument(lista).map { it.id })
    }

    @Test
    fun grupperaDokumentPerKategoriTomLista() {
        assertEquals(emptyList<DokumentGrupp>(), grupperaDokumentPerKategori(emptyList()))
    }

    @Test
    fun grupperaDokumentPerKategoriSexExempel() {
        val grupper = grupperaDokumentPerKategori(sex)
        assertEquals(5, grupper.size)
        assertEquals(
            listOf(
                DokumentKategori.MANUAL,
                DokumentKategori.RITNING,
                DokumentKategori.BESIKTNING,
                DokumentKategori.OVRIGT,
                null,
            ),
            grupper.map { it.kategori },
        )
        assertEquals(
            listOf(
                listOf(2L, 4L),
                listOf(1L),
                listOf(5L),
                listOf(6L),
                listOf(3L),
            ),
            grupper.map { g -> g.dokument.map { it.id } },
        )
    }

    @Test
    fun grupperaDokumentPerKategoriEnUtanKategori() {
        val grupper = grupperaDokumentPerKategori(listOf(d3))
        assertEquals(1, grupper.size)
        assertEquals(null, grupper.single().kategori)
        assertEquals(listOf(3L), grupper.single().dokument.map { it.id })
    }
}
