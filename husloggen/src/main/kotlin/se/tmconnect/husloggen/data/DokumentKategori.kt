package se.tmconnect.husloggen.data

enum class DokumentKategori {
    MANUAL,
    RITNING,
    BESIKTNING,
    GARANTI,
    AVTAL,
    OVRIGT,
    ;

    fun sorteringsOrd(): Int = ordinal
}
