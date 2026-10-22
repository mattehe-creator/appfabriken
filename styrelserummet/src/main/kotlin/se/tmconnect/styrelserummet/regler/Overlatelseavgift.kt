package se.tmconnect.styrelserummet.regler

enum class Avgiftstyp {
    OVERLATELSE,
    PANTSATTNING,
    ANDRAHAND,
}

fun avgiftFranPromille(prisbasbelopp: Long, promilleAvPrisbasbelopp: Int): Long {
    if (prisbasbelopp < 0 || promilleAvPrisbasbelopp < 0) {
        throw IllegalArgumentException()
    }
    return prisbasbelopp * promilleAvPrisbasbelopp / 1000
}

fun hogstaOverlatelseavgift(prisbasbelopp: Long, data: Map<String, StammaRad>): Long {
    val rad = data["referens_overlatelse_0"] ?: throw IllegalArgumentException()
    return avgiftFranPromille(prisbasbelopp, rad.varde.toInt())
}

fun hogstaPantsattningsavgift(prisbasbelopp: Long, data: Map<String, StammaRad>): Long {
    val rad = data["referens_pantsattning_0"] ?: throw IllegalArgumentException()
    return avgiftFranPromille(prisbasbelopp, rad.varde.toInt())
}

fun hogstaAndrahandsavgift(prisbasbelopp: Long): Long =
    avgiftFranPromille(prisbasbelopp, 100)
