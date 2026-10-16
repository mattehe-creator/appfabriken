package se.tmconnect.husloggen.regler

/** Läser kolumnen `bilder` (kommaseparerade filnamn) till en lista. Tomma poster ignoreras. */
fun tolkaBilder(text: String?): List<String> =
    text.orEmpty().split(',').map { it.trim() }.filter { it.isNotEmpty() }

/** Gör om en lista filnamn till kolumnvärdet, `null` när listan är tom. */
fun formateraBilder(lista: List<String>): String? =
    lista.map { it.trim() }.filter { it.isNotEmpty() }
        .joinToString(",")
        .takeIf { it.isNotEmpty() }
