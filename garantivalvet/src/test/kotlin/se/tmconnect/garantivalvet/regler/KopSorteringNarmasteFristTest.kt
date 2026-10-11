package se.tmconnect.garantivalvet.regler

import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.garantivalvet.data.Kop
import java.time.LocalDate

class KopSorteringNarmasteFristTest {

    private fun kop(vad: String, kopdatum: LocalDate, garantiManader: Int): Kop =
        Kop(
            vad = vad,
            kopdatum = kopdatum,
            garantiManader = garantiManader,
        )

    @Test
    fun aktivaKopSorterasEfterNarmasteFrist() {
        val idag = LocalDate.of(2026, 5, 1)
        
        // Köp med garantislut om 10 dagar
        val kop1 = kop("första", LocalDate.of(2025, 5, 1), 24)
        // Köp med garantislut om 40 dagar  
        val kop2 = kop("andra", LocalDate.of(2025, 6, 1), 24)
        
        val lista = listOf(kop2, kop1)
        val resultat = sorteraEfterNarmasteFrist(lista, idag)
        
        assertEquals(listOf(kop1, kop2), resultat)
    }

    @Test
    fun utgångnaKopSorterasEfterSenasteFrist() {
        val idag = LocalDate.of(2026, 5, 1)
        
        // Köp med garanti utgången men reklamation om 200 dagar
        val kop1 = kop("reklamation", LocalDate.of(2024, 5, 1), 12)
        // Köp helt utgånget (reklamationsslut före idag, inte på sista dagen)
        val kop2 = kop("utgångna", LocalDate.of(2023, 4, 30), 12)
        
        val lista = listOf(kop2, kop1)
        val resultat = sorteraEfterNarmasteFrist(lista, idag)
        
        assertEquals(listOf(kop1, kop2), resultat)
    }

    @Test
    fun kopFöreIkraftträde() {
        val idag = LocalDate.of(2026, 5, 1)
        
        // Köp före ikraftträdande (ingen reklamation)
        val kop1 = kop("utgångna", LocalDate.of(2022, 4, 30), 12)
        // Aktivt köp
        val kop2 = kop("aktiv", LocalDate.of(2025, 5, 1), 24)
        
        val lista = listOf(kop1, kop2)
        val resultat = sorteraEfterNarmasteFrist(lista, idag)
        
        assertEquals(listOf(kop2, kop1), resultat)
    }

    @Test
    fun sammaFristSorterasEfterVad() {
        val idag = LocalDate.of(2026, 5, 1)
        
        val sammaSlut = LocalDate.of(2027, 5, 1)
        val kop1 = kop("Zebra", sammaSlut.minusMonths(24), 24)
        val kop2 = kop("Apa", sammaSlut.minusMonths(24), 24)
        
        val lista = listOf(kop2, kop1)
        val resultat = sorteraEfterNarmasteFrist(lista, idag)
        
        assertEquals(listOf(kop2, kop1), resultat)
    }
}
