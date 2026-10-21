package se.tmconnect.styrelserummet.server

import se.tmconnect.styrelserummet.tolking.Tolkare
import se.tmconnect.styrelserummet.tolking.TolkningMisslyckades

enum class SvarLage {
    NORMAL,
    TIMEOUT,
    OGILTIGT_JSON,
    TOMT,
}

class FalskTolkare(
    private val jsonSvar: String = """
        {"komponenter":[{"namn":"Tak","grupp":"TAK","anskaffningsvarde":500000,"anskaffningsar":2010,"nyttjandeperiodAr":40,"kalla":"plan, sida 2","sakerhet":"HOG"}],"anmarkningar":[]}
    """.trimIndent(),
    private val lage: SvarLage = SvarLage.NORMAL,
) : Tolkare {
    var senasteUtdrag: String? = null
        private set

    override fun foreslaKomponenter(
        underhallsplanUtdrag: String,
        sidor: List<Int>,
        filnamn: String,
    ): Result<String> {
        senasteUtdrag = underhallsplanUtdrag
        return when (lage) {
            SvarLage.NORMAL -> Result.success(jsonSvar)
            SvarLage.TIMEOUT -> Result.failure(TolkningMisslyckades("timeout"))
            SvarLage.OGILTIGT_JSON -> Result.success("inte json")
            SvarLage.TOMT -> Result.success("")
        }
    }
}
