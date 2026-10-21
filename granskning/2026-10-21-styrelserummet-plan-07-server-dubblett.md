beslut: underkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Grenen ändrar bara uppgiftsflytt `ny/` → `pagar/` och en ny testfil; `CsvExport.kt` är oförändrad trots att den ska vara huvudändringen.
- `CsvExport.kt` har kvar den privata `bokfortVarde` (rad 40–49); ingen import av `se.tmconnect.styrelserummet.regler.bokfortVarde`.
- `CsvExportServerTest.kt` är tom — inga tester som anropar serverns `tillCsv` eller jämför mot exemplen.
- Ingen `FRAGA.md` på grenen.
- Inga nya beroenden, behörigheter eller nätverksanrop i diffen.
- `gradle :styrelserummet-server:test` grönt lokalt (tom testfil kompilerar).

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

# Uppgift: Servern använder kärnans bokfortVarde

uppdrag: uppdrag/claude/2026-10-21-styrelserummet-avskrivningsplan.md
försök: 2
beror-på: 2026-10-21-styrelserummet-plan-01-heltal.md

## Vad som saknades
- **`CsvExport.kt` ändrades inte.** Den privata funktionen `bokfortVarde` (nederst i filen) ska tas bort. Lägg till `import se.tmconnect.styrelserummet.regler.bokfortVarde` och anropa den befintliga top-level-funktionen på samma ställe som idag (rad med `bokfortVarde(modell, arsslut)`). Ta bort import av `avskrivningFor` om den bara användes i den privata funktionen. Inget annat i filen (rubriker, kolumner, `csvFalt`, `UTKAST_RAD`).
- **`CsvExportServerTest.kt` är tom.** Implementera testklassen enligt nedan. Använd JUnit 4 (`org.junit.Test`, `org.junit.Assert`), inte `kotlin.test`.

Tips för testet:
- Importera serverns `tillCsv` (samma paket `se.tmconnect.styrelserummet.server`).
- Skapa `KomponentDto` med `grupp` och `sakerhet` som **text** — enum-namn, t.ex. `"STOMME"` och `"MEDEL"` (se `ApiModell.kt` / `franDto`).
- Dela CSV på `\n`, ta dataraden (index 1 när listan har en komponent), splitta på `;` och kontrollera fältet **Bokfört värde** (index 5), eller använd `assertTrue(csv.contains(";120000;"))` som i exemplen.

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/CsvExport.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/CsvExportServerTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/BokfortVarde.kt
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/ApiModell.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/MiljoTest.kt (JUnit 4-stil)

## Uppgift
`CsvExport.kt` i servern har en egen privat kopia av `bokfortVarde`. Två kopior av en räkneregel kan glida isär.

1. Ta bort den privata funktionen `bokfortVarde` i `CsvExport.kt`.
2. Importera `se.tmconnect.styrelserummet.regler.bokfortVarde` och använd den på samma ställe.
3. Inget annat i filen ändras.

Fyll i `CsvExportServerTest` så att den anropar `tillCsv` med en `KomponentDto` och kontrollerar kolumnen Bokfört värde.

## Exempel
Komponent: anskaffningsvärde 300_000, anskaffningsår 2020, nyttjandeperiod 10 år (övriga fält valfria men giltiga enligt `KomponentDto`).

- Årsslut 2025 → andra raden innehåller `;120000;`
- Samma, årsslut 2019 → `;300000;` som bokfört värde
- Samma, årsslut 2035 → `;0;`
- Sista raden är fortfarande `UTKAST_RAD` (eventuellt inom citattecken om den innehåller semikolon; den gör det inte).

## Test som ska gå igenom
gradle :styrelserummet-server:test

## Gör inte
- Ändra kolumner eller rubriker.
- Ändra andra filer.
- Lägga till beroenden.

## Frågor till Claude
- inga
