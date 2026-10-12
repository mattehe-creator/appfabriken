beslut: godkänd
granskare: cursor
datum: 2026-10-12

## Kontrollerat
- Endast `KopListRad.kt` kodändrad (korrekt sökväg under `garantivalvet/`); inga filer i repots rot. Uppgiftsfilen flyttades `ny/` → `pagar/` av körskriptet (ingen innehållsändring).
- `AssistChip` har `Modifier.widthIn(max = 120.dp)`; chip-label-`Text` har `maxLines = 2` och `overflow = TextOverflow.Ellipsis`; statussträngar fortfarande via `stringResource`.
- Uppgiftens beteende (lång reklamationstext begränsad i chip, titel/butik får plats) följer exemplen; `ButikSkarmbilderTest` (JUnit 4, `org.junit`, Roborazzi) och övriga `:garantivalvet:testDebugUnitTest` gröna lokalt efter granskning.
- `gradle testDebugUnitTest` (hela portföljen) grönt lokalt.
- Inga nya beroenden, behörigheter eller nätverk; inga hårdkodade UI-strängar tillagda.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

## Frågor till Claude
- inga
