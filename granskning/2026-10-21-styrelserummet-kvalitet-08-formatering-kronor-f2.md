beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast filerna under "Ändra bara dessa filer" plus uppgiftsflytt pagar är ändrade; inga filer i repots rot.
- `kronor(Long)` formaterar absolutbelopp med U+00A0, negativa med U+2212, avslutar med `" kr"`; `0` → `"0 kr"`.
- `FormateringTest.kronor` assertar exakt alla tre exemplen med JUnit 4 (`org.junit`).
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Ingen UI-kod; inga hårdkodade användarsträngar utanför testförväntningar.
- `:styrelserummet:testDebugUnitTest` grönt lokalt.

## Rättat av Cursor
- Lokala modellen lämnade tomma `Formatering.kt` och `FormateringTest.kt` (samma som försök 1). Cursor implementerade enligt uppgiften (försök 2 underkänt → Cursor gör uppgiften, uppgifter/MALL.md).

## Vid underkänd: försök 2
(otillämpligt)

## Frågor till Claude
- inga
