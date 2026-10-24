package se.tmconnect.husloggen.export

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import se.tmconnect.husloggen.R

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class HusHistorikKallorTextTest {
    private fun kallorText(): String {
        val context = ApplicationProvider.getApplicationContext<Context>()
        return skatteKallorText(
            lagRad = context.getString(R.string.historik_pdf_kallor_lag),
            kontrolleradRad = { datum -> context.getString(R.string.historik_pdf_kallor_kontrollerat, datum) },
        )
    }

    @Test
    @Config(qualifiers = "en")
    fun engelskaKallorStarPaEngelska() {
        val text = kallorText()
        assertTrue(text, text.contains("Swedish Income Tax Act (Inkomstskattelagen)"))
        assertTrue(text, text.contains("Checked 2026-10-17."))
        assertFalse(text, text.contains("Kontrollerat"))
        assertFalse(text, text.contains("kap."))
    }

    @Test
    @Config(qualifiers = "sv")
    fun svenskaKallorStarPaSvenska() {
        val text = kallorText()
        assertTrue(text, text.contains("Inkomstskattelagen: 45 kap. (avyttring av fastigheter), 46 kap. (avyttring av bostadsrätter)."))
        assertTrue(text, text.contains("Kontrollerat 2026-10-17."))
        assertTrue(text, text.contains("Hjälpblankett SKV 2197"))
    }

    @Test
    fun parametrarnaAnvandsOforandrade() {
        val text = skatteKallorText(lagRad = "LAG.", kontrolleradRad = { "KOLL $it." })
        assertTrue(text, text.endsWith("LAG. KOLL 2026-10-17."))
    }
}
