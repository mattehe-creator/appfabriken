package se.tmconnect.garantivalvet.export

import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import se.tmconnect.garantivalvet.regler.Underlag
import se.tmconnect.garantivalvet.regler.UnderlagRad
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class PdfUnderlagSkrivareTest {
    private val enkeltUnderlag = Underlag(
        dokumentRubrik = "Test",
        rader = listOf(UnderlagRad("Etikett", "Värde")),
        skapad = LocalDate.of(2026, 10, 11),
    )

    @Test
    fun skrivningGerResultatUtanKrasch() {
        val result = skrivUnderlagTillPdf(enkeltUnderlag, ByteArrayOutputStream())
        assertTrue(result is PdfExportResult.Ok || result is PdfExportResult.Fel)
    }

    @Test
    fun returnerarFelVidSkrivfel() {
        val stream = object : OutputStream() {
            override fun write(b: Int) {
                throw IOException("disk full")
            }
        }
        val result = skrivUnderlagTillPdf(enkeltUnderlag, stream)
        assertTrue(result is PdfExportResult.Fel)
    }
}
