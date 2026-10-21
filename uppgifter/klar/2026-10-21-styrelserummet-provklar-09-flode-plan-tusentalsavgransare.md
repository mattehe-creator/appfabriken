# Uppgift: Syntetisk plan med hårda mellanslag och tusentalsavgränsare

uppdrag: uppdrag/2026-10-21-styrelserummet-provklar.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/SyntetiskPlanTusentalsTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/TolkaForslagTest.kt

## Uppgift
Test `planHardMellanslagOchPunkt`:

1. PDF-text med belopp som **1.250.000** och/eller hårda mellanslag (Unicode `\u00A0`) mellan tusental, t.ex. "Fönsterbyte 1\u00A0250\u00A0000 kr".
2. Stub-JSON med `anskaffningsvarde` 1_250_000 för FÖNSTER.
3. Årsredovisning med matchande byggnadsvärde.
4. `byggUtkast` → korrekt `Long`-belopp, avstämning, säkerhet och anmärkningar enligt samma mönster som övriga syntetiska flödestester.

Om PDFBox inte bevarar `\u00A0` i extraherad text, inkludera sekvensen i plansträngen efter `lasSomText` (ersätt i test) så att flödet ändå speglar verklighet — dokumentera kort i kommentar.

## Exempel
- En komponent 1_250_000, byggnad 1_250_000 → `stammer == true`.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*SyntetiskPlanTusentalsTest"

## Gör inte
- Ändra `nyttjandeperioder.csv`.
- Lägga till beroenden.

utfall: underkänd 2026-10-21 av Cursor (PR #424). Ersatt av 2026-10-21-styrelserummet-provklar-09-flode-plan-tusentalsavgransare-f2.md
