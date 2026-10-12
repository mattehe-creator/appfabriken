package se.tmconnect.garantivalvet.regler

import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.garantivalvet.data.Kop
import java.time.LocalDate

class ListaGruppTest {

    @Test
    fun garantiUtgarSnartHamnarIGarUtSnart() {
        val kop = Kop(
            vad = "Test",
            kopdatum = LocalDate.of(2024, 1, 10),
            garantiManader = 24,
        )
        val idag = LocalDate.of(2025, 12, 20)

        assertEquals(ListaGrupp.GAR_UT_SNART, listaGrupp(kop, idag))
    }

    @Test
    fun reklamationGallerEfterGarantinHamnarIGaller() {
        val kop = Kop(
            vad = "Test",
            kopdatum = LocalDate.of(2024, 1, 10),
            garantiManader = 24,
        )
        val idag = LocalDate.of(2026, 2, 1)

        assertEquals(ListaGrupp.GALLER, listaGrupp(kop, idag))
    }

    @Test
    fun alltUtgangenHamnarIUtgangna() {
        val kop = Kop(
            vad = "Test",
            kopdatum = LocalDate.of(2024, 1, 10),
            garantiManader = 24,
        )
        val idag = LocalDate.of(2027, 1, 11)

        assertEquals(ListaGrupp.UTGANGNA, listaGrupp(kop, idag))
    }
}
