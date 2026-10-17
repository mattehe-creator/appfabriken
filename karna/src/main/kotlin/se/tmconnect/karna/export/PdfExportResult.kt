package se.tmconnect.karna.export

sealed class PdfExportResult {
    data object Ok : PdfExportResult()

    data class Fel(val meddelande: String) : PdfExportResult()
}
