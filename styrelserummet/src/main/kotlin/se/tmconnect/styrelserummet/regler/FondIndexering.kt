package se.tmconnect.styrelserummet.regler

fun indexeratBelopp(belopp: Long, ar: Int, basAr: Int, okningTiondelsProcent: Int): Long {
    if (okningTiondelsProcent < 0 || okningTiondelsProcent > 200) {
        throw IllegalArgumentException("okningTiondelsProcent måste vara mellan 0 och 200")
    }
    
    if (ar <= basAr) {
        return belopp
    }
    
    var result = belopp
    for (i in 0 until (ar - basAr)) {
        // Använd formeln: v = (v * (1000 + t) * 2 + 1000) / 2000
        // Detta är en avrundningsformel som ger samma resultat som (v * (1000 + t) + 500) / 1000
        result = (result * (1000 + okningTiondelsProcent) * 2 + 1000) / 2000
    }
    
    return result
}

fun indexeradeAtgarder(atgarder: List<PlaneradAtgard>, basAr: Int, okningTiondelsProcent: Int): List<PlaneradAtgard> {
    return atgarder.map { atgard ->
        val indexeratBelopp = indexeratBelopp(atgard.belopp, atgard.ar, basAr, okningTiondelsProcent)
        PlaneradAtgard(atgard.ar, indexeratBelopp, atgard.beskrivning)
    }
}
