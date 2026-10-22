package se.tmconnect.husloggen.regler

enum class MatareFel {
    NamnSaknas,
    EnhetSaknas,
    PrisOgiltigt,
}

fun valideraMatare(namn: String, enhet: String): MatareFel? {
    if (namn.trim().isEmpty()) return MatareFel.NamnSaknas
    if (enhet.trim().isEmpty()) return MatareFel.EnhetSaknas
    return null
}
