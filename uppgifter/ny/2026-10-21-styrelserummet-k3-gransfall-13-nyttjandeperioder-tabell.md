# Uppgift: Standardtabellen nyttjandeperioder.csv

uppdrag: uppdrag/2026-10-21-styrelserummet-k3-gransfall.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/NyttjandeperioderTabellGransfallTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/NyttjandeperioderTabell.kt
- styrelserummet/src/main/resources/nyttjandeperioder.csv
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Komponentgrupp.kt

## Uppgift
Testa `lasNyttjandeperioderFranResurs()` (eller `lasNyttjandeperioder` på filinnehållet):
1. Varje `Komponentgrupp` **utom** de som uttryckligen saknar rad ska ha en rad i tabellen (minst en rad per enum-värde som finns i CSV).
2. För rader med intervall: `intervallAr!!.first ≤ forslagAr ≤ intervallAr.last`.
3. **MARK_OCH_GARAGE** har andel 0 och intervall 0 — ska finnas men räknas inte in i procentsumman.
4. Summan av `standardandelProcent` för alla grupper **utom** `MARK_OCH_GARAGE` ska vara **100**.

Vid fel i data eller kod: rätta högst **en** av `nyttjandeperioder.csv` eller `NyttjandeperioderTabell.kt`.

## Exempel
- Minst 12 rader (en per grupp) efter parsning.
- STOMME: forslag 100 inom 75–120, andel 63.
- Summa andelar (exkl. mark) = 100.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*NyttjandeperioderTabellGransfallTest"

## Gör inte
- Ändra `NyttjandeperiodForslag`-testfiler (parallell kö).
- Lägga till beroenden.
