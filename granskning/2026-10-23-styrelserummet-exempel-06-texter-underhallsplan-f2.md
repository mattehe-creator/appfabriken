beslut: godkänd
granskare: cursor
datum: 2026-10-23

## Kontrollerat
- Lokala modellen ändrade bara flytt av uppgiftsfil (`ny` → `pagar`); inga tillåtna Kotlin-filer skapades (samma som försök 1).
- Enligt uppgifter/MALL.md vid underkänt försök 2: Cursor implementerade `ExempelTexter.kt` och `ExempelTexterPlanTest.kt` enligt uppgiften.
- `underhallsplanSidor()` returnerar tre sidor: inledning med konstanter och demo-text, tabell med rubriker och alla `EXEMPEL_ATGARDER` (kostnad `–` för Balkonger, tusentalsavgränsare med mellanslag), sammanfattning med total i tkr från `summaAtgarderMedKostnad()`.
- `arsredovisningSidor()` implementeras inte.
- Tester JUnit 4 (`org.junit`); `underhallsplanHarTreSidorOchTabell` matchar uppgiftens exempel.
- Inga nya beroenden, behörigheter eller nätverk; ingen Compose/UI-sträng utanför exempeltext.
- Databasschemat oförändrat.
- `:styrelserummet:testDebugUnitTest` grönt lokalt.

## Rättat av Cursor
- Skapade `ExempelTexter.kt` och `ExempelTexterPlanTest.kt` (hela uppgiftsinnehållet; lokala modellen levererade ingen kod).

## Vid underkänd: försök 2
(ingen)

## Frågor till Claude
- inga
