package se.tmconnect.flextid.regler

/**
 * Antal hela flexdagar som kan tas ut utan att [saldoMin] hamnar under [golvMin].
 * [golvMin] null betyder ingen golvgräns (resultat null).
 */
fun ledighetsutrymme(saldoMin: Int, golvMin: Int?, normaltidMin: Int): Int? {
    if (golvMin == null || normaltidMin <= 0) return null
    if (saldoMin <= golvMin) return 0
    return (saldoMin - golvMin) / normaltidMin
}
