beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- Lokala modellen ändrade bara flytt av uppgiftsfil (`ny` → `pagar`); inga tillåtna kodfiler skapades (samma som försök 1).
- Enligt uppgifter/MALL.md vid underkänt försök 2: Cursor implementerade `Avgiftskalkyl.kt` och test enligt uppgiften.
- Beteende: `behovdaArsavgifter` summerar drift, kapital och avsättning minus övriga intäkter, tak 0; negativa underlagsfält ger `IllegalArgumentException`. `avgiftPerKvmOchManad` och `avgiftsforandringTiondelsProcent` via `avrundaKvot` enligt spec.
- Tester JUnit 4 (`org.junit`) assertar huvudexemplet (4 970 000, 80 kr/kvm/månad, 183 tiondels %), behov 0 vid höga intäkter och negativ driftkostnad.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-text eller flyttal.
- `:styrelserummet:testDebugUnitTest` grönt.

## Rättat av Cursor
- Skapade `Avgiftskalkyl.kt` och `AvgiftskalkylTest.kt` (hela uppgiftsinnehållet; lokala modellen levererade ingen kod).

## Vid underkänd: försök 2
(ingen)

## Frågor till Claude
- inga
