package se.tmconnect.styrelserummet.server

import se.tmconnect.styrelserummet.tolking.Tolkare

class FalskTolkare(
    private val jsonSvar: String = """
        {"komponenter":[{"namn":"Tak","grupp":"TAK","anskaffningsvarde":500000,"anskaffningsar":2010,"nyttjandeperiodAr":40,"kalla":"plan, sida 2","sakerhet":"HOG"}],"anmarkningar":[]}
    """.trimIndent(),
) : Tolkare {
    override fun foreslaKomponenter(
        underhallsplanUtdrag: String,
        sidor: List<Int>,
        filnamn: String,
    ): Result<String> = Result.success(jsonSvar)
}
