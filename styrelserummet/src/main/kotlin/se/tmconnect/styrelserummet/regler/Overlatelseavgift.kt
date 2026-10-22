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

fun avgiftarGiltig(
    avgift: Long,
    prisbasbelopp: Long,
    typ: Avgiftstyp,
    data: Map<String, StammaRad>,
): Boolean {
    val grans = when (typ) {
        Avgiftstyp.OVERLATELSE -> hogstaOverlatelseavgift(prisbasbelopp, data)
        Avgiftstyp.PANTSATTNING -> hogstaPantsattningsavgift(prisbasbelopp, data)
        Avgiftstyp.ANDRAHAND -> hogstaAndrahandsavgift(prisbasbelopp)
    }
    return avgift <= grans
}

fun avgiftFelmeddelande(
    avgift: Long,
    prisbasbelopp: Long,
    typ: Avgiftstyp,
    data: Map<String, StammaRad>,
): String? {
    if (avgiftarGiltig(avgift, prisbasbelopp, typ, data)) {
        return null
    }
    val grans = when (typ) {
        Avgiftstyp.OVERLATELSE -> hogstaOverlatelseavgift(prisbasbelopp, data)
        Avgiftstyp.PANTSATTNING -> hogstaPantsattningsavgift(prisbasbelopp, data)
        Avgiftstyp.ANDRAHAND -> hogstaAndrahandsavgift(prisbasbelopp)
    }
    return when (typ) {
        Avgiftstyp.OVERLATELSE ->
            "Överlåtelseavgiften $avgift kr överstiger gränsen $grans kr enligt föreningens stadgar " +
                "(referensvärde, inte lagtak)."
        Avgiftstyp.PANTSATTNING ->
            "Pantsättningsavgiften $avgift kr överstiger gränsen $grans kr enligt föreningens stadgar " +
                "(referensvärde, inte lagtak)."
        Avgiftstyp.ANDRAHAND ->
            "Andrahandsavgiften $avgift kr överstiger gränsen $grans kr."
    }
}
