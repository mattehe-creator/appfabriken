package se.tmconnect.styrelserummet.underlag

import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Komponentgrupp

class GrupperaNamnTest {

    @Test
    fun gissaGruppTakFasadFonster() {
        assertEquals(Komponentgrupp.TAK, gissaGrupp("Yttertak papp 2018"))
        assertEquals(Komponentgrupp.FASAD, gissaGrupp("Putsad fasad söder"))
        assertEquals(Komponentgrupp.FONSTER_DORRAR, gissaGrupp("Entrédörr aluminium"))
        assertEquals(null, gissaGrupp("Stomme i betong"))
        assertEquals(Komponentgrupp.FONSTER_DORRAR, gissaGrupp("Nya fönster väster"))
        assertEquals(Komponentgrupp.TAK, gissaGrupp("Tegeltak nord"))
        assertEquals(Komponentgrupp.FASAD, gissaGrupp("Fasadtegel öst"))
    }
}
