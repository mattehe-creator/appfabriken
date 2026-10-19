package se.tmconnect.styrelserummet.pdf

import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper

const val INGEN_LASBAR_TEXT =
    "Filen innehåller ingen läsbar text, ladda upp en PDF som går att markera text i"

class PdfLasare {
    fun lasPerSida(bytes: ByteArray): Result<List<String>> {
        return runCatching {
            Loader.loadPDF(bytes).use { dokument ->
                val stripper = PDFTextStripper()
                (1..dokument.numberOfPages).map { sida ->
                    stripper.startPage = sida
                    stripper.endPage = sida
                    stripper.getText(dokument).trim()
                }
            }
        }
    }

    fun harLasbarText(bytes: ByteArray): Boolean {
        val sidor = lasPerSida(bytes).getOrNull() ?: return false
        return sidor.any { it.isNotBlank() }
    }

    fun lasSomText(bytes: ByteArray): Result<String> {
        val sidor = lasPerSida(bytes).getOrElse { return Result.failure(it) }
        if (sidor.none { it.isNotBlank() }) {
            return Result.failure(IngenLasbarTextException(INGEN_LASBAR_TEXT))
        }
        return Result.success(sidor.joinToString("\u000C"))
    }
}

class IngenLasbarTextException(val meddelande: String) : Exception(meddelande)
