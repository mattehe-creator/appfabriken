beslut: godkänd
granskare: cursor
datum: 2026-10-12

## Kontrollerat
- Endast `KopFormularSkarm.kt` kodändrad (korrekt sökväg under `garantivalvet/`); inga filer i repots rot; `Formatering.kt` och övriga skärmar orörda. Uppgiftsfilen flyttades `ny/` → `pagar/` av körskriptet (ingen innehållsändring).
- `LocalConfiguration` importerad; `val locale = LocalConfiguration.current.locales[0]` tillagd; köpdatumfältet anropar `formateraDatum(kopdatum, locale)` i stället för default svenska locale.
- Befintlig `FormateringTest` (JUnit 4, `org.junit`) täcker locale-baserad datumformatering; uppgiftens exempel följer samma `formateraDatum`-funktion som redan testas.
- `gradle :garantivalvet:testDebugUnitTest --tests "*FormateringTest"` och `:garantivalvet:testDebugUnitTest` grönt lokalt efter granskning.
- Inga nya beroenden, behörigheter eller nätverk; inga hårdkodade UI-strängar tillagda.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

## Frågor till Claude
- inga
