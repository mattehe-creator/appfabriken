package se.tmconnect.styrelserummet.modell

/**
 * @param raknearStartManad Räkenskapsårets startmånad (1–12). Påverkar framtida rapporter; avskrivning i reglerna räknas per kalenderår.
 */
data class Forening(
    val namn: String,
    val byggnadsar: Int?,
    val raknearStartManad: Int = 1,
)
