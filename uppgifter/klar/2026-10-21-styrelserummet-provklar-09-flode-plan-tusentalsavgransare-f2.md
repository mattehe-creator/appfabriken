# Uppgift: Syntetisk plan med hårda mellanslag och tusentalsavgränsare

uppdrag: uppdrag/2026-10-21-styrelserummet-provklar.md
försök: 2
beror-på: ingen

## Vad som saknades
- En komplett fil lades i repots rot (`SyntetiskPlanTusentalsTest.kt`); endast sökvägen under `styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/` gäller.
- Testet i modulen körde `assertTrue(true)` i stället för att anropa `byggUtkast` och asserta belopp, avstämning, säkerhet och anmärkningar.
- `byggUtkast` anropades (i kommenterad kod) med fel parametrar. Rätt mönster finns i `SyntetiskPlanTabellTest.kt` och `SyntetiskPlanTkrTextTest.kt`: skapa PDF med PDFBox, `PdfLasare().lasSomText(pdfBytes)`, stub-`Tolkare` som returnerar JSON-strängen, årsredovisningstext, `Forening(...)`, `nuAr = 2025`.

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/SyntetiskPlanTusentalsTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/SyntetiskPlanTabellTest.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/SyntetiskPlanTkrTextTest.kt

## Uppgift
Test `planHardMellanslagOchPunkt`:

1. Bygg en minimal PDF (PDFBox, samma hjälpmönster som i förebilderna) med text som innehåller belopp **1.250.000** (punkt som tusentalsavgränsare), t.ex. raden `Fönsterbyte 2025 1.250.000 kr`.
2. `val planText = PdfLasare().lasSomText(pdfBytes).getOrThrow()`. Om extraherad text saknar hårda mellanslag (`\u00A0`) men uppgiften kräver dem i flödet: lägg in sekvensen i `planText` efter `lasSomText` (t.ex. ersätt mellanslag i beloppet) och dokumentera kort i kommentar varför (PDFBox bevarar inte alltid NBSP).
3. Stub-JSON via `object : Tolkare { ... }` med **en** komponent: namn t.ex. Fönster, `grupp` `FONSTER_DORRAR`, `anskaffningsvarde` **1250000** (eller `"1 250 000"`), övriga fält som i övriga syntetiska tester.
4. Årsredovisningstext med byggnadsvärde **1 250 000** (samma summa som komponenten), t.ex. `Byggnader och mark 1 250 000`.
5. `byggUtkast(planText, arText, Forening("Testföreningen", 1975), tolkare, nuAr = 2025)`.
6. Assert: `utkast.komponenter.single().anskaffningsvarde == 1_250_000L`, `utkast.avstamning!!.stammer == true`, `utkast.avstamning!!.summa == 1_250_000L`, säkerhet enligt stub (t.ex. `MEDEL`), och anmärkningar enligt samma mönster som tabell-/tkr-testerna (inga falska differensvarningar när allt stämmer).

Ta bort all utkommenterad kod, `assertTrue(true)` och oanvända variabler.

## Exempel
- En komponent 1_250_000, byggnad 1_250_000 → `stammer == true`, summa 1_250_000.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*SyntetiskPlanTusentalsTest"

## Gör inte
- Skapa filer i repots rot.
- Ändra `nyttjandeperioder.csv`.
- Lägga till beroenden.
- Ändra produktionskod.

utfall: godkänd 2026-10-21 (tog bort duplicerad testfil i repots rot; stavfel anskaffningsvarde)
