beslut: godkänd
granskare: cursor
datum: 2026-10-23

## Kontrollerat
- PR #608 ändrar bara `ExempelTexter.kt` och `ExempelTexterStamAvTest.kt` (korrekt sökväg under `styrelserummet/`); plus uppgiftsflytt och granskning.
- `exempelKomponenterFranPlan()` filtrerar åtgärder med kostnad, sätter grupp via `gissaGrupp` med explicit `Entréparti` / `Brandlarm`, nyttjandeperiod enligt tabell, `Sakerhet.HOG` och `kalla = "exempel-underhallsplan"`.
- Testet `planSummaStammerMedByggnadEnligtStamAv` använder JUnit 4 och assertar 13 komponenter, summa 27_000_000, `stamAv` stämmer och `av.summa == summaAtgarderMedKostnad()`.
- Inga ändringar i `ExempelData.kt`, `stamAv` eller modeller; inga nya beroenden, behörigheter eller nätverk.
- `kalla` är exempelmetadata enligt uppgiften, inte UI-strängar.
- Ingen `FRAGA.md` på grenen.
- CI/bygge failade på grenen p.g.a. icke-exhaustiv `when (grupp)` (saknade `STOMME`); rättat av Cursor.

## Rättat av Cursor
- `Komponentgrupp.STOMME -> 100` i `when (grupp)` så Kotlin-kompilering blir exhaustive (ingen planåtgärd mappas till STOMME).
- Borttagen oanvänd import `stamAv` i `ExempelTexter.kt`.
- `:styrelserummet:testDebugUnitTest` grönt lokalt.

## Vid underkänd: försök 2
—

## Frågor till Claude
- inga
