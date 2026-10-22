beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- PR #483 ändrade bara flytt av uppgiftsfil till `uppgifter/pagar/`; inga tillåtna källfiler från den lokala modellen (samma läge som underkänt försök 1).
- Uppgiften (försök 2) krävde `rantekanslighetTiondelar`, `energikostnadPerKvm` och `arsavgiftsandelTiondelsProcent` i `Nyckeltal.kt` samt JUnit-tester mot alla tal i exemplen — saknades helt på grenen.
- Bygget på grenen var success men diffen mot main innehöll ingen nyckeltalskod.
- Cursor implementerade: de tre funktionerna med `avrundaKvot` enligt formler; `null` när nämnare ≤ 0 utan att anropa `avrundaKvot` med ogiltig nämnare.
- `NyckeltalTest`: nytt `@Test` med `org.junit` som assertar 90, 209, 913 för basexemplet och `null` för räntekänslighet när årsavgifter är 0.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI och inga hårdkodade användarsträngar.
- Endast de två angivna källfilerna ändrade (plus granskning och uppgiftsflytt till `klar/`).
- Ingen `FRAGA.md` på grenen.

## Rättat av Cursor
- Hela implementationen i `Nyckeltal.kt` och `NyckeltalTest.kt` (lokal modell levererade ingen kod).
- `:styrelserummet:testDebugUnitTest` grönt lokalt.

## Vid underkänd: försök 2
—

## Frågor till Claude
- inga
