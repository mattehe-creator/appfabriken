beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- PR #541 ändrade bara flytt av uppgiftsfil till `uppgifter/pagar/`; inga källfiler från den lokala modellen (försök 2 levererade ingen kod, till skillnad från försök 1 som hade tomma filer).
- Uppgiften krävde `UppgiftKostnad.kt` och `UppgiftKostnadTest.kt` med `senasteKostnadPerUppgift` enligt spec.
- Cursor implementerade filtrering på `uppgiftId` och nettobelopp via `postBeloppForSammanstallningOre`, gruppering per uppgift, vinnare på senast `datum` och vid lika datum högst `id`.
- Tester: `assertEquals` på hela map:en från uppgiftens sex poster; tomlisttest; JUnit 4 (`org.junit`), inte `kotlin.test`.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI eller hårdkodade strängar.
- Endast de två angivna källfilerna tillagda (plus granskning och uppgiftsflytt).

## Rättat av Cursor
- Ny `husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/UppgiftKostnad.kt` och `UppgiftKostnadTest.kt` (lokal modell levererade ingen kod på försök 2).
- `:husloggen:testDebugUnitTest` grönt lokalt.

## Vid underkänd: försök 2


## Frågor till Claude
- inga
