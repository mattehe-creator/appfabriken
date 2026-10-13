package se.tmconnect.garantivalvet.regler

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.garantivalvet.data.Kop

class ReklamationsUppfoljningTest {
    private val idag = LocalDate.of(2026, 10, 13)

    private fun kop(reklamerad: LocalDate?, utfall: String? = null) = Kop(
        vad = "TV", kopdatum = LocalDate.of(2025, 1, 1), garantiManader = 24,
        reklamerad = reklamerad, reklamationUtfall = utfall,
    )

    @Test
    fun ingenReklamationGerIngenUppfoljning() {
        assertEquals(Uppfoljning.INGEN, uppfoljning(kop(null), idag))
    }

    @Test
    fun vantarTillsTvaVeckorGatt() {
        assertEquals(Uppfoljning.VANTAR_SVAR, uppfoljning(kop(idag.minusDays(13)), idag))
        assertEquals(Uppfoljning.BORDE_HA_SVARAT, uppfoljning(kop(idag.minusDays(14)), idag))
        assertEquals(Uppfoljning.BORDE_HA_SVARAT, uppfoljning(kop(idag.minusDays(40)), idag))
    }

    @Test
    fun utfalletGallerOavsettTid() {
        val gammal = idag.minusDays(100)
        assertEquals(Uppfoljning.LOST, uppfoljning(kop(gammal, "LOST"), idag))
        assertEquals(Uppfoljning.AVSLAG, uppfoljning(kop(gammal, "AVSLAG"), idag))
        assertEquals(Uppfoljning.ARN_ANMALD, uppfoljning(kop(gammal, "ARN_ANMALD"), idag))
    }

    @Test
    fun okantUtfallBehandlasSomInget() {
        assertEquals(Uppfoljning.VANTAR_SVAR, uppfoljning(kop(idag, "NAGOT_ANNAT"), idag))
    }

    @Test
    fun svarFristenArFjortonDagarEfterReklamationen() {
        assertEquals(LocalDate.of(2026, 10, 27), svarForvantasSenast(idag))
    }

    @Test
    fun arnPaminnelseBaraNarArendetArOppet() {
        assertTrue(arnPaminnelseRelevant(kop(idag)))
        assertTrue(arnPaminnelseRelevant(kop(idag, "AVSLAG")))
        assertFalse(arnPaminnelseRelevant(kop(idag, "LOST")))
        assertFalse(arnPaminnelseRelevant(kop(idag, "ARN_ANMALD")))
    }

    @Test
    fun svarPaminnelsenKommerDagenGattOchEnVeckaFramat() {
        val frist = Frist(1, FristTyp.SVAR, idag)
        fun skaAviseras(dagar: Long) = fristerSomSkaAviseras(listOf(frist), idag.plusDays(dagar), emptySet()).isNotEmpty()
        assertFalse(skaAviseras(-1))
        assertTrue(skaAviseras(0))
        assertTrue(skaAviseras(7))
        assertFalse(skaAviseras(8))
    }

    @Test
    fun svarPaminnelsenAviserasBaraEnGang() {
        val frist = Frist(1, FristTyp.SVAR, idag)
        val redan = setOf(fristNyckel(frist))
        assertTrue(fristerSomSkaAviseras(listOf(frist), idag, redan).isEmpty())
    }
}
