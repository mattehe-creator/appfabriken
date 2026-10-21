beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast `GaHemTid.kt`, `GaHemTidTest.kt` och uppgiftsfilen ändrade; korrekt sökväg under `flextid/`, inget i repots rot.
- `gaHemTid` följer uppgiften: minuter från midnatt, negativa rast/normaltid som 0, `null` vid `m >= 24 * 60`, sekunder ignoreras.
- Tester jämför mot alla sju exemplen i uppgiften.
- JUnit 4 (`org.junit`), inte `kotlin.test`.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-kod eller hårdkodade strängar.
- `gradle testDebugUnitTest` grönt lokalt.

## Rättat av Cursor
- Testimports: `org.junit.Test`, `org.junit.Assert.assertEquals` och `assertNull` (bygget failade på `kotlin.test`).

## Vid underkänd: försök 2

## Frågor till Claude
- inga
