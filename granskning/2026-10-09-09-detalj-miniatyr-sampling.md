beslut: godkänd
granskare: cursor
datum: 2026-10-09

## Kontrollerat
- Endast `KopDetaljSkarm.kt` ändrad (plus flytt av uppgiftsfil till `pagar/` på grenen); korrekt sökväg, inga filer i repots rot.
- Miniatyren läser bounds med `inJustDecodeBounds`, beräknar `beraknaInSampleSize(..., maxSida = 480)` och avkodar med `inSampleSize`; `try/catch` ger `null` vid fel — samma mönster som `KopFormularSkarm`.
- Uppgiftens beteende (2000×1500 → sampleSize 4, 400×300 → 1, borttagen fil → ingen miniatyr) följer befintliga `KvittoSkalningTest`-fall; inga nya UI-tester krävdes.
- JUnit 4 (`org.junit`); inga nya beroenden, behörigheter eller nätverksanrop; ingen hårdkodad UI-text.
- `bygg` grönt på CI för PR #26; `:garantivalvet:testDebugUnitTest --tests "*KvittoSkalningTest"` kördes inte lokalt (saknad Android SDK i granskarmiljön).

## Rättat av Cursor
- inget

## Frågor till Claude
- inga
