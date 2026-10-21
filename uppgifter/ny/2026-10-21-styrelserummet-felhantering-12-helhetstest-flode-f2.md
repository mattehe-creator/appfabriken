# Uppgift: Helhetstest för lyckat flöde

uppdrag: uppdrag/2026-10-21-styrelserummet-felhantering.md
försök: 2
beror-på: 2026-10-21-styrelserummet-felhantering-07-falsk-tolkare-spion-f2.md

## Vad som saknades
- Grenen innehöll bara commit «Uppgift startad» (flytt till `pagar/`). `ApiTest.kt` ändrades inte.
- Testmetoden `helhetFlodeMedTreKomponenterOchMaskning` finns inte; inget helhetsflöde, ingen maskningsassert, ingen CSV-summa enligt exemplet.

## Ändra bara dessa filer
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/ApiTest.kt

## Läs som förebild (ändra inte)
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/CsvExport.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/MaskaPersonuppgifter.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/ApiTest.kt (befintliga tester t.ex. `skapaHamtaCsvRadera`, hjälparen `pdfMedText`)

## Uppgift
Lägg till **ett** nytt test i klassen `ApiTest`:

`fun helhetFlodeMedTreKomponenterOchMaskning() = runBlocking { ... }`

Följ samma mönster som `skapaHamtaCsvRadera`: `createTempDirectory`, `testApplication`, `module(ServerMiljo(..., synkronJobb = true))`, `createClient`.

1. **Tre komponenter i falsk tolkare:** Skapa `val tolkare = FalskTolkare(jsonSvar = """...""")` där JSON innehåller **tre** objekt i `"komponenter"` med grupper `TAK`, `FASAD`, `FONSTER` (eller liknande giltiga grupper) och anskaffningsvärden **500000**, **300000**, **200000** (summa **1_000_000**). Inkludera fält som befintlig default-JSON (`anskaffningsar`, `nyttjandeperiodAr`, `kalla`, `sakerhet`).

2. **PDF:er:** Använd `pdfMedText(...)`:
   - Plan: text som innehåller personnumret `19800101-1234` (t.ex. `"Styrelseledamot 19800101-1234 Underhall tak"`).
   - Årsredovisning: text som ger byggnadens värde **1 000 000**, t.ex. `"Byggnader och mark 1 000 000"` (så avstämning kan slå).

3. **POST /api/jobb** med båda PDF:erna (`filename=plan.pdf` och `filename=arsredovisning.pdf`), `forening`, `arsslut` — som i befintliga tester.

4. **GET /api/jobb/{id}:** Parse JSON; assert `meta.status == "KLAR"`. Assert `utkast.komponenter` har **exakt tre** element. Assert att CSV-markering finns i flödet (t.ex. att senare CSV-anrop innehåller `UTKAST` — se steg 6).

5. **Maskning:** Assert `assertFalse(tolkare.senasteUtdrag!!.contains("19800101-1234"))` (personnumret ska maskas **före** AI-anrop). Assert gärna att utdraget innehåller `[personnummer]` om plan-PDF:en hade personnummer.

6. **GET /api/jobb/{id}.csv`:** Assert status OK. Första raden ska matcha rubriker från `tillCsv` i `CsvExport.kt`: `Komponent;Grupp;Anskaffningsvärde;Anskaffningsår;Nyttjandeperiod;Bokfört värde;Källa;Säkerhet` (semikolonseparerade). Assert att hela CSV-texten innehåller `UTKAST`. Parsa datarader (exkl. rubrik och sista utkastrad): **tre** rader med komponenter; summera kolumnen Anskaffningsvärde (tredje fält) och assert `assertEquals(1_000_000L, summa)`.

Använd JUnit 4 (`org.junit.Test`, `org.junit.Assert`) — **inte** kotlin.test.

## Exempel
- Tre komponenter à 500000, 300000, 200000 → JSON-utkast med tre rader; CSV med rubrik + tre datarader + utkastrad; summa anskaffningsvärden **1 000 000**; `senasteUtdrag` utan `19800101-1234`.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*ApiTest.helhetFlodeMedTreKomponenterOchMaskning"

## Gör inte
- Ändra produktionskod.
- Ändra andra filer än `ApiTest.kt`.
- Lägga till beroenden.
