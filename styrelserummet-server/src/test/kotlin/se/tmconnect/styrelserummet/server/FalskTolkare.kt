package se.tmconnect.styrelserummet.server

import se.tmconnect.styrelserummet.underlag.RadForslag
import se.tmconnect.styrelserummet.underlag.Tolkare
import se.tmconnect.styrelserummet.underlag.TolkningsForslag

class FalskTolkare(
    private val jsonSvar: String = """
        {"komponenter":[{"namn":"Tak","grupp":"TAK","anskaffningsvarde":500000,"anskaffningsar":2010,"nyttjandeperiodAr":40,"kalla":"plan, sida 2","sakerhet":"HOG"}],"anmarkningar":[]}
    """.trimIndent(),
) : Tolkare {
    override fun foreslaKomponenter(underhallsplanText: String, filnamn: String): Result<TolkningsForslag> {
        val komponenter = listOf(
            RadForslag("Tak", "TAK", 500_000, 2010, 40, "plan, sida 2", "HOG"),
        )
        return Result.success(TolkningsForslag(komponenter, emptyList()))
    }
}
