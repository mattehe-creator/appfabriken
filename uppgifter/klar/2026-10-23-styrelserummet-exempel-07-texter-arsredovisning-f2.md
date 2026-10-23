# Uppgift: Exempeltexter årsredovisning

uppdrag: uppdrag/2026-10-23-styrelserummet-exempelhandlingar.md
försök: 2
beror-på: 2026-10-23-styrelserummet-exempel-06-texter-underhallsplan-f2.md

## Vad som saknades
- Grenen innehöll enbart commit «Uppgift startad» (flytt `ny` → `pagar`); ingen kod i `ExempelTexter.kt` och ingen testfil `ExempelTexterArTest.kt`.
- `arsredovisningSidor()` implementerades inte.

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelTexter.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelTexterArTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/SyntetiskArsredovisningTest.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/HittaByggnadensVarde.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelTexterPlanTest.kt (JUnit 4-mönster)

## Uppgift
1. Lägg till `fun arsredovisningSidor(): List<String>` i `ExempelTexter.kt`. Returnera minst en sida (en sträng räcker) med markerbar text som innehåller:
   - en kort **förvaltningsberättelse** (påhittad) som nämner `FORENING_NAMN` (`Brf Exempelgården`) och räkenskapsåret `RAKENSKAPSAR` (2025);
   - **balansräkning** med två kolumner: innevarande år först, föregående år som andra kolumn med något **lägre** belopp;
   - raden **Byggnader och mark** där innevarande års belopp = `BYGGNADENS_VARDE` formaterat i kronor med mellanslag som tusentalsavgränsare (samma format som befintliga `formatKronor` i filen).

   Exempel på balansrad (får ligga på samma rad): `Byggnader och mark 27 000 000 26 500 000`.

2. Skapa `ExempelTexterArTest.kt` med test `arsredovisningInnehallerByggnaderOchMark`:
   - `val sammanslagenText = arsredovisningSidor().joinToString("\n")`
   - sammanslagen text innehåller `Byggnader och mark`
   - sammanslagen text innehåller `27 000 000`
   - `hittaByggnadensVarde(sammanslagenText)?.kronor` equals `BYGGNADENS_VARDE` (importera `hittaByggnadensVarde` från `se.tmconnect.styrelserummet.underlag`)

   Använd JUnit 4 (`org.junit.Test`, `org.junit.Assert`), inte `kotlin.test`.

## Exempel
- Balansrad: `Byggnader och mark 27 000 000 26 500 000` (föregående år får vara påhittat men lägre).

## Test som ska gå igenom
gradle :styrelserummet:testDebugUnitTest --tests "*ExempelTexterArTest"

## Gör inte
- Ändra `HittaByggnadensVarde.kt`.
- Lägga till riktiga revisorsnamn.
- Lämna testfilen tom eller utan asserts mot exemplen ovan.

utfall: godkänd 2026-10-23 (Cursor implementerade hela uppgiften; lokal modell levererade ingen kod)
