package se.tmconnect.husloggen.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MatareValideringTest {
    @Test
    fun tolkaPrisOrePerEnhetAccepterarTomtOchHeltal() {
        assertNull(tolkaPrisOrePerEnhet(""))
        assertEquals(150L, tolkaPrisOrePerEnhet("150"))
        assertNull(tolkaPrisOrePerEnhet("1,5"))
    }

    @Test
    fun valideraMatarePrisOgiltigt() {
        assertEquals(MatareFel.PrisOgiltigt, valideraMatare("El", "kWh", "abc"))
    }
}
