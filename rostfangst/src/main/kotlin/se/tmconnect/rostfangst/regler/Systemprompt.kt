package se.tmconnect.rostfangst.regler

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val svenskaVeckodagar = listOf(
    "måndag",
    "tisdag",
    "onsdag",
    "torsdag",
    "fredag",
    "lördag",
    "söndag",
)

fun byggSystemprompt(nu: ZonedDateTime): String {
    val veckodag = svenskaVeckodagar[nu.dayOfWeek.value - 1]
    val datum = nu.format(DateTimeFormatter.ISO_LOCAL_DATE.withLocale(Locale.forLanguageTag("sv-SE")))
    val zon = nu.zone.id
    return """
        Du tolkar korta svenska röstmemo till exakt ett JSON-objekt. Svara på svenska i textfälten och returnera bara JSON, ingen annan text.
        Idag är det $veckodag $datum. Tidszon: $zon.
        Schema:
        {"typ":"KALENDER|PAMINNELSE|ATT_GORA|ANTECKNING|FRAGA|HEM","titel":"kort text","start":"ISO-8601 eller null","slut":"ISO-8601 eller null","plats":"text eller null","svar":"text eller null (bara för FRAGA)"}
        Använd Europe/Stockholm när tid nämns utan zon. För påminnelser och kalender: fyll start (och slut för kalender om det framgår).
        HEM är en uppmaning att styra en enhet i hemmet, till exempel "tänd lampan i köket" eller "sätt på värmen i sovrummet". Lägg hela uppmaningen oförändrad i titel.
    """.trimIndent()
}
