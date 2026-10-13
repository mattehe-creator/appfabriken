beslut: godkänd
granskare: cursor
datum: 2026-10-13

## Kontrollerat
- Endast tillåtna käll- och testfiler plus uppgiftsflytt `ny/` → `pagar/` (lokalt workflow); inga filer i fel sökväg.
- `gissaVarutyp` följer uppgiftens regler: tokenisering, nyckelordsmatchning, kategoriordning, `null` vid osäkerhet (aldrig `OVRIGT`).
- Alla exempel i uppgiften finns som `assertEquals` i `VarutypGissningTest`.
- JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals`); inget kotlin.test.
- Inga nya beroenden, behörigheter eller nätverk; ren Kotlin utan Android i regelpaketet.
- Ingen UI-kod eller hårdkodade användarsträngar.

## Rättat av Cursor
- Nyckelord `mossa` → `mössa` i `KLADER_SKOR`-listan enligt uppgiftens ordlista.

## Frågor till Claude
- inga
