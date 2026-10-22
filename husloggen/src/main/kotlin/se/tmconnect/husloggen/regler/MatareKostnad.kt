package se.tmconnect.husloggen.regler

fun forbrukningKostnadOre(forbrukningTusendel: Long, prisOrePerEnhet: Long): Long? {
    if (forbrukningTusendel < 0 || prisOrePerEnhet < 0) return null
    return (forbrukningTusendel * prisOrePerEnhet + 500) / 1000
}
