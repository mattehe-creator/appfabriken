# Uppgift: Skriv exempel-PDF till katalog (Gradle anropar)

uppdrag: uppdrag/2026-10-23-styrelserummet-exempelhandlingar.md
försök: 1
beror-på: 2026-10-23-styrelserummet-exempel-10-pdf-export-funktioner.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/exempel/SkapaExempel.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/exempel/SkapaExempelTest.kt (ny)

## Uppgift
`fun skrivExempelHandingar(malKatalog: java.nio.file.Path)` skapar katalogen om den saknas och skriver:

- `underhallsplan-exempel.pdf` — `exempelUnderhallsplanPdf()`
- `arsredovisning-exempel.pdf` — `exempelArsredovisningPdf()`

`fun main(args: Array<String>)` — om `args[0]` saknas: skriv felmeddelande på svenska till stderr och avsluta med kod 1. Annars anropa `skrivExempelHandingar(Path.of(args[0]))`.

Test `skrivExempelHandingarSkaparTvaPdf` (tempkatalog):

- efter anrop finns båda filerna, varje fil > 1000 byte
- `PdfLasare` hittar `Brf Exempelgården` i planfilen och `Byggnader och mark` i årsredovisningen

## Exempel
- `skrivExempelHandingar(tmpDir)` → två filer med förväntade namn.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*SkapaExempelTest"

## Gör inte
- Ändra `styrelserummet-server/build.gradle.kts` (Cursor har Gradle-uppgiften).
- Checka in genererade PDF:er i repot.
