# Uppgift: Miljökontroll datakatalog och skrivbarhet

uppdrag: uppdrag/2026-10-21-styrelserummet-provklar.md
försök: 2
beror-på: ingen

## Vad som saknades
- `Miljo.kt` och `MiljoTest.kt` lämnades tomma. Det finns ingen `kontrolleraMiljo`, inga felmeddelanden och inga enhetstester.
- Kör `gradle :styrelserummet-server:test --tests "*MiljoTest"` — den ska hitta minst tre tester och bli grön.

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Miljo.kt (ny)
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/MiljoTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Application.kt
- styrelserummet/DRIFT.md

## Uppgift
Inför `fun kontrolleraMiljo(env: Map<String, String?>, kanSkriva: (String) -> Boolean = { true }): List<String>` i `Miljo.kt` (toppnivå i paketet `se.tmconnect.styrelserummet.server`).

Varje sträng i listan är ett svenskt **fel** eller en **varning** (prefix `"Fel:"` respektive `"Varning:"`). Tom lista när inget att rapportera för de kontroller som ingår här.

Implementera i detta steg:
1. Saknad eller tom `STYRELSERUMMET_DATA` → fel som säger att datakatalog måste anges (nämn variabelnamnet `STYRELSERUMMET_DATA`, inget hemligt).
2. När `STYRELSERUMMET_DATA` är satt till en icke-tom sträng men `kanSkriva(sökväg)` är `false` för den sökvägen → fel om att katalogen inte går att skriva till (nämn sökvägen, inte nycklar).

Kontrollera `STYRELSERUMMET_DATA` så här: nyckeln saknas i `env`, värdet är `null`, eller värdet är en tom sträng — alla tre räknas som saknad/tom.

Övriga miljökontroller kommer i senare uppgifter; returnera bara det som hör hit.

## Exempel
- Indata: `env = mapOf("STYRELSERUMMET_DATA" to null)`, `kanSkriva = { true }` → listan innehåller exakt ett fel om saknad datakatalog (prefix `Fel:`, text nämner `STYRELSERUMMET_DATA`).
- Indata: `env = mapOf("STYRELSERUMMET_DATA" to "/data")`, `kanSkriva = { false }` → listan innehåller exakt ett fel om skrivbarhet; texten nämner `/data` och prefix `Fel:`.
- Indata: `env = mapOf("STYRELSERUMMET_DATA" to "/data")`, `kanSkriva = { true }` → tom lista (för dessa kontroller).

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*MiljoTest"

Skriv `MiljoTest` med **JUnit 4** (`org.junit.Test`, `org.junit.Assert`), som i `FelmeddelandenTest.kt`. Minst tre tester som motsvarar exemplen ovan (assert antal fel, prefix, att rätt namn/sökväg finns i meddelandet, tom lista när allt ok).

## Gör inte
- Ändra andra filer.
- Skriva ut eller inkludera `ANTHROPIC_API_KEY` eller andra hemligheter i felmeddelanden.
- Lägga till beroenden.

utfall: godkänd 2026-10-21 (Cursor implementerade Miljo.kt och MiljoTest.kt — tom försök 2)
