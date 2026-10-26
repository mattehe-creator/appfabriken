package se.tmconnect.rostfangst.lokal

fun inspelningenSlut(ms: Long, maxMs: Long): Boolean {
    return ms >= maxMs
}

fun formateraLangd(ms: Long): String {
    if (ms < 0) return "0:00"
    
    val sekunder = ms / 1000
    val minuter = sekunder / 60
    val sekunderRest = sekunder % 60
    
    return "$minuter:${sekunderRest.toString().padStart(2, '0')}"
}
