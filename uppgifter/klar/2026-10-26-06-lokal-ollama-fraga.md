# Uppgift: Ollama-förfrågan

uppdrag: uppdrag/vantar/2026-10-25-rostfangst-lokal-ai.md
försök: 1
beror-på: 2026-10-26-04-lokal-json-hjalp-f2.md

## Ändra bara dessa filer
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/lokal/OllamaFraga.kt (ny)
- rostfangst/src/test/kotlin/se/tmconnect/rostfangst/lokal/OllamaFragaTest.kt (ny)

## Läs som förebild (ändra inte)
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/lokal/JsonHjalp.kt
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/tolking/AnthropicTolkare.kt (bara för att se fälten; den använder `org.json`, det ska den här filen inte)

## Uppgift
Paket `se.tmconnect.rostfangst.lokal`. Använd `jsonCitera` från `JsonHjalp.kt`. Inget `org.json`. JUnit 4, utan Robolectric.

`fun byggOllamaChat(modell: String, systemprompt: String, text: String, typer: List<String>): String`

Ger en JSON-sträng på exakt den här formen (inga blanksteg utanför strängvärdena, fälten i den här ordningen):

`{"model":M,"stream":false,"messages":[{"role":"system","content":S},{"role":"user","content":T}],"format":{"type":"object","properties":{"typ":{"type":"string","enum":[E]},"rubrik":{"type":"string"},"start":{"type":"string"},"text":{"type":"string"}},"required":["typ","rubrik"]}}`

der M, S och T är `jsonCitera` av respektive argument, och E är `jsonCitera` av varje element i `typer` (i ordning) åtskilda av komma.

## Exempel
`byggOllamaChat("qwen3:8b", "Du tolkar.", "ring tandläkaren", listOf("PAMINNELSE", "ANTECKNING"))` ger exakt
`{"model":"qwen3:8b","stream":false,"messages":[{"role":"system","content":"Du tolkar."},{"role":"user","content":"ring tandläkaren"}],"format":{"type":"object","properties":{"typ":{"type":"string","enum":["PAMINNELSE","ANTECKNING"]},"rubrik":{"type":"string"},"start":{"type":"string"},"text":{"type":"string"}},"required":["typ","rubrik"]}}`
- Text med citattecken och radbrytning: `byggOllamaChat("m", "s", "a\"b\nc", listOf("X"))` innehåller delsträngen `"content":"a\"b\nc"` (med skrivna backslash-tecken).
- Tom `typer`: `"enum":[]`.

## Test som ska gå igenom
gradle :rostfangst:testDebugUnitTest --tests "*OllamaFragaTest"

## Gör inte
- Använda `org.json`.
- Ändra andra filer (inte heller `JsonHjalp.kt`).
- Lägga till beroenden.

utfall: underkänd 2026-10-26 av Cursor (PR #760). Ersatt av 2026-10-26-06-lokal-ollama-fraga-f2.md
