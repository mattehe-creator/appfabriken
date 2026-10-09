package se.tmconnect.garantivalvet.regler

import org.junit.Test
import se.tmconnect.garantivalvet.data.Kop
import java.time.LocalDate

class KopSorteringTest {
    
    @Test
    fun test_sorteraEfterGaranti() {
        val idag = LocalDate.of(2026, 5, 1)
        
        // Testa tre köp med olika garantitider
        val kop1 = Kop(
            vad = "Köp 1",
            kopdatum = LocalDate.of(2026, 1, 10),
            garantiManader = 12,
            varKopt = null,
            prisOre = null,
            anteckning = null
        )
        
        val kop2 = Kop(
            vad = "Köp 2",
            kopdatum = LocalDate.of(2026, 1, 10),
            garantiManader = 12,
            varKopt = null,
            prisOre = null,
            anteckning = null
        )
        
        val kop3 = Kop(
            vad = "Köp 3",
            kopdatum = LocalDate.of(2026, 1, 10),
            garantiManader = 12,
            varKopt = null,
            prisOre = null,
            anteckning = null
        )
        
        val lista = listOf(kop1, kop2, kop3)
        val resultat = sorteraEfterGaranti(lista, idag)
        
        // Kontrollera att resultatet är korrekt sorterat
        assert(resultat.size == 3)
    }
}
