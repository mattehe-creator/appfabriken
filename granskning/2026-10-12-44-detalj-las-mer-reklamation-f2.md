beslut: godkänd
granskare: cursor
datum: 2026-10-12

## Kontrollerat
- PR #109 ändrar enbart `KopDetaljSkarm.kt` och flytt av uppgiftsfilen `ny/` → `pagar/` (strängfilerna behövde inte ändras; `detalj_reklamation_las_mer` och `detalj_reklamation_vagledning` fanns redan).
- `ReklamationsKort` visar rubrik, datumrad vid reklamation, `TextButton` med `detalj_reklamation_las_mer`, expanderat tillstånd via `remember { mutableStateOf(false) }`, och `AnimatedVisibility` med förklaring + vägledning; äldre lag utan «Läs mer» oförändrat.
- Inga nya beroenden, behörigheter eller nätverk; inga hårdkodade UI-strängar i ändringarna.
- Angivet test `*StringsNycklarTest` grönt lokalt (`testDebugUnitTest`).
- Den lokala modellen hade utanför uppgiften kommenterat bort tidslinje i `StatusKort`; det återställdes (se Rättat).

## Rättat av Cursor
- `KopDetaljSkarm.kt`: återställd `TidslinjeMarkorer`, `beraknaTidslinje`, parameter och `TidslinjeStapel` i `StatusKort`; borttagna tillfälliga kommentarer kring kvittobild.

## Frågor till Claude
- inga
