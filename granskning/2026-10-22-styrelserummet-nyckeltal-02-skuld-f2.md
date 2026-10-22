beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- PR #474 ändrade bara flytt av uppgiftsfil till `uppgifter/pagar/`; inga tillåtna källfiler från den lokala modellen (samma läge som underkänt försök 1).
- Uppgiften (försök 2) krävde fyra funktioner i `Nyckeltal.kt` och JUnit-tester mot alla tal i exemplen — saknades helt på grenen.
- Cursor implementerade: `totalYta`, `arsavgiftPerKvmBostadsratt`, `skuldsattningPerKvm`, `skuldsattningPerKvmBostadsratt` med `null` vid nämnare ≤ 0 utan att anropa `avrundaKvot` då.
- `NyckeltalTest`: nytt `@Test` med `org.junit` som assertar exempelunderlaget och noll kvm; befintliga `avrundaKvot`-tester oförändrade i beteende.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI och inga hårdkodade användarsträngar.
- Endast de två angivna källfilerna ändrade (plus granskning och uppgiftsflytt till `klar/`).

## Rättat av Cursor
- Hela implementationen i `Nyckeltal.kt` och `NyckeltalTest.kt` (lokal modell levererade ingen kod).
- Test: `Long`-literaler i `assertEquals` så JUnit 4 inte jämför `Integer` med `Long`.
- `:styrelserummet:testDebugUnitTest` grönt lokalt.

## Vid underkänd: försök 2
—

## Frågor till Claude
- inga
