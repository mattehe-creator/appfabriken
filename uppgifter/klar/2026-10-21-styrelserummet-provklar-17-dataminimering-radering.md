# Uppgift: Dataminimering efter radering

uppdrag: uppdrag/2026-10-21-styrelserummet-provklar.md
försök: 1
beror-på: 2026-10-21-styrelserummet-provklar-16-dataminimering-efter-klart.md

## Ändra bara dessa filer
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/DataminimeringTest.kt
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/JobbLagring.kt

## Uppgift
Test `efterRaderaFinnsIngenKatalog`:

1. Skapa och slutför jobb (samma mönster som uppgift 16).
2. `DELETE /api/jobb/{id}` (eller `lagring.raderaJobb`).
3. Assert: sökvägen `jobb/{id}` **existerar inte**.

Ändra `JobbLagring.raderaJobb` **bara** om katalogen lämnas kvar med filer idag.

## Exempel
- Efter delete: `Files.notExists(jobbKatalog(id))`.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*DataminimeringTest.efterRadera*"

## Gör inte
- Ändra raderings-API utöver vad testet kräver.

utfall: underkänd 2026-10-21 av Cursor (PR #434). Ersatt av 2026-10-21-styrelserummet-provklar-17-dataminimering-radering-f2.md
