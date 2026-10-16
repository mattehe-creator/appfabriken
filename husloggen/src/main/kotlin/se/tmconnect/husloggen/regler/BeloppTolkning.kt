package se.tmconnect.husloggen.regler

private const val MAX_KRONOR = 99_999_999L
private const val MAX_ORE = MAX_KRONOR * 100

fun tolkaBeloppTillOre(text: String): Long? {
    val trimmed = text.trim()
    if (trimmed.isEmpty()) return null

    val normalized = trimmed.replace(" ", "")
    if (normalized.isEmpty()) return null
    if (normalized.first() == '-' || normalized.first() == '+') return null

    val comma = normalized.lastIndexOf(',')
    val dot = normalized.lastIndexOf('.')
    val decPos = maxOf(comma, dot)

    val intRaw: String
    val decRaw: String?

    if (decPos >= 0) {
        intRaw = normalized.substring(0, decPos)
        decRaw = normalized.substring(decPos + 1)
        if (decRaw.isEmpty() || decRaw.length > 2 || !decRaw.all { it.isDigit() }) return null
    } else {
        intRaw = normalized
        decRaw = null
    }

    val intClean = intRaw.replace(".", "").replace(",", "")
    if (intClean.isEmpty() || !intClean.all { it.isDigit() }) return null

    val kronor = intClean.toLongOrNull() ?: return null
    if (kronor > MAX_KRONOR) return null

    val oreDel = when (decRaw?.length) {
        null -> 0L
        1 -> decRaw.toLong() * 10
        else -> decRaw.toLong()
    }

    val totalOre = kronor * 100 + oreDel
    if (totalOre > MAX_ORE) return null
    return totalOre
}

fun beloppOreTillFaltText(ore: Long): String {
    val kronor = ore / 100
    val rest = ore % 100
    if (rest == 0L) return kronor.toString()
    val decimal = rest.toString().padStart(2, '0').trimEnd('0')
    return "$kronor,$decimal"
}
