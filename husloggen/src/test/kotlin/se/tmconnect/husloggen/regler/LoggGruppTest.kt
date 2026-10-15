package se.tmconnect.husloggen.regler

import org.junit.Test
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.PostTyp
import java.time.LocalDate

class LoggGruppTest {
    
    @Test
    fun `grupperaPerAr med exempeldata`() {
        val poster = listOf(
            Post(0, LocalDate.of(2025, 5, 1), "Vad 1", PostTyp.UNDERHALL, 100_000L),
            Post(0, LocalDate.of(2026, 3, 1), "Vad 2", PostTyp.REPARATION, 50_000L),
            Post(0, LocalDate.of(2026, 4, 1), "Vad 3", PostTyp.OMBYGGNAD, null)
        )
        
        val resultat = grupperaPerAr(poster)
        
        assert(resultat.size == 2)
        assert(resultat[0].ar == 2026)
        assert(resultat[0].poster.size == 2)
        assert(resultat[0].summaOre == 50_000L)
        assert(resultat[1].ar == 2025)
        assert(resultat[1].poster.size == 1)
        assert(resultat[1].summaOre == 100_000L)
    }
    
    @Test
    fun `grupperaPerAr med tom lista`() {
        val resultat = grupperaPerAr(emptyList())
        assert(resultat.isEmpty())
    }
    
    @Test
    fun `grupperaPerAr med endast poster utan belopp`() {
        val poster = listOf(
            Post(0, LocalDate.of(2025, 5, 1), "Vad 1", PostTyp.UNDERHALL, null),
            Post(0, LocalDate.of(2026, 3, 1), "Vad 2", PostTyp.REPARATION, null)
        )
        
        val resultat = grupperaPerAr(poster)
        
        assert(resultat.size == 2)
        assert(resultat[0].ar == 2026)
        assert(resultat[0].summaOre == 0L)
        assert(resultat[1].ar == 2025)
        assert(resultat[1].summaOre == 0L)
    }
}
