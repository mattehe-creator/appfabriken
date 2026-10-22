package se.tmconnect.styrelserummet.regler

fun stegvisHojning(nuvarande: Long, mal: Long, antalAr: Int): List<Long>? {
    if (antalAr < 1 || antalAr > 10 || nuvarande < 0 || mal < 0) {
        return null
    }
    
    val result = mutableListOf<Long>()
    
    for (k in 1..antalAr) {
        // Beräkna värdet för år k: nuvarande + (mal - nuvarande) * k / antalAr
        val value = nuvarande + (mal - nuvarande) * k / antalAr
        // Avrunda till närmaste heltal, halva bort från noll
        val roundedValue = avrundaHalvaBortFranNoll(value)
        result.add(roundedValue)
    }
    
    return result
}

// Hjälpfunktion för avrundning: halva bort från noll
fun avrundaHalvaBortFranNoll(x: Long): Long {
    val absX = kotlin.math.abs(x)
    val decimalPart = absX % 1000
    val integerPart = absX / 1000
    
    // Om decimaldelen är 500 eller mer, avrunda upp (bort från noll)
    // Annars avrunda ned
    return if (decimalPart >= 500) {
        if (x >= 0) {
            (integerPart + 1) * 1000
        } else {
            (integerPart + 1) * 1000 * -1
        }
    } else {
        integerPart * 1000
    }
}
