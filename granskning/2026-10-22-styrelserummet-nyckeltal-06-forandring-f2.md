beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- Lokala modellen ändrade bara flytt av uppgiftsfil (`ny` → `pagar`); inga tillåtna kodfiler skapades (samma som försök 1).
- Enligt uppgifter/MALL.md vid underkänt försök 2: Cursor implementerade `NyckeltalForandring.kt` och test enligt uppgiften.
- Beteende: `forandringTiondelsProcent` via `avrundaKvot((nu - foregaende) * 1000, foregaende)` med `null` vid `null` eller `foregaende <= 0`; `formateraForandring` med en-dash, Unicode-minus och en decimal utan flyttal.
- Tester JUnit 4 (`org.junit`) assertar alla fem exemplen (värden och strängar inkl. `null` och `"–"`).
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-text.

## Rättat av Cursor
- Skapade `NyckeltalForandring.kt` och `NyckeltalForandringTest.kt` (hela uppgiftsinnehållet; lokala modellen levererade ingen kod).

## Vid underkänd: försök 2
(ingen)

## Frågor till Claude
- inga
