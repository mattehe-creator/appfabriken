package se.tmconnect.husloggen.regler

/** Kostnad i öre för förbrukning i tusendelar av enheten, givet pris i öre per hel enhet. */
fun forbrukningKostnadOre(forbrukningTusendel: Long, prisOrePerEnhet: Long): Long? {
    if (forbrukningTusendel < 0 || prisOrePerEnhet < 0) return null
    return (forbrukningTusendel * prisOrePerEnhet + 500) / 1_000
}
