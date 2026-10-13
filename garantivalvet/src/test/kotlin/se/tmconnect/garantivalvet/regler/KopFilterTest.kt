package se.tmconnect.garantivalvet.regler

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.garantivalvet.data.Kop

class KopFilterTest {
    private val idag = LocalDate.of(2026, 10, 12)

    private fun kop(
        id: Long,
        vad: String,
        butik: String? = null,
        anteckning: String? = null,
        varutyp: String? = null,
        kopdatum: LocalDate = LocalDate.of(2026, 9, 1),
        garantiManader: Int = 24,
    ) = Kop(id = id, vad = vad, varKopt = butik, anteckning = anteckning, varutyp = varutyp, kopdatum = kopdatum, garantiManader = garantiManader)

    private val lista = listOf(
        kop(1, "Kaffebryggare", "Elgiganten", varutyp = "ELEKTRONIK"),
        kop(2, "Regnjacka", "Stadium", anteckning = "Blå, storlek M", varutyp = "KLADER_SKOR"),
        kop(3, "Skrivplatta A4", "Biltema", varutyp = "OVRIGT"),
        kop(4, "Gammal TV", "Elgiganten", varutyp = "ELEKTRONIK", kopdatum = LocalDate.of(2020, 1, 1), garantiManader = 12),
    )

    private fun ider(filter: KopFilter) = filtreraKop(lista, filter, idag).map { it.id }

    @Test
    fun tomtFilterGerAllt() {
        assertEquals(listOf(1L, 2L, 3L, 4L), ider(KopFilter()))
        assertEquals(listOf(1L, 2L, 3L, 4L), ider(KopFilter(fraga = "   ")))
    }

    @Test
    fun sokerIVaraButikOchAnteckning() {
        assertEquals(listOf(1L), ider(KopFilter("kaffe")))
        assertEquals(listOf(3L), ider(KopFilter("biltema")))
        assertEquals(listOf(2L), ider(KopFilter("storlek m")))
        assertEquals(listOf(1L, 4L), ider(KopFilter("ELGIGANTEN")))
    }

    @Test
    fun allaOrdMasteFinnasOchOrdningenSpelarInteRoll() {
        assertEquals(listOf(1L), ider(KopFilter("elgiganten kaffe")))
        assertEquals(listOf(1L), ider(KopFilter("kaffe elgiganten")))
        assertEquals(emptyList<Long>(), ider(KopFilter("kaffe biltema")))
    }

    @Test
    fun filterPaVarutyp() {
        assertEquals(listOf(1L, 4L), ider(KopFilter(varutyp = Varutyp.ELEKTRONIK)))
        assertEquals(listOf(2L), ider(KopFilter(varutyp = Varutyp.KLADER_SKOR)))
    }

    @Test
    fun filterPaStatusgrupp() {
        assertTrue(4L in ider(KopFilter(grupp = ListaGrupp.UTGANGNA)))
        assertEquals(listOf(1L, 2L, 3L), ider(KopFilter(grupp = ListaGrupp.GALLER)))
    }

    @Test
    fun filterKombineras() {
        assertEquals(listOf(4L), ider(KopFilter("elgiganten", ListaGrupp.UTGANGNA, Varutyp.ELEKTRONIK)))
        assertEquals(emptyList<Long>(), ider(KopFilter("elgiganten", ListaGrupp.UTGANGNA, Varutyp.KLADER_SKOR)))
    }

    @Test
    fun inteIListanGerTomtResultat() {
        assertEquals(emptyList<Long>(), ider(KopFilter("finns inte")))
    }
}
