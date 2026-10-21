# Uppgift: Säker loggrad för jobbfel

uppdrag: uppdrag/2026-10-21-styrelserummet-felhantering.md
försök: 1
beror-på: 2026-10-21-styrelserummet-felhantering-01-felmeddelande-pdf-text-krypterad-f2.md

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/FelHantering.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/FelHanteringTest.kt

## Uppgift
Lägg till:

```kotlin
fun loggJobbFel(jobbId: String, fel: ServerFel, tidMs: Long): String
```

Loggraden ska innehålla jobb-id, feltyp (t.ex. sealed-klassens namn eller en kort kod), och tid som ISO-8601 UTC eller epoch ms. Den får **inte** innehålla filnamn, PDF-text, belopp, personnummer, stacktrace eller användarmeddelandet rakt av.

## Exempel
- `loggJobbFel("abc123...", ServerFel.AiTimeout, 1_700_000_000_000L)` → en rad med id, `AiTimeout` (eller liknande) och tid — ingen PDF-text.
- Samma funktion med `ServerFel.IngenLasbarText` → ingen filnamnssträng i raden.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*FelHanteringTest"

## Gör inte
- Ändra andra filer.
- Koppla in faktisk loggning i `JobbKo` (kommer i senare uppgift).
