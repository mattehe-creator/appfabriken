package se.tmconnect.karna.tillganglighet

import kotlin.math.pow

/**
 * WCAG 2.x kontrastkvot mellan två färger givna som `0xFFRRGGBB`.
 */
fun kontrastkvot(a: Long, b: Long): Double {
    val luminansA = relativLuminans(a)
    val luminansB = relativLuminans(b)
    val ljusare = maxOf(luminansA, luminansB)
    val morkare = minOf(luminansA, luminansB)
    return (ljusare + 0.05) / (morkare + 0.05)
}

private fun relativLuminans(farg: Long): Double {
    val rgb = farg and 0xFFFFFF
    val r = kanalTillLinjar((rgb shr 16) and 0xFF)
    val g = kanalTillLinjar((rgb shr 8) and 0xFF)
    val b = kanalTillLinjar(rgb and 0xFF)
    return 0.2126 * r + 0.7152 * g + 0.0722 * b
}

private fun kanalTillLinjar(kanal: Long): Double {
    val c = kanal / 255.0
    return if (c <= 0.03928) {
        c / 12.92
    } else {
        ((c + 0.055) / 1.055).pow(2.4)
    }
}
