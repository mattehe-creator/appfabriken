beslut: godkänd
granskare: cursor
datum: 2026-10-15

## Kontrollerat
- Endast `LoggGrupp.kt`, `LoggGruppTest.kt` plus uppgiftsflytt `ny/` → `pagar/` (lokalt workflow); rätt sökvägar under `husloggen/`.
- `ArGrupp` och `grupperaPerAr` matchar signaturerna i uppgiften; gruppering på `datum.year`, fallande år, ordning inom år via `groupBy`, `summaOre` bara för icke-null `beloppOre`.
- Huvudtestet följer exemplen (2026: summa 50_000, 2 poster; 2025: summa 100_000, 1 post) plus tom lista och enbart null-belopp.
- JUnit 4 (`org.junit.Test`); inget kotlin.test.
- Inga nya beroenden, behörigheter eller nätverk; ren Kotlin utan UI.
- Ingen hårdkodad användartext i produktionskod.

## Rättat av Cursor
- Tog bort oanvänd import `java.time.LocalDate` i `LoggGrupp.kt`.

## Vid underkänd: försök 2

## Frågor till Claude
- inga
