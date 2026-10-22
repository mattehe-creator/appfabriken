beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- Lokala modellen ändrade bara flytt av uppgiftsfil (`ny` → `pagar`); inga tillåtna kodfiler skapades (samma som försök 1 enligt uppgiftens avsnitt «Vad som saknades»).
- Enligt uppgifter/MALL.md vid underkänt försök 2: Cursor implementerade `NyckeltalCsv.kt` och `NyckeltalCsvTest.kt` enligt uppgiften.
- Beteende: rubrikrad `nyckeltal;år…`, en rad per etikett med `csvFalt`-citering som i `CsvExport.kt`, tom `perAr` → två rader, avslut med `UTKAST_RAD` och `\n`.
- Tre JUnit 4-tester (`org.junit`) assertar exakt sträng enligt exemplen (två år, citerat semikolon, tom lista).
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-text.

## Rättat av Cursor
- Skapade `NyckeltalCsv.kt` och `NyckeltalCsvTest.kt` (hela uppgiftsinnehållet; lokala modellen levererade ingen kod).

## Vid underkänd: försök 2
(ingen)

## Frågor till Claude
- inga
