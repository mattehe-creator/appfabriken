beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- Endast angivna filer ändrade (AvgiftCsv.kt, AvgiftCsvTest.kt) plus flytt av uppgiftsfil till `pagar/` (lokal modell); inga filer i fel sökväg.
- `avgiftCsv` följer uppgiften: semikolon, radslut `\n` inklusive efter sista raden, tom rad mellan lägenhets- och trappdel, månadsformel `(år * 2 + 12) / 24`, tom sträng när båda listor är tomma.
- Tester jämför mot uppgiftens exempel och extra fall; beteende, inte bara att koden körs.
- Inga nya beroenden, behörigheter eller nätverk.
- Ingen UI-kod; CSV-rubriker enligt specifikation i uppgiften.
- Ingen databasändring.

## Rättat av Cursor
- `AvgiftCsvTest.kt`: `org.junit` / `org.junit.Assert.assertEquals` i stället för JUnit Jupiter och `kotlin.test` (orsak till CI-fel).
- Samma fil: förväntat månadsbelopp `(1_000_000 * 2 + 12) / 24` = 83333, inte 41667.

## Vid underkänd: försök 2
(ingen — godkänd)

## Frågor till Claude
- inga
