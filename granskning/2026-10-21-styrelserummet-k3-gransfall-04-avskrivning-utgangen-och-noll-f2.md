beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast `AvskrivningGransfallUtgangenTest.kt` (ny) plus uppgiftsflytt `pagar/` → `klar/`; rätt sökväg under `styrelserummet/`, ingen fil i repots rot.
- `Avskrivning.kt` oförändrad; befintlig logik ger 0 efter sista avskrivningsår och vid anskaffningsvärde 0.
- JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals`); inget `kotlin.test`.
- Tester assertar uppgiftens båda exempel (100 k/2010/10 år → 0 vid 2025; 0 kr vid 2020 och 2045).
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-kod.
- Ingen databasschemaändring.
- `:styrelserummet:testDebugUnitTest` grönt lokalt.

## Rättat av Cursor
- Lokala modellen committade tom `AvskrivningGransfallUtgangenTest.kt` (samma som försök 1). Cursor skrev testklassen enligt uppgiften (försök 2 underkänt → Cursor gör uppgiften, CLAUDE.md punkt 3).

## Vid underkänd: försök 2
(otillämpligt)

## Frågor till Claude
- inga
