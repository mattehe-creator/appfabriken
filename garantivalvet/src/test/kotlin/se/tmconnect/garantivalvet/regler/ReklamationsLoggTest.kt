package se.tmconnect.garantivalvet.regler

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.garantivalvet.data.Kop

class ReklamationsLoggTest {
    private fun kop(upptackt: LocalDate? = null, reklamerad: LocalDate? = null) =
        Kop(
            vad = "Kabel",
            kopdatum = LocalDate.of(2026, 9, 1),
            garantiManader = 24,
            felUpptackt = upptackt,
            reklamerad = reklamerad,
        )

    @Test
    fun tvaManaderFranUpptackten() {
        assertEquals(LocalDate.of(2026, 12, 1), reklamerasSenast(LocalDate.of(2026, 10, 1)))
        assertEquals(LocalDate.of(2027, 2, 28), reklamerasSenast(LocalDate.of(2026, 12, 31)))
    }

    @Test
    fun ettArFranForstaReklamationenForArn() {
        assertEquals(LocalDate.of(2027, 10, 5), arnAnmalanSenast(LocalDate.of(2026, 10, 5)))
        assertEquals(LocalDate.of(2025, 2, 28), arnAnmalanSenast(LocalDate.of(2024, 2, 29)))
    }

    @Test
    fun loggenFoljerKopetsTillstand() {
        assertEquals(ReklamationsLogg.INGEN, reklamationsLogg(kop()))
        assertEquals(ReklamationsLogg.FEL_UPPTACKT, reklamationsLogg(kop(upptackt = LocalDate.of(2026, 10, 1))))
        assertEquals(
            ReklamationsLogg.REKLAMERAD,
            reklamationsLogg(kop(upptackt = LocalDate.of(2026, 10, 1), reklamerad = LocalDate.of(2026, 10, 5))),
        )
    }

    @Test
    fun paminnelseFranFelUpptackt14DagarFore() {
        val slut = LocalDate.of(2026, 12, 1)
        val frist = Frist(1, FristTyp.REKLAMERA_SENAST, slut)
        assertEquals(listOf(frist), fristerSomSkaAviseras(listOf(frist), LocalDate.of(2026, 11, 17), emptySet()))
        assertTrue(fristerSomSkaAviseras(listOf(frist), LocalDate.of(2026, 11, 16), emptySet()).isEmpty())
    }

    @Test
    fun arnPaminnelseTrettioDagarFore() {
        val slut = LocalDate.of(2027, 10, 5)
        val frist = Frist(1, FristTyp.ARN, slut)
        assertEquals(listOf(frist), fristerSomSkaAviseras(listOf(frist), LocalDate.of(2027, 9, 5), emptySet()))
        assertTrue(fristerSomSkaAviseras(listOf(frist), LocalDate.of(2027, 9, 4), emptySet()).isEmpty())
    }
}
