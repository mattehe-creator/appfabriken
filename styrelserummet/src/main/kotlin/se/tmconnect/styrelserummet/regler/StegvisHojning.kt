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
    // För att korrekt avrunda halva bort från noll, behöver vi hantera decimaler korrekt
    // Vi multiplicerar med 1000 för att få enkla heltal för avrundning
    val scaledValue = x * 1000
    
    // Om det är negativt, gör om till positivt för beräkning
    val absScaled = kotlin.math.abs(scaledValue)
    
    // Hitta resten vid division med 1000
    val remainder = absScaled % 1000
    
    // Om resten är >= 500, avrunda upp (bort från noll)
    // Annars avrunda ned
    val roundedScaled = if (remainder >= 500) {
        (absScaled / 1000) + 1
    } else {
        absScaled / 1000
    }
    
    // Om originalvärdet var negativt, gör om tillbaka till negativt
    return if (x < 0) {
        -roundedScaled
    } else {
        roundedScaled
    }
}
