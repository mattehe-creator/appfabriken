package se.tmconnect.styrelserummet.regler

fun extraRantekostnadPerAr(skulder: Long, hojningTiondelar: Int): Long {
    require(skulder >= 0) { "Skulderna kan inte vara negativa." }
    require(hojningTiondelar in 0..200) { "Räntehöjningen ska vara 0–20 procentenheter." }
    return avrundaKvot(skulder * hojningTiondelar, 1000)
}

fun extraPerKvmOchManad(extraPerAr: Long, kvmBostadsratt: Long): Long? =
    if (kvmBostadsratt <= 0) null else avrundaKvot(extraPerAr, kvmBostadsratt * 12)

fun avgiftshojningTiondelsProcent(skulder: Long, arsavgifter: Long, hojningTiondelar: Int): Long? =
    if (arsavgifter <= 0) null else avrundaKvot(skulder * hojningTiondelar, arsavgifter)
