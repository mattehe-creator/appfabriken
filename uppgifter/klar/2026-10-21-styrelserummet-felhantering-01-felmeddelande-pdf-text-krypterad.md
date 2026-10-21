# Uppgift: Felmeddelande för PDF utan text och krypterad PDF

uppdrag: uppdrag/2026-10-21-styrelserummet-felhantering.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/FelHantering.kt (ny)
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/FelHanteringTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/JobbKo.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/pdf/PdfLasare.kt

## Uppgift
Skapa en `sealed class ServerFel` med minst dessa varianter (fler kommer i senare uppgifter):
- `IngenLasbarText` — inskannad PDF eller bild utan markerbar text
- `KrypteradPdf` — lösenordsskyddad PDF

Implementera:

```kotlin
fun felTillMeddelande(fel: ServerFel): String
```

Meddelandena ska vara på svenska, begripliga för en styrelseledamot, utan filnamn, stacktrace, API-koder eller personuppgifter.

## Exempel
- `felTillMeddelande(ServerFel.IngenLasbarText)` → text som förklarar att PDF:en saknar läsbar text och att en textbaserad fil behövs (inte teknisk jargon).
- `felTillMeddelande(ServerFel.KrypteradPdf)` → text som förklarar att filen är lösenordsskyddad och ska laddas upp olåst.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*FelHanteringTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Implementera HTTP-status eller koppling till API ännu.

utfall: underkänd 2026-10-21
