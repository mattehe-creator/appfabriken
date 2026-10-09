package se.tmconnect.garantivalvet.regler

import org.junit.Test
import se.tmconnect.garantivalvet.data.Kop
import java.time.LocalDate

class KopSorteringTest {
    
    @Test
    fun testTreAktiva() {
        val idag = LocalDate.of(2026, 5, 1)
        val kop1 = Kop(vad = "A", kopdatum = LocalDate.of(2025, 1, 1), garantiManader = 12) // utgången
        val kop2 = Kop(vad = "B", kopdatum = LocalDate.of(2025, 6, 1), garantiManader = 12) // aktiv
        val kop3 = Kop(vad = "C", kopdatum = LocalDate.of(2025, 12, 1), garantiManader = 12) // aktiv
        
        val resultat = sorteraEfterGaranti(listOf(kop1, kop2, kop3), idag)
        
        // Förväntat: kop2 (slut 2026-06-01), kop3 (slut 2027-01-01), kop1 (slut 2026-01-01)
        assert(resultat[0].vad == "B")
        assert(resultat[1].vad == "C")
        assert(resultat[2].vad == "A")
    }
    
    @Test
    fun testAktivtOchUtgångna() {
        val idag = LocalDate.of(2026, 5, 1)
        val kop1 = Kop(vad = "A", kopdatum = LocalDate.of(2025, 1, 1), garantiManader = 12) // utgången
        val kop2 = Kop(vad = "B", kopdatum = LocalDate.of(2025, 6, 1), garantiManader = 12) // aktiv
        
        val resultat = sorteraEfterGaranti(listOf(kop1, kop2), idag)
        
        // Förväntat: kop2 (aktiv) före kop1 (utgången)
        assert(resultat[0].vad == "B")
        assert(resultat[1].vad == "A")
    }
    
    @Test
    fun testTvåUtgångna() {
        val idag = LocalDate.of(2026, 5, 1)
        val kop1 = Kop(vad = "A", kopdatum = LocalDate.of(2024, 1, 1), garantiManader = 12) // senast utgången
        val kop2 = Kop(vad = "B", kopdatum = LocalDate.of(2023, 6, 1), garantiManader = 12) // tidigare utgången
        
        val resultat = sorteraEfterGaranti(listOf(kop1, kop2), idag)
        
        // Förväntat: kop1 (senast utgången) före kop2 (tidigare utgången)
        assert(resultat[0].vad == "A")
        assert(resultat[1].vad == "B")
    }
    
    @Test
    fun testBlandade() {
        val idag = LocalDate.of(2026, 5, 1)
        val kop1 = Kop(vad = "A", kopdatum = LocalDate.of(2025, 1, 1), garantiManader = 12) // utgången
        val kop2 = Kop(vad = "B", kopdatum = LocalDate.of(2025, 6, 1), garantiManader = 12) // aktiv
        val kop3 = Kop(vad = "C", kopdatum = LocalDate.of(2024, 1, 1), garantiManader = 12) // utgången
        
        val resultat = sorteraEfterGaranti(listOf(kop1, kop2, kop3), idag)
        
        // Förväntat: kop2 (aktiv) före kop1 och kop3 (utgångna)
        assert(resultat[0].vad == "B")
        assert(resultat[1].vad == "A")
        assert(resultat[2].vad == "C")
    }
}
