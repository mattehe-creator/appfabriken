package se.tmconnect.husloggen.regler

private const val MAX_HELTAL = 999_999_999L
private const val TUSENDEL_PER_ENHET = 1_000L

fun tolkaMatareVardeTillTusendel(text: String): Long? {
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
        if (decRaw.isEmpty() || decRaw.length > 3 || !decRaw.all { it.isDigit() }) return null
    } else {
        intRaw = normalized
        decRaw = null
    }

    val intClean = intRaw.replace(".", "").replace(",", "")
    if (intClean.isEmpty() || !intClean.all { it.isDigit() }) return null

    val heltal = intClean.toLongOrNull() ?: return null
    if (heltal > MAX_HELTAL) return null

    val tusendelDel =
        when (decRaw?.length) {
            null -> 0L
            1 -> decRaw.toLong() * 100
            2 -> decRaw.toLong() * 10
            else -> decRaw.toLong()
        }

    return heltal * TUSENDEL_PER_ENHET + tusendelDel
}

fun matareVardeTillFaltText(tusendel: Long): String {
    val heltal = tusendel / TUSENDEL_PER_ENHET
    val rest = tusendel % TUSENDEL_PER_ENHET
    if (rest == 0L) return heltal.toString()
    val decimal = rest.toString().padStart(3, '0').trimEnd('0')
    return "$heltal,$decimal"
}

fun formateraMatareVarde(tusendel: Long): String = matareVardeTillFaltText(tusendel)
