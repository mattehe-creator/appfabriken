package se.tmconnect.husloggen.regler

import org.junit.Test
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.PostTyp
import java.time.LocalDate

class LoggFilterTest {
    
    @Test
    fun testFiltreraPoster() {
        val poster = listOf(
            Post(id = 1, bostadId = 1L, datum = LocalDate.now(), vad = "Service värmepump", typ = PostTyp.REPARATION, utforare = "Kyl & Värme AB"),
            Post(id = 2, bostadId = 1L, datum = LocalDate.now(), vad = "Målning fasad söder", typ = PostTyp.UNDERHALL, anteckning = "Två lager"),
            Post(id = 3, bostadId = 1L, datum = LocalDate.now(), vad = "Ny altan", typ = PostTyp.OMBYGGNAD, utforare = "Byggfirma")
        )
        
        // Testa ("värmepump", null) → [1]
        val resultat1 = filtreraPoster(poster, "värmepump", null)
        assert(resultat1.size == 1)
        assert(resultat1[0].id == 1L)
        
        // Testa ("VÄRME kyl", null) → [1]
        val resultat2 = filtreraPoster(poster, "VÄRME kyl", null)
        assert(resultat2.size == 1)
        assert(resultat2[0].id == 1L)
        
        // Testa ("byggfirma", OMBYGGNAD) → [3]
        val resultat3 = filtreraPoster(poster, "byggfirma", PostTyp.OMBYGGNAD)
        assert(resultat3.size == 1)
        assert(resultat3[0].id == 3L)
        
        // Testa ("", UNDERHALL) → [2]
        val resultat4 = filtreraPoster(poster, "", PostTyp.UNDERHALL)
        assert(resultat4.size == 1)
        assert(resultat4[0].id == 2L)
        
        // Testa ("lager fasad", null) → [2]
        val resultat5 = filtreraPoster(poster, "lager fasad", null)
        assert(resultat5.size == 1)
        assert(resultat5[0].id == 2L)
        
        // Testa ("altan", REPARATION) → []
        val resultat6 = filtreraPoster(poster, "altan", PostTyp.REPARATION)
        assert(resultat6.isEmpty())
    }
}
