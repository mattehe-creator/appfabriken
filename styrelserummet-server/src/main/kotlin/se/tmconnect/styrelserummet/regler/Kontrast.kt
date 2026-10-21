package se.tmconnect.styrelserummet.regler

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.max
import kotlin.math.min

fun kontrastforhallande(fg: String, bg: String): Double {
    val fgRgb = hexToRgb(fg)
    val bgRgb = hexToRgb(bg)
    
    val fgLuminance = relativLuminans(fgRgb)
    val bgLuminance = relativLuminans(bgRgb)
    
    val ratio = if (fgLuminance > bgLuminance) {
        (fgLuminance + 0.05) / (bgLuminance + 0.05)
    } else {
        (bgLuminance + 0.05) / (fgLuminance + 0.05)
    }
    
    return BigDecimal(ratio).setScale(1, RoundingMode.HALF_UP).toDouble()
}

private fun hexToRgb(hex: String): Triple<Double, Double, Double> {
    val cleanHex = hex.removePrefix("#")
    
    val rgb = when (cleanHex.length) {
        3 -> {
            // #rgb format
            val r = cleanHex[0].toString().repeat(2)
            val g = cleanHex[1].toString().repeat(2)
            val b = cleanHex[2].toString().repeat(2)
            listOf(r, g, b)
        }
        6 -> {
            // #rrggbb format
            listOf(
                cleanHex.substring(0, 2),
                cleanHex.substring(2, 4),
                cleanHex.substring(4, 6)
            )
        }
        else -> throw IllegalArgumentException("Ogiltigt hex-format: $hex")
    }
    
    return Triple(
        rgb[0].toInt(16) / 255.0,
        rgb[1].toInt(16) / 255.0,
        rgb[2].toInt(16) / 255.0
    )
}

private fun relativLuminans(rgb: Triple<Double, Double, Double>): Double {
    val (r, g, b) = rgb
    
    val rLinear = if (r <= 0.03928) r / 12.92 else Math.pow((r + 0.055) / 1.055, 2.4)
    val gLinear = if (g <= 0.03928) g / 12.92 else Math.pow((g + 0.055) / 1.055, 2.4)
    val bLinear = if (b <= 0.03928) b / 12.92 else Math.pow((b + 0.055) / 1.055, 2.4)
    
    return 0.2126 * rLinear + 0.7152 * gLinear + 0.0722 * bLinear
}
