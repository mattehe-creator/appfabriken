package se.tmconnect.styrelserummet.server

fun kontrolleraMiljo(
    env: Map<String, String?>,
    kanSkriva: (String) -> Boolean = { true },
): List<String> {
    val dataSokvag = env["STYRELSERUMMET_DATA"]
    if (dataSokvag.isNullOrEmpty()) {
        return listOf(
            "Fel: Miljövariabeln STYRELSERUMMET_DATA måste anges (datakatalog).",
        )
    }
    if (!kanSkriva(dataSokvag)) {
        return listOf(
            "Fel: Datakatalogen $dataSokvag går inte att skriva till.",
        )
    }
    return emptyList()
}
