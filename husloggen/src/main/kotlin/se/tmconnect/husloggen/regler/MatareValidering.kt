package se.tmconnect.husloggen.regler

enum class MatareFel {
    NamnSaknas,
    EnhetSaknas,
    PrisOgiltigt,
}

fun tolkaPrisOrePerEnhet(text: String): Long? {
    val trimmed = text.trim()
    if (trimmed.isEmpty()) return null
    if (!trimmed.all { it.isDigit() }) return null
    return trimmed.toLongOrNull()
}

fun valideraMatare(namn: String, enhet: String, prisOrePerEnhetText: String = ""): MatareFel? {
    if (namn.trim().isEmpty()) return MatareFel.NamnSaknas
    if (enhet.trim().isEmpty()) return MatareFel.EnhetSaknas
    if (prisOrePerEnhetText.isNotBlank() && tolkaPrisOrePerEnhet(prisOrePerEnhetText) == null) {
        return MatareFel.PrisOgiltigt
    }
    return null
}
