beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Diff mot main: endast `GrupperaNamn.kt`, `GrupperaNamnTest.kt` och flytt av uppgiftsfil till `pagar/`; inga filer i repots rot.
- Nyckelord för STAMMAR och VENTILATION tillagda enligt uppgiften; befintliga TAK/FASAD/FONSTER_DORRAR oförändrade; samma `gissaGrupp`-logik.
- `gissaGruppStammarVentilation` testar alla fyra exemplen med förväntade `Komponentgrupp`-värden; `gissaGruppTakFasadFonster` kvar.
- JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals`); inga nya beroenden, behörigheter eller nätverk; ingen UI-kod.
- `:styrelserummet:testDebugUnitTest` grönt lokalt.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

## Frågor till Claude
- inga
