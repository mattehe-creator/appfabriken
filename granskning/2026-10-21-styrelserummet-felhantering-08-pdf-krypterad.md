beslut: underkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- PR #399 innehåller enbart flytt av uppgiftsfilen från `uppgifter/ny/` till `uppgifter/pagar/`; **ingen diff** i `PdfLasare.kt` eller `PdfLasareTest.kt`.
- Uppgiften kräver `KrypteradPdfException`, `arKrypterad(bytes)` (eller motsvarande) och att krypterad PDF inte behandlas som «ingen läsbar text» — inget av detta finns i koden.
- Inga nya tester för krypterad PDF; befintliga `PdfLasareTest`-metoder täcker bara olåst PDF med/s utan text.
- Inga filer utanför uppgiftens lista ändrade; inga filer i repots rot.
- Inga nya beroenden, behörigheter eller nätverksanrop i diffen.
- Befintliga tester använder JUnit 4 (`org.junit`); `PdfLasare.kt` har ingen Compose-UI (konstanten `INGEN_LASBAR_TEXT` är befintlig, oförändrad).

## Rättat av Cursor
- inget (saknad implementation — inte småfel)

## Vid underkänd: försök 2

# Uppgift: Känna igen krypterad PDF

uppdrag: uppdrag/2026-10-21-styrelserummet-felhantering.md
försök: 2
beror-på: 2026-10-21-styrelserummet-felhantering-05-fel-http-status.md

## Vad som saknades
- PR #399 ändrade **ingen** Kotlin-kod. Körskriptet rapporterade att `PdfLasare.kt` och `PdfLasareTest.kt` «saknades» i diffen — filerna finns redan på main men uppgiften är **inte** implementerad där.
- Saknas: `KrypteradPdfException`, sätt att avgöra krypterad PDF (t.ex. `fun PdfLasare.arKrypterad(bytes: ByteArray): Boolean`), och att `harLasbarText` / `lasSomText` **inte** ska ge `IngenLasbarTextException` när PDF:en är lösenordsskyddad.
- Saknas: minst ett JUnit 4-test som jämför mot uppgiftens exempel (olåst → false, krypterad → true eller `KrypteradPdfException` vid läsning).

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/pdf/PdfLasare.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/pdf/PdfLasareTest.kt

## Läs som förebild (ändra inte)
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/FelHantering.kt (`ServerFel.KrypteradPdf` — samma domän, annan modul)
- Befintlig `PdfLasare.kt` och `pdfMedText`-hjälparen i `PdfLasareTest.kt`

## Uppgift
Utöka `PdfLasare` i paketet `se.tmconnect.styrelserummet.pdf`:

1. Lägg till `class KrypteradPdfException : Exception()` (ingen användartext behövs här — servern mappar senare till `ServerFel.KrypteradPdf`).

2. Lägg till t.ex.:

```kotlin
fun arKrypterad(bytes: ByteArray): Boolean
```

som returnerar `true` när PDFBox behandlar filen som krypterad/lösenordsskyddad (t.ex. `Loader.loadPDF(bytes).use { it.isEncrypted }` eller fånga PDFBox undantag för krypterad fil utan lösenord).

3. Se till att **lösenordsskyddad PDF inte räknas som «ingen läsbar text»**:
   - Antingen kontrollera kryptering **före** textutvinning i `lasPerSida` / `harLasbarText` / `lasSomText`, och returnera `Result.failure(KrypteradPdfException())` (eller kasta vid `getOrThrow`), **eller**
   - låt `lasSomText` misslyckas med `KrypteradPdfException` medan `IngenLasbarTextException` bara används när filen är olåst men saknar text.

4. Olåst PDF med text ska bete sig som idag (befintliga tester ska fortsätta gå).

Skapa i testet en minimal **krypterad** PDF med PDFBox (redan dependency i `:styrelserummet`) — t.ex. `StandardProtectionPolicy` med tomt ägarlösenord och ett användarlösenord på en minimal `PDDocument` — **eller** en fast byte-array som PDFBox markerar som krypterad. Inga nya Gradle-beroenden.

## Exempel
- Bytes från olåst test-PDF (befintlig `pdfMedText`) → `arKrypterad(bytes)` är **false**; `harLasbarText` / `lasSomText` som idag.
- Bytes från krypterad test-PDF → `arKrypterad(bytes)` är **true**; och antingen misslyckas `lasPerSida`/`lasSomText` med `KrypteradPdfException`, eller så är `harLasbarText` **false** utan att `lasSomText` ger `IngenLasbarTextException` (asserta rätt undantagstyp i testet).

## Test som ska gå igenom
Lägg till minst en `@Test` i `PdfLasareTest` med JUnit 4 (`org.junit.Test`, `org.junit.Assert.*`) som assertar enligt exemplen ovan — inte bara att funktionen anropas utan att resultat/undantag stämmer.

```
gradle :styrelserummet:test --tests "*PdfLasareTest"
```

Befintliga tester `lasPerSidaReturnerarTextPerSida` och `skannadPdfUtanTextGerIngenLasbarText` ska fortsätta gå.

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Flytta eller duplicera filer till repots rot.

## Frågor till Claude
- inga
