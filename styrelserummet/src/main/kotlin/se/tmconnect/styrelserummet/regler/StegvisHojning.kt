package se.tmconnect.styrelserummet.regler

fun stegvisHojning(nuvarande: Long, mal: Long, antalAr: Int): List<Long>? {
    if (antalAr < 1 || antalAr > 10 || nuvarande < 0 || mal < 0) {
        return null
    }

    val antal = antalAr.toLong()
    val diff = mal - nuvarande
    return (1..antalAr).map { k ->
        nuvarande + avrundaKvot(diff * k, antal)
    }
}
