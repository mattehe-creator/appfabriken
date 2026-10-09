beslut: godkänd
granskare: cursor
datum: 2026-10-09

## Kontrollerat
- Endast `KopFormularSkarm.kt` och `KvittoSkalningTest.kt` ändrade (plus flytt av uppgiftsfil till `pagar/` på grenen); inga filer i fel sökväg.
- Förhandsvisningen läser bounds med `inJustDecodeBounds`, beräknar `beraknaInSampleSize(..., maxSida = 480)` och avkodar med `inSampleSize`; `try/catch` ger fortfarande `null` vid fel.
- Nya testfall matchar uppgiftens exempel (4000×3000 → 8, 400×300 → 1).
- JUnit 4 (`org.junit`); inga nya beroenden, behörigheter eller nätverksanrop; ingen hårdkodad UI-text.
- `testDebugUnitTest` grönt lokalt och på CI för grenen.

## Rättat av Cursor
- inget

## Frågor till Claude
- inga
