package se.tmconnect.garantivalvet.regler

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

fun formateraPrisKr(prisOre: Long?, locale: Locale = Locale.forLanguageTag("sv-SE")): String {
    if (prisOre == null) {
        return ""
    }
    
    val prisKr = prisOre / 100.0
    
    // Format the number with appropriate thousands separator and decimal separator
    val formatter = java.text.NumberFormat.getNumberInstance(locale)
    formatter.minimumFractionDigits = 2
    formatter.maximumFractionDigits = 2
    
    val formattedNumber = formatter.format(prisKr)
    
    return "$formattedNumber kr"
}

fun formateraDatum(datum: LocalDate, locale: Locale = Locale.forLanguageTag("sv-SE")): String {
    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    return datum.format(formatter.withLocale(locale))
}
