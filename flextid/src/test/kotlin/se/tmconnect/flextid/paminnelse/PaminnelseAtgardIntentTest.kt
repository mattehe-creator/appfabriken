package se.tmconnect.flextid.paminnelse

import android.content.Intent
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [28])
class PaminnelseAtgardIntentTest {
    @Test
    fun tolkarStamplaIn() {
        val intent = Intent(PaminnelseAtgardIntent.ACTION_STAMPLA_IN)
        assertTrue(PaminnelseAtgardIntent.arStamplaIn(intent))
        assertTrue(PaminnelseAtgardIntent.arKandAtgard(intent))
        assertFalse(PaminnelseAtgardIntent.arStamplaUt(intent))
    }

    @Test
    fun tolkarStamplaUt() {
        val intent = Intent(PaminnelseAtgardIntent.ACTION_STAMPLA_UT)
        assertTrue(PaminnelseAtgardIntent.arStamplaUt(intent))
        assertTrue(PaminnelseAtgardIntent.arKandAtgard(intent))
        assertFalse(PaminnelseAtgardIntent.arStamplaIn(intent))
    }

    @Test
    fun okandAtgardIgnoreras() {
        val intent = Intent("se.tmconnect.flextid.action.ANNAT")
        assertFalse(PaminnelseAtgardIntent.arKandAtgard(intent))
        assertFalse(PaminnelseAtgardIntent.arStamplaIn(intent))
        assertFalse(PaminnelseAtgardIntent.arStamplaUt(intent))
    }

    @Test
    fun genvagAtgardArIntePaminnelseAtgard() {
        val intent = Intent(se.tmconnect.flextid.GenvagIntent.ACTION_STAMPLA_IN)
        assertFalse(PaminnelseAtgardIntent.arKandAtgard(intent))
    }
}
