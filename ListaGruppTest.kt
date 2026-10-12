package se.tmconnect.garantivalvet.regler

import se.tmconnect.garantivalvet.data.Kop
import java.time.LocalDate

class ListaGruppTest {
    
    // Testa att en köp med garantitid som går ut snart hamnar i rätt grupp
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
    
    // Testa att en köp med aktiv garantirätt hamnar i rätt grupp
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
    
    // Testa att en köp med utgången garantirätt hamnar i rätt grupp
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
