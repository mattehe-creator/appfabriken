# Uppgift: HTTP-status för serverfel

uppdrag: uppdrag/2026-10-21-styrelserummet-felhantering.md
försök: 1
beror-på: 2026-10-21-styrelserummet-felhantering-04-felmeddelande-grans-okant.md

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/FelHantering.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/FelHanteringTest.kt

## Uppgift
Lägg till:

```kotlin
fun felTillHttpStatus(fel: ServerFel): Int
```

Mappa varje `ServerFel`-variant till rätt HTTP-status enligt tabellen:

| Fel | Status |
|-----|--------|
| IngenLasbarText, KrypteradPdf | 400 |
| ForStorFil | 413 |
| IntePdf | 415 |
| ForMangaJobb | 429 |
| AiOgiltigtSvar | 502 |
| AiTimeout | 504 |
| Okant | 500 |

## Exempel
- `felTillHttpStatus(ServerFel.ForStorFil)` → `413`
- `felTillHttpStatus(ServerFel.AiTimeout)` → `504`
- `felTillHttpStatus(ServerFel.IntePdf)` → `415`

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*FelHanteringTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.

utfall: godkänd 2026-10-21 (is ServerFel.ForMangaJobb i felTillHttpStatus)
