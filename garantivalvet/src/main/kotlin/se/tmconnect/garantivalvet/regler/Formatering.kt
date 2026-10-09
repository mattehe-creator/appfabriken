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
    val formatter = java.text.NumberFormat.getCurrencyInstance(locale)
    formatter.minimumFractionDigits = 2
    formatter.maximumFractionDigits = 2
    
    val formattedPrice = formatter.format(prisKr)
    
    // Remove the currency symbol and add " kr"
    return formattedPrice.replace(Regex("[^0-9,\\.\\s]"), "").trim() + " kr"
}

fun formateraDatum(datum: LocalDate, locale: Locale = Locale.forLanguageTag("sv-SE")): String {
    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    return datum.format(formatter.withLocale(locale))
}
