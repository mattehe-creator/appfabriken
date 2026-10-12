package se.tmconnect.karna.tillganglighet

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.karna.tema.AppfabrikFarger

class KontrastTest {
    @Test
    fun svartMotVittGer21() {
        assertEquals(21.0, kontrastkvot(0xFF000000, 0xFFFFFFFF), 0.05)
    }

    @Test
    fun vittMotVittGer1() {
        assertEquals(1.0, kontrastkvot(0xFFFFFFFF, 0xFFFFFFFF), 0.001)
    }

    @Test
    fun textfargerMotBakgrundMinst45() {
        assertMinst45(AppfabrikFarger.LjusTextArgb, AppfabrikFarger.LjusBakgrundArgb)
        assertMinst45(AppfabrikFarger.LjusSekundarTextArgb, AppfabrikFarger.LjusBakgrundArgb)
        assertMinst45(AppfabrikFarger.LjusTextPaPrimarArgb, AppfabrikFarger.LjusPrimarArgb)
        assertMinst45(AppfabrikFarger.LjusTextPaPrimarContainerArgb, AppfabrikFarger.LjusPrimarContainerArgb)
        assertMinst45(AppfabrikFarger.LjusTextPaSekundarContainerArgb, AppfabrikFarger.LjusSekundarContainerArgb)
        assertMinst45(AppfabrikFarger.LjusTextPaTertiarContainerArgb, AppfabrikFarger.LjusTertiarContainerArgb)
        assertMinst45(AppfabrikFarger.LjusTextPaFelContainerArgb, AppfabrikFarger.LjusFelContainerArgb)
        assertMinst45(AppfabrikFarger.LjusTextArgb, AppfabrikFarger.LjusYtaArgb)
        assertMinst45(AppfabrikFarger.LjusSekundarTextArgb, AppfabrikFarger.LjusYtaVariantArgb)
        assertMinst45(AppfabrikFarger.LjusFelArgb, AppfabrikFarger.LjusBakgrundArgb)

        assertMinst45(AppfabrikFarger.MorkTextArgb, AppfabrikFarger.MorkBakgrundArgb)
        assertMinst45(AppfabrikFarger.MorkSekundarTextArgb, AppfabrikFarger.MorkBakgrundArgb)
        assertMinst45(AppfabrikFarger.MorkTextPaPrimarArgb, AppfabrikFarger.MorkPrimarArgb)
        assertMinst45(AppfabrikFarger.MorkTextPaPrimarContainerArgb, AppfabrikFarger.MorkPrimarContainerArgb)
        assertMinst45(AppfabrikFarger.MorkTextPaSekundarContainerArgb, AppfabrikFarger.MorkSekundarContainerArgb)
        assertMinst45(AppfabrikFarger.MorkTextPaTertiarContainerArgb, AppfabrikFarger.MorkTertiarContainerArgb)
        assertMinst45(AppfabrikFarger.MorkTextPaFelContainerArgb, AppfabrikFarger.MorkFelContainerArgb)
        assertMinst45(AppfabrikFarger.MorkTextArgb, AppfabrikFarger.MorkYtaArgb)
        assertMinst45(AppfabrikFarger.MorkSekundarTextArgb, AppfabrikFarger.MorkYtaVariantArgb)
        assertMinst45(AppfabrikFarger.MorkFelArgb, AppfabrikFarger.MorkBakgrundArgb)
    }

    private fun assertMinst45(text: Long, bakgrund: Long) {
        assertTrue(
            "Kontrast ${kontrastkvot(text, bakgrund)} under 4,5 för $text mot $bakgrund",
            kontrastkvot(text, bakgrund) >= 4.5,
        )
    }
}
