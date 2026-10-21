beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Diff mot main: lokala modellen ändrade bara flytt till `pagar/` (samma som försök 1); inga kodfiler. Cursor lade till endast `GrupperaNamnTest.kt` enligt uppgiften; `GrupperaNamn.kt` oförändrad (befintlig logik klarade alla null-exempel).
- `gissaGruppNullfall` med alla fyra exemplen (`""`, `Stomme i betong`, `Elisabeths trädgård`, `Balkong`); JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals`); inga nya beroenden, behörigheter eller nätverk; ingen UI-kod.
- Inga filer utanför listan «Ändra bara dessa filer»; rätt sökvägar under `styrelserummet/`.
- `:styrelserummet:test --tests "*GrupperaNamnTest.gissaGruppNullfall"` och `:styrelserummet:testDebugUnitTest` grönt lokalt.

## Rättat av Cursor
- Testmetod `gissaGruppNullfall` efter försök 2 utan kod (enligt uppgifter/MALL.md).

## Vid underkänd: försök 2

## Frågor till Claude
- inga
