package se.tmconnect.styrelserummet.underlag

import org.junit.jupiter.api.Test
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import org.junit.jupiter.api.Assertions.assertEquals

class GrupperaNamnTest {
    
    @Test
    fun `gissaGruppTakFasadFonster`() {
        assertEquals(Komponentgrupp.TAK, gissaGrupp("Yttertak papp 2018"))
        assertEquals(Komponentgrupp.FASAD, gissaGrupp("Putsad fasad söder"))
        assertEquals(Komponentgrupp.FONSTER_DORRAR, gissaGrupp("Entrédörr aluminium"))
        assertEquals(null, gissaGrupp("Stomme i betong"))
    }
}
