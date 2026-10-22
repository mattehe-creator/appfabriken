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
        val result1 = filtreraPoster(poster, "värmepump", null)
        assert(result1.size == 1)
        assert(result1[0].id == 1L)
        
        // Testa ("VÄRME kyl", null) → [1]
        val result2 = filtreraPoster(poster, "VÄRME kyl", null)
        assert(result2.size == 1)
        assert(result2[0].id == 1L)
        
        // Testa ("byggfirma", OMBYGGNAD) → [3]
        val result3 = filtreraPoster(poster, "byggfirma", PostTyp.OMBYGGNAD)
        assert(result3.size == 1)
        assert(result3[0].id == 3L)
        
        // Testa ("", UNDERHALL) → [2]
        val result4 = filtreraPoster(poster, "", PostTyp.UNDERHALL)
        assert(result4.size == 1)
        assert(result4[0].id == 2L)
        
        // Testa ("lager fasad", null) → [2]
        val result5 = filtreraPoster(poster, "lager fasad", null)
        assert(result5.size == 1)
        assert(result5[0].id == 2L)
        
        // Testa ("altan", REPARATION) → []
        val result6 = filtreraPoster(poster, "altan", PostTyp.REPARATION)
        assert(result6.isEmpty())
    }
}
