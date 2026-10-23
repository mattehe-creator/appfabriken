# Uppgift: Skriv exempel-PDF till katalog (Gradle anropar)

uppdrag: uppdrag/2026-10-23-styrelserummet-exempelhandlingar.md
försök: 2
beror-på: 2026-10-23-styrelserummet-exempel-10-pdf-export-funktioner-f2.md

## Vad som saknades
- Ingen Kotlin-kod levererades — bara uppgiftsflytt till `pagar/`.
- Skapa båda filerna enligt nedan. Exportfunktionerna `exempelUnderhallsplanPdf()` och `exempelArsredovisningPdf()` finns redan i `ExempelPdf.kt` — anropa dem, skriv inte om PDF-innehållet.

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/exempel/SkapaExempel.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/exempel/SkapaExempelTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelPdf.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelPdfExportTest.kt

## Uppgift
Ny fil `SkapaExempel.kt` i paketet `se.tmconnect.styrelserummet.underlag.exempel`.

`fun skrivExempelHandingar(malKatalog: java.nio.file.Path)`:
1. Skapa katalogen om den saknas (`Files.createDirectories(malKatalog)`).
2. Skriv `malKatalog.resolve("underhallsplan-exempel.pdf")` med bytes från `exempelUnderhallsplanPdf()`.
3. Skriv `malKatalog.resolve("arsredovisning-exempel.pdf")` med bytes från `exempelArsredovisningPdf()`.
   Använd `Files.write(path, bytes)` (eller motsvarande).

`fun main(args: Array<String>)`:
- Om `args.isEmpty()` eller `args[0]` är tom: skriv ett kort felmeddelande på **svenska** till **stderr** (t.ex. `"Ange målkatalog som första argument."`) och avsluta med `kotlin.system.exitProcess(1)`.
- Annars: `skrivExempelHandingar(java.nio.file.Path.of(args[0]))`.

Testklass `SkapaExempelTest` med **JUnit 4** (`org.junit.Test`, `org.junit.Assert` — inte `kotlin.test`).

Test `skrivExempelHandingarSkaparTvaPdf`:
1. Tempkatalog, t.ex. `kotlin.io.path.createTempDirectory("skapa_exempel")`.
2. Anropa `skrivExempelHandingar(tmpDir)`.
3. Assert att `tmpDir/underhallsplan-exempel.pdf` och `tmpDir/arsredovisning-exempel.pdf` finns och att varje fil är **större än 1000 byte**.
4. `PdfLasare().lasSomText(Files.readAllBytes(planPath)).getOrThrow()` ska innehålla **`Brf Exempelgården`**.
5. `PdfLasare().lasSomText(Files.readAllBytes(arPath)).getOrThrow()` ska innehålla **`Byggnader och mark`**.

## Exempel
- `skrivExempelHandingar(tmpDir)` → två filer med namnen `underhallsplan-exempel.pdf` och `arsredovisning-exempel.pdf`.

## Test som ska gå igenom
gradle :styrelserummet:testDebugUnitTest --tests "*SkapaExempelTest"

## Gör inte
- Ändra `styrelserummet-server/build.gradle.kts`.
- Checka in genererade PDF:er i repot.
- Lägga till beroenden.
- Använda `kotlin.test`.

utfall: godkänd 2026-10-23 (hela implementationen; lokal modell levererade ingen kod; en rad i ExempelTexter för PDF-storlek >1000)
