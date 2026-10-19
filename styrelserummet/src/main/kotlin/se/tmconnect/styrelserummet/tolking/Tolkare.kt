package se.tmconnect.styrelserummet.tolking

interface Tolkare {
    fun foreslaKomponenter(
        underhallsplanUtdrag: String,
        sidor: List<Int>,
        filnamn: String,
    ): Result<String>
}

class TolkningMisslyckades(orsak: String) : Exception(orsak)
