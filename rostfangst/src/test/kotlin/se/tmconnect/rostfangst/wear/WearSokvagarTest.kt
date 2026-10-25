package se.tmconnect.rostfangst.wear

import org.junit.Test
import org.junit.Assert.assertEquals

class WearSokvagarTest {
    
    @Test
    fun testArKandSokvag() {
        assertEquals(true, WearSokvagar.arKandSokvag("/rostfangst/fangst"))
        assertEquals(true, WearSokvagar.arKandSokvag("/rostfangst/forslag"))
        assertEquals(true, WearSokvagar.arKandSokvag("/rostfangst/utfor"))
        assertEquals(true, WearSokvagar.arKandSokvag("/rostfangst/klart"))
        assertEquals(false, WearSokvagar.arKandSokvag("/rostfangst/okand"))
        assertEquals(false, WearSokvagar.arKandSokvag("rostfangst/fangst"))
        assertEquals(false, WearSokvagar.arKandSokvag(""))
        assertEquals(false, WearSokvagar.arKandSokvag("/rostfangst/"))
        assertEquals(false, WearSokvagar.arKandSokvag("/rostfangst/fangst/extra"))
    }
}
