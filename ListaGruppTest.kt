package se.tmconnect.garantivalvet.regler

import org.junit.Test
import se.tmconnect.garantivalvet.data.Kop
import java.time.LocalDate

class ListaGruppTest {
    
    @Test
    fun testGarantiSnart() {
        val idag = LocalDate.of(2026, 10, 1)
        val kop = Kop(
            id = 1,
            vad = "Test",
            var = "Test",
            butik = "Testbutik",
            kopdatum = LocalDate.of(2025, 10, 1),
            pris = 100.0,
            garantiManader = 12,
            anteckning = "",
            kvittoBild = null
        )
        
        val grupp = listaGrupp(kop, idag)
        assert(grupp == ListaGrupp.GAR_UT_SNART)
    }
    
    @Test
    fun testGarantiGaller() {
        val idag = LocalDate.of(2026, 10, 1)
        val kop = Kop(
            id = 1,
            vad = "Test",
            var = "Test",
            butik = "Testbutik",
            kopdatum = LocalDate.of(2025, 10, 1),
            pris = 100.0,
            garantiManader = 24,
            anteckning = "",
            kvittoBild = null
        )
        
        val grupp = listaGrupp(kop, idag)
        assert(grupp == ListaGrupp.GALLER)
    }
    
    @Test
    fun testUtgangna() {
        val idag = LocalDate.of(2027, 10, 1)
        val kop = Kop(
            id = 1,
            vad = "Test",
            var = "Test",
            butik = "Testbutik",
            kopdatum = LocalDate.of(2025, 10, 1),
            pris = 100.0,
            garantiManader = 12,
            anteckning = "",
            kvittoBild = null
        )
        
        val grupp = listaGrupp(kop, idag)
        assert(grupp == ListaGrupp.UTGANGNA)
    }
}
