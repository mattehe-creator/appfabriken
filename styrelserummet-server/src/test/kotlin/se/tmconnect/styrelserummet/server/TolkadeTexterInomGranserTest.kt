package se.tmconnect.styrelserummet.server

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Forening
import se.tmconnect.styrelserummet.underlag.byggUtkast

class TolkadeTexterInomGranserTest {
    @Test
    fun utkastMedLangaTolkadeTexterGarAttSpara() {
        val json =
            """{"komponenter":[{"namn":"${"N".repeat(1000)}","grupp":"TAK","anskaffningsvarde":500000,"anskaffningsar":2010,"nyttjandeperiodAr":40,"kalla":"${"k".repeat(1000)}","sakerhet":"HOG"}],"anmarkningar":[]}"""
        val utkast =
            byggUtkast(
                underhallsplanText = "Tak 500 000 kr",
                arsredovisningText = "",
                forening = Forening(namn = "Brf Test", byggnadsar = 1970),
                tolkare = FalskTolkare(jsonSvar = json),
                nuAr = 2025,
            )
        val dto = tillUtkastDto(utkast, 2025)
        val k = dto.komponenter.single()
        assertEquals(MAX_TEXT_PER_FALT, k.namn.length)
        assertEquals(MAX_TEXT_PER_FALT, k.kalla.length)
        // Samma kontroll som PUT /rader gör: det oförändrade utkastet ska gå att spara.
        assertTrue(raderInomGranser(dto.komponenter, dto.arsslut, nuAr = 2025))
    }
}
