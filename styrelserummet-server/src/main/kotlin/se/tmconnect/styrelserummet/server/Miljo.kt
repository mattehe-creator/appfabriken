package se.tmconnect.styrelserummet.server

import java.nio.file.Files
import java.nio.file.Path

val GILTIGA_MODELLER = setOf(
    "claude-sonnet-5-5",
    "claude-3-5-sonnet",
    "claude-3-opus",
    "claude-3-haiku"
)

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
    
    val port = env["PORT"]
    if (!port.isNullOrEmpty()) {
        try {
            val portNummer = port.toInt()
            if (portNummer < 1 || portNummer > 65535) {
                return listOf(
                    "Fel: Miljövariabeln PORT måste vara ett heltal mellan 1 och 65535.",
                )
            }
        } catch (e: NumberFormatException) {
            return listOf(
                "Fel: Miljövariabeln PORT måste vara ett heltal mellan 1 och 65535.",
            )
        }
    }
    
    val proxy = env["STYRELSERUMMET_PROXY"]
    if (!proxy.isNullOrEmpty() && proxy != "1") {
        return listOf(
            "Varning: Miljövariabeln STYRELSERUMMET_PROXY bör vara \"1\" bakom omvänd proxy enligt DRIFT.md.",
        )
    }
    
    val modell = env["STYRELSERUMMET_MODELL"]
    if (modell != null && modell.isEmpty()) {
        return listOf(
            "Fel: Miljövariabeln STYRELSERUMMET_MODELL måste anges.",
        )
    }

    if (!modell.isNullOrEmpty() && modell !in GILTIGA_MODELLER) {
        return listOf(
            "Varning: Miljövariabeln STYRELSERUMMET_MODELL har okänt värde \"$modell\", använd en av giltiga modeller: ${GILTIGA_MODELLER.joinToString(", ")}.",
        )
    }
    
    return emptyList()
}

fun skrivMiljoRapporter(rader: List<String>, skriv: (String) -> Unit = System.err::println) {
    rader.forEach { rad ->
        skriv(rad)
    }
}
