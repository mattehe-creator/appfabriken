package se.tmconnect.garantivalvet.regler

import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.garantivalvet.data.Kop
import java.time.LocalDate

class KopSorteringTest {

    private fun slut(kop: Kop) = garantiSlut(kop.kopdatum, kop.garantiManader)

    private fun kop(vad: String, slut: LocalDate): Kop =
        Kop(
            vad = vad,
            kopdatum = slut.minusMonths(12),
            garantiManader = 12,
        )

    @Test
    fun aktivaSorterasMedNarmasteGarantislutForst() {
        val idag = LocalDate.of(2026, 5, 1)
        val lista = listOf(
            kop("tredje", LocalDate.of(2027, 1, 1)),
            kop("forsta", LocalDate.of(2026, 6, 1)),
            kop("andra", LocalDate.of(2026, 12, 1)),
        )

        val resultat = sorteraEfterGaranti(lista, idag)

        assertEquals(
            listOf(
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 12, 1),
                LocalDate.of(2027, 1, 1),
            ),
            resultat.map { slut(it) },
        )
    }

    @Test
    fun aktivtKopForeUtgangenGaranti() {
        val idag = LocalDate.of(2026, 5, 1)
        val utgangen = kop("utgangen", LocalDate.of(2025, 1, 1))
        val aktiv = kop("aktiv", LocalDate.of(2026, 12, 1))
        val lista = listOf(utgangen, aktiv)

        val resultat = sorteraEfterGaranti(lista, idag)

        assertEquals(listOf(aktiv, utgangen), resultat)
    }

    @Test
    fun utgangnaSorterasMedSenastUtgangnaSist() {
        val idag = LocalDate.of(2026, 5, 1)
        val aldre = kop("aldre", LocalDate.of(2023, 6, 1))
        val nyare = kop("nyare", LocalDate.of(2024, 1, 1))
        val lista = listOf(aldre, nyare)

        val resultat = sorteraEfterGaranti(lista, idag)

        assertEquals(listOf(nyare, aldre), resultat)
    }

    @Test
    fun vidLikaGarantislutSorterasEfterVad() {
        val idag = LocalDate.of(2026, 5, 1)
        val sammaSlut = LocalDate.of(2026, 12, 1)
        val lista = listOf(
            kop("Zebra", sammaSlut),
            kop("Apa", sammaSlut),
        )

        val resultat = sorteraEfterGaranti(lista, idag)

        assertEquals(listOf("Apa", "Zebra"), resultat.map { it.vad })
    }
}
