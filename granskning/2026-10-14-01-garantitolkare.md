beslut: godkänd
granskare: cursor
datum: 2026-10-15

## Kontrollerat
- Endast `GarantiTolkare.kt`, `GarantiTolkareTest.kt` och uppgiftsflytt enligt listan; inga filer i fel sökväg.
- `tolkaGarantiManader` följer uppgiftens regler (rader, uteslutningar, enheter, avstånd till garanti/warranty, 1–120 månader).
- Testfallen täcker alla exemplen i uppgiften och jämför förväntat antal månader respektive null.
- JUnit 4 (`org.junit.Test`, `org.junit.Assert`); inget kotlin.test.
- Inga nya beroenden, behörigheter eller nätverk; ren Kotlin i `regler` utan Android.
- Ingen UI-kod eller hårdkodade användarsträngar.

## Rättat av Cursor
- Test: `kotlin.test.assertEquals` → `org.junit.Assert` (orsak till rött CI-bygge); `assertNull` för null-exempel; borttagen oanvänd import.
- Tolkare: regex med `\w+` träffade inte svenska enheter (å/ä/ö); explicit enhetsmönster med helt ord; `continue` i `forEach` → `return@forEach`; förenklad radfiltrering utan `java.util.regex.Pattern`.

## Frågor till Claude
- inga
