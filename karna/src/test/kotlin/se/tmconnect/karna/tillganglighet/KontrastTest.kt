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
        assertMinst45(AppfabrikFarger.LjusSekundarTextArgb, AppfabrikFarger.LjusSurfaceContainerLowArgb)
        assertMinst45(AppfabrikFarger.LjusPrimarArgb, AppfabrikFarger.LjusYtaArgb)
        assertMinst45(AppfabrikFarger.LjusFelArgb, AppfabrikFarger.LjusBakgrundArgb)
        assertMinst45(AppfabrikFarger.LjusFelArgb, AppfabrikFarger.LjusYtaArgb)

        assertMinst45(AppfabrikFarger.MorkTextArgb, AppfabrikFarger.MorkBakgrundArgb)
        assertMinst45(AppfabrikFarger.MorkSekundarTextArgb, AppfabrikFarger.MorkBakgrundArgb)
        assertMinst45(AppfabrikFarger.MorkTextPaPrimarArgb, AppfabrikFarger.MorkPrimarArgb)
        assertMinst45(AppfabrikFarger.MorkTextPaPrimarContainerArgb, AppfabrikFarger.MorkPrimarContainerArgb)
        assertMinst45(AppfabrikFarger.MorkTextPaSekundarContainerArgb, AppfabrikFarger.MorkSekundarContainerArgb)
        assertMinst45(AppfabrikFarger.MorkTextPaTertiarContainerArgb, AppfabrikFarger.MorkTertiarContainerArgb)
        assertMinst45(AppfabrikFarger.MorkTextPaFelContainerArgb, AppfabrikFarger.MorkFelContainerArgb)
        assertMinst45(AppfabrikFarger.MorkTextArgb, AppfabrikFarger.MorkYtaArgb)
        assertMinst45(AppfabrikFarger.MorkSekundarTextArgb, AppfabrikFarger.MorkYtaVariantArgb)
        assertMinst45(AppfabrikFarger.MorkSekundarTextArgb, AppfabrikFarger.MorkSurfaceContainerLowArgb)
        assertMinst45(AppfabrikFarger.MorkPrimarArgb, AppfabrikFarger.MorkYtaArgb)
        assertMinst45(AppfabrikFarger.MorkFelArgb, AppfabrikFarger.MorkBakgrundArgb)
        assertMinst45(AppfabrikFarger.MorkFelArgb, AppfabrikFarger.MorkYtaArgb)

        statusChipPar().forEach { (text, bakgrund, namn) ->
            assertTrue(
                "Statuschip $namn: kontrast ${kontrastkvot(text, bakgrund)} under 4,5",
                kontrastkvot(text, bakgrund) >= 4.5,
            )
        }
    }

    /** Text/bakgrund för statuschips (samma par som [se.tmconnect.karna.tema.statusChipFarger]). */
    private fun statusChipPar(): List<Triple<Long, Long, String>> = listOf(
        Triple(AppfabrikFarger.LjusTextPaPrimarContainerArgb, AppfabrikFarger.LjusPrimarContainerArgb, "ljus GALLER"),
        Triple(AppfabrikFarger.LjusTextPaTertiarContainerArgb, AppfabrikFarger.LjusTertiarContainerArgb, "ljus SNART"),
        Triple(AppfabrikFarger.LjusTextPaSekundarContainerArgb, AppfabrikFarger.LjusSekundarContainerArgb, "ljus REKLAMATION"),
        Triple(AppfabrikFarger.LjusTextPaFelContainerArgb, AppfabrikFarger.LjusFelContainerArgb, "ljus UTGANGEN"),
        Triple(AppfabrikFarger.MorkTextPaPrimarContainerArgb, AppfabrikFarger.MorkPrimarContainerArgb, "mörk GALLER"),
        Triple(AppfabrikFarger.MorkTextPaTertiarContainerArgb, AppfabrikFarger.MorkTertiarContainerArgb, "mörk SNART"),
        Triple(AppfabrikFarger.MorkTextPaSekundarContainerArgb, AppfabrikFarger.MorkSekundarContainerArgb, "mörk REKLAMATION"),
        Triple(AppfabrikFarger.MorkTextPaFelContainerArgb, AppfabrikFarger.MorkFelContainerArgb, "mörk UTGANGEN"),
    )

    private fun assertMinst45(text: Long, bakgrund: Long) {
        assertTrue(
            "Kontrast ${kontrastkvot(text, bakgrund)} under 4,5 för $text mot $bakgrund",
            kontrastkvot(text, bakgrund) >= 4.5,
        )
    }
}
