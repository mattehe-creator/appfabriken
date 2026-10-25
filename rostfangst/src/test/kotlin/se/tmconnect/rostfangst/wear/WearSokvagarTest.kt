package se.tmconnect.rostfangst.wear

import org.junit.Test
import org.junit.Assert.*

class WearSokvagarTest {

    @Test
    fun testArKandSokvag() {
        assertTrue(WearSokvagar.arKandSokvag("/rostfangst/fangst"))
        assertTrue(WearSokvagar.arKandSokvag("/rostfangst/forslag"))
        assertTrue(WearSokvagar.arKandSokvag("/rostfangst/utfor"))
        assertTrue(WearSokvagar.arKandSokvag("/rostfangst/klart"))

        assertFalse(WearSokvagar.arKandSokvag("/rostfangst/okand"))
        assertFalse(WearSokvagar.arKandSokvag("rostfangst/fangst"))
        assertFalse(WearSokvagar.arKandSokvag(""))
        assertFalse(WearSokvagar.arKandSokvag(null))
    }
}
