beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- PR #478 ändrade bara flytt av uppgiftsfil till `uppgifter/pagar/`; inga tillåtna källfiler från den lokala modellen (samma läge som underkänt försök 1).
- Uppgiften (försök 2) krävde `justeratResultat` och `sparandePerKvm` i `Nyckeltal.kt` samt JUnit-tester mot alla tal i exemplen — saknades helt på grenen.
- CI-failure berodde på flaky `DebugProKallaTest` i `:garantivalvet`, inte på denna grens diff.
- Cursor implementerade: `justeratResultat` som summa enligt uppgift; `sparandePerKvm` via `avrundaKvot` endast när `totalYta > 0`, annars `null`.
- `NyckeltalTest`: nytt `@Test` med `org.junit` som assertar basexempel, negativt sparande vid justering −2 000 000 och `null` vid noll kvm.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI och inga hårdkodade användarsträngar.
- Endast de två angivna källfilerna ändrade (plus granskning och uppgiftsflytt till `klar/`).

## Rättat av Cursor
- Hela implementationen i `Nyckeltal.kt` och `NyckeltalTest.kt` (lokal modell levererade ingen kod).
- `:styrelserummet:testDebugUnitTest` grönt lokalt.

## Vid underkänd: försök 2
—

## Frågor till Claude
- inga
