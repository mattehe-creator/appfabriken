package se.tmconnect.flextid

import android.content.Intent
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [28])
class GenvagIntentTest {
    @Test
    fun tolkarStamplaIn() {
        val intent = Intent(GenvagIntent.ACTION_STAMPLA_IN)
        assertTrue(GenvagIntent.arStamplaIn(intent))
        assertTrue(GenvagIntent.arGenvag(intent))
        assertFalse(GenvagIntent.arStamplaUt(intent))
    }

    @Test
    fun tolkarStamplaUt() {
        val intent = Intent(GenvagIntent.ACTION_STAMPLA_UT)
        assertTrue(GenvagIntent.arStamplaUt(intent))
        assertTrue(GenvagIntent.arGenvag(intent))
        assertFalse(GenvagIntent.arStamplaIn(intent))
    }

    @Test
    fun vanligStartInteGenvag() {
        val intent = Intent(Intent.ACTION_MAIN)
        assertFalse(GenvagIntent.arGenvag(intent))
    }
}
