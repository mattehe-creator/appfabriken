# Uppgift: Känna igen krypterad PDF

uppdrag: uppdrag/2026-10-21-styrelserummet-felhantering.md
försök: 1
beror-på: 2026-10-21-styrelserummet-felhantering-05-fel-http-status.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/pdf/PdfLasare.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/pdf/PdfLasareTest.kt

## Läs som förebild (ändra inte)
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/FelHantering.kt (när den finns)

## Uppgift
Lägg till ett tydligt sätt att skilja krypterad PDF från övriga läsfel, t.ex.:

```kotlin
class KrypteradPdfException : Exception()

fun PdfLasare.arKrypterad(bytes: ByteArray): Boolean
```

eller motsvarande som `JobbKo` senare kan använda. `harLasbarText` / `lasSomText` ska inte ge samma fel som tom text när filen är lösenordsskyddad.

Skapa i testet en minimal krypterad PDF med PDFBox (samma bibliotek som redan används) **eller** använd en fast minimal byte-array som PDFBox behandlar som krypterad — utan nya beroenden.

## Exempel
- Bytes från en olåst test-PDF → `arKrypterad` är false.
- Bytes från krypterad test-PDF → `arKrypterad` är true (eller `lasPerSida` misslyckas med `KrypteradPdfException`).

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*PdfLasareTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.

utfall: underkänd 2026-10-21 av Cursor (PR #399). Ersatt av 2026-10-21-styrelserummet-felhantering-08-pdf-krypterad-f2.md
