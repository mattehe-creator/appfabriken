package se.tmconnect.rostfangst.lokal

import java.util.regex.Pattern

sealed interface AdressBedomning {
    data class Ogiltig(val skal: String) : AdressBedomning
    object Krypterad : AdressBedomning
    object OkrypteradLokal : AdressBedomning
    data class Avvisad(val skal: String) : AdressBedomning
}

fun bedomAdress(text: String): AdressBedomning {
    val trimmad = text.trim()
    if (trimmad.isEmpty()) return AdressBedomning.Ogiltig("tom")

    val schemaMatch = Pattern.compile("^(https?://)", Pattern.CASE_INSENSITIVE).matcher(trimmad)
    if (!schemaMatch.find()) return AdressBedomning.Ogiltig("saknar_schema")

    val schema = schemaMatch.group(1)
    val rest = trimmad.substring(schema.length)
    
    val värde = rest.split('/', ':').firstOrNull().orEmpty()
    if (värde.isEmpty()) return AdressBedomning.Ogiltig("saknar_vard")

    if (schema.equals("https://", ignoreCase = true)) {
        return AdressBedomning.Krypterad
    }

    // schema är nu "http://"
    if (värde == "localhost" || värde.endsWith(".local")) {
        return AdressBedomning.OkrypteradLokal
    }

    // Kontrollera IPv4-adress
    val ipv4Pattern = Regex("""^(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})$""")
    val match = ipv4Pattern.matchEntire(värde)
    if (match != null) {
        val groups = match.groupValues.drop(1).map { it.toInt() }
        if (groups.size == 4) {
            val (a, b, c, d) = groups
            if (a in 0..255 && b in 0..255 && c in 0..255 && d in 0..255) {
                // Kontrollera om det är en lokal adress
                if (a == 10) return AdressBedomning.OkrypteradLokal
                if (a == 192 && b == 168) return AdressBedomning.OkrypteradLokal
                if (a == 172 && b in 16..31) return AdressBedomning.OkrypteradLokal
            }
        }
    }

    return AdressBedomning.Avvisad("okrypterad_utanfor_lokalt_nat")
}
