# Uppgift: Miljökontroll datakatalog och skrivbarhet

uppdrag: uppdrag/2026-10-21-styrelserummet-provklar-forsta-korning.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Miljo.kt (ny)
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/MiljoTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Application.kt
- styrelserummet/DRIFT.md

## Uppgift
Inför `fun kontrolleraMiljo(env: Map<String, String?>, kanSkriva: (String) -> Boolean = { true }): List<String>` i `Miljo.kt`.

Varje sträng i listan är ett svenskt **fel** eller en **varning** (prefix `"Fel:"` respektive `"Varning:"`). Tom lista när inget att rapportera för de kontroller som ingår här.

Implementera i detta steg:
1. Saknad eller tom `STYRELSERUMMET_DATA` → fel som säger att datakatalog måste anges (nämn variabelnamnet, inget hemligt).
2. När `STYRELSERUMMET_DATA` är satt men `kanSkriva(sökväg)` är `false` → fel om att katalogen inte går att skriva till (nämn sökvägen, inte nycklar).

Övriga miljökontroller kommer i senare uppgifter; returnera bara det som hör hit.

## Exempel
- Indata: `env = mapOf("STYRELSERUMMET_DATA" to null)`, `kanSkriva = { true }` → listan innehåller exakt ett fel om saknad datakatalog.
- Indata: `env = mapOf("STYRELSERUMMET_DATA" to "/data")`, `kanSkriva = { false }` → listan innehåller fel om skrivbarhet för `/data`.
- Indata: `env = mapOf("STYRELSERUMMET_DATA" to "/data")`, `kanSkriva = { true }` → tom lista (för dessa kontroller).

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*MiljoTest"

## Gör inte
- Ändra andra filer.
- Skriva ut eller inkludera `ANTHROPIC_API_KEY` eller andra hemligheter i felmeddelanden.
- Lägga till beroenden.
