# Uppgift: Dataminimering efter radering

uppdrag: uppdrag/2026-10-21-styrelserummet-provklar.md
försök: 2
beror-på: 2026-10-21-styrelserummet-provklar-16-dataminimering-efter-klart.md

## Vad som saknades
- Ingen implementation: PR:en ändrade bara uppgiftsfilens plats (`ny` → `pagar`), inga rader i `DataminimeringTest.kt` eller `JobbLagring.kt`.
- Obligatoriska testmetoden `efterRaderaFinnsIngenKatalog` finns inte.

## Ändra bara dessa filer
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/DataminimeringTest.kt
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/JobbLagring.kt

## Läs som förebild (ändra inte)
- Samma fil: metoden `efterKlartJobbBaraTillatnaFiler` (uppgift 16) — samma `testApplication`, `ServerMiljo` med `synkronJobb = true`, `FalskTolkare`, två PDF:er, vänta tills `meta.status == "KLAR"`.
- `styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/ApiTest.kt` — `client.delete("/api/jobb/$id")` förväntar `204 No Content`.

## Uppgift
Lägg till test `efterRaderaFinnsIngenKatalog`:

1. Skapa och slutför jobb (samma steg som i `efterKlartJobbBaraTillatnaFiler`: POST `/api/jobb`, poll GET tills status `KLAR`).
2. Anropa `DELETE /api/jobb/{id}` (importera `io.ktor.client.request.delete`).
3. Assert: jobbkatalogen `tmp / "jobb" / id` **finns inte** — t.ex. `assertTrue(Files.notExists(jobbKatalog))` eller `kotlin.io.path`-ekvivalent (`!jobbKatalog.exists()`).

Ändra `JobbLagring.raderaJobb` **endast** om testet visar att katalogen `jobb/{id}` lämnas kvar efter radering. Idag rensas innehåll och `dir.deleteIfExists()` anropas; om det redan raderar hela katalogen ska produktionskoden vara oförändrad.

## Exempel
- Efter delete: `Files.notExists(jobbKatalog)` (eller motsvarande) är sant.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*DataminimeringTest.efterRadera*"

## Gör inte
- Ändra raderings-API utöver vad testet kräver.
- Skapa filer i repots rot.
- Lägga till beroenden.
- Använda `kotlin.test` — använd `org.junit`.

utfall: godkänd 2026-10-21 (inget)
