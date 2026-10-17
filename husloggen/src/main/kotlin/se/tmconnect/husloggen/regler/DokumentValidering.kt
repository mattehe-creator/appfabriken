package se.tmconnect.husloggen.regler

enum class DokumentFel {
    TitelSaknas,
    FilSaknas,
}

fun valideraDokument(titel: String, harFil: Boolean): DokumentFel? =
    when {
        titel.isBlank() -> DokumentFel.TitelSaknas
        !harFil -> DokumentFel.FilSaknas
        else -> null
    }
