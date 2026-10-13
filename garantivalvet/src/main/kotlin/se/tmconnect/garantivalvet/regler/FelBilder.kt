package se.tmconnect.garantivalvet.regler

/** Högst så många bilder på felet per köp, så att mejlet och säkerhetskopian håller sig lagom stora. */
const val MAX_FEL_BILDER = 5

/** Läser kolumnen `fel_bilder` (kommaseparerade filnamn) till en lista. Tomma poster ignoreras. */
fun lasFelBilder(lagrat: String?): List<String> =
    lagrat.orEmpty().split(',').map { it.trim() }.filter { it.isNotEmpty() }

/** Gör om en lista filnamn till kolumnvärdet, `null` när listan är tom. */
fun skrivFelBilder(filnamn: List<String>): String? =
    filnamn.map { it.trim() }.filter { it.isNotEmpty() }.distinct().take(MAX_FEL_BILDER)
        .joinToString(",").takeIf { it.isNotEmpty() }

fun kanLaggaTillFelBild(lagrat: String?): Boolean = lasFelBilder(lagrat).size < MAX_FEL_BILDER
