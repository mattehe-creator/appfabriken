beslut: godkänd
granskare: cursor
datum: 2026-10-19

## Kontrollerat
- PR #286 ändrade bara flytt av uppgiftsfil `ny/` → `pagar/`; inga `BokfortVarde.kt` / `BokfortVardeTest.kt` från den lokala modellen (försök 2 utan leverans, enligt uppgifter/MALL.md).
- Efter granskning: endast de två tillåtna filerna under `styrelserummet/…/regler/`, inga filer i repots rot.
- `bokfortVarde` summerar `avskrivningFor` från anskaffningsår till och med `arsslut`, tom intervall när `arsslut` före anskaffningsår, golv `maxOf(restvarde, …)`.
- JUnit 4-tester med `assertEquals` mot uppgiftens exempl (720_000 vid 2025, 1_200_000 vid 2009) och samma komponentdata som `AvskrivningTest`.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI; inget databasschema.
- `:styrelserummet:testDebugUnitTest` och `:styrelserummet:test --tests "*BokfortVardeTest"` grönt lokalt.

## Rättat av Cursor
- Hela `BokfortVarde.kt` och `BokfortVardeTest.kt` (lokal modell levererade ingen produktionskod eller tester i försök 2).

## Vid underkänd: försök 2

## Frågor till Claude
- inga
