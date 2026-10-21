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
    
    val apiNyckel = env["ANTHROPIC_API_KEY"]
    if (apiNyckel.isNullOrEmpty()) {
        return listOf(
            "Fel: Miljövariabeln ANTHROPIC_API_KEY måste anges.",
        )
    }
    if (!apiNyckel.startsWith("sk-ant-")) {
        return listOf(
            "Fel: ANTHROPIC_API_KEY har fel format, börja med \"sk-ant-\".",
        )
    }
    
    return emptyList()
}
