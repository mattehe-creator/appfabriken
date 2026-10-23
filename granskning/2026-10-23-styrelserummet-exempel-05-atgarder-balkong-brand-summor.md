beslut: godkänd
granskare: cursor
datum: 2026-10-23

## Kontrollerat
- Endast ExempelData.kt och ExempelDataSummaTest.kt ändrade (plus uppgiftsflytt pagar→klar), inga filer i fel sökväg
- Balkonger (2038, null) och Brandlarm (2026, 200_000) tillagda; BYGGNADENS_VARDE och summaAtgarderMedKostnad() enligt uppgift
- Summan av kostnader med värde är 27_000_000; inga belopp i tidigare grupper ändrade
- Test jämför mot exemplen (14 åtgärder, en null, summa, områdesdelsträngar)
- Inga nya beroenden, behörigheter eller nätverk; inga UI-strängar
- ExempelDataSummaTest använder org.junit (JUnit 4)

## Rättat av Cursor
- Bytte kotlin.test-import till org.junit.Assert (orsakade kompileringsfel i CI)
- Döpte om testmetoden till summaOchByggnadsvVardeStammer och lade till assert på exakt en null-kostnad
- ignoreCase på delsträngar i summa-testet (namn har versaler)
- Uppdaterade ExempelDataAtgarderGrupp1Test och Grupp3Test (antal 14, null-säker summa) så hela testDebugUnitTest blir grönt

## Vid underkänd: försök 2

## Frågor till Claude
- inga
