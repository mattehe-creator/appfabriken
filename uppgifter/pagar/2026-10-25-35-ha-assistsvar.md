# Uppgift: Läs Assist-svar från Home Assistant

uppdrag: uppdrag/2026-10-25-rostfangst-home-assistant.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/ha/HaAssistSvar.kt (ny)
- rostfangst/src/test/kotlin/se/tmconnect/rostfangst/ha/HaAssistSvarTest.kt (ny)

## Läs som förebild (ändra inte)
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/regler/TolkaSvar.kt
- rostfangst/src/test/kotlin/se/tmconnect/rostfangst/regler/TolkaSvarTest.kt

## Uppgift
Paket `se.tmconnect.rostfangst.ha`. Ren Kotlin-funktion utan Android och utan nätverk.
- `fun lasAssistSvar(json: String): String?` — plockar `response.speech.plain.speech` ur svaret från `POST /api/conversation/process` med `org.json`. Trimmar. Ger `null` om något steg saknas, om texten är tom, eller om JSON är ogiltig (fånga undantag).

## Exempel
- `{"response":{"speech":{"plain":{"speech":"Lampan i vardagsrummet är tänd."}}}}` → `"Lampan i vardagsrummet är tänd."`
- `{"response":{"speech":{}}}` → `null`
- `{"response":{"speech":{"plain":{"speech":"  "}}}}` → `null`
- `inte json` → `null`

## Test som ska gå igenom
gradle :rostfangst:testDebugUnitTest --tests "*HaAssistSvarTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Göra nätverksanrop eller logga adress eller token.
