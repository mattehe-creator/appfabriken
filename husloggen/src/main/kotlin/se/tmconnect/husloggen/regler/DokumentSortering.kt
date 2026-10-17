package se.tmconnect.husloggen.regler

import se.tmconnect.husloggen.data.Dokument
import se.tmconnect.husloggen.data.DokumentKategori

data class DokumentGrupp(
    val kategori: DokumentKategori?,
    val dokument: List<Dokument>,
)

fun sorteraDokument(dokument: List<Dokument>): List<Dokument> =
    dokument.sortedWith(
        compareBy(
            { it.kategori?.sorteringsOrd() ?: Int.MAX_VALUE },
            { it.titel.lowercase() },
            { it.id },
        ),
    )

fun grupperaDokumentPerKategori(dokument: List<Dokument>): List<DokumentGrupp> {
    val sorterade = sorteraDokument(dokument)
    if (sorterade.isEmpty()) {
        return emptyList()
    }
    val grupper = mutableListOf<DokumentGrupp>()
    var aktuellKategori: DokumentKategori? = null
    var aktuellLista = mutableListOf<Dokument>()
    for (rad in sorterade) {
        if (rad.kategori != aktuellKategori && aktuellLista.isNotEmpty()) {
            grupper.add(DokumentGrupp(aktuellKategori, aktuellLista.toList()))
            aktuellLista = mutableListOf()
        }
        aktuellKategori = rad.kategori
        aktuellLista.add(rad)
    }
    if (aktuellLista.isNotEmpty()) {
        grupper.add(DokumentGrupp(aktuellKategori, aktuellLista))
    }
    return grupper
}
