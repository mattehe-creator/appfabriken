beslut: godkänd
granskare: cursor
datum: 2026-10-09

## Kontrollerat
- Endast de tre kodfiler uppgiften anger plus flytt av uppgiftsfilen från `uppgifter/ny/` till `pagar/` (lokal modell); inga filer i fel sökväg.
- `KopListRad` visar vad, valfritt var_kopt, garantislut via `lista_garanti_slut` + `formateraDatum(garantiSlut(...))`, status från `status_*` med primary/tertiary/error.
- `KopSkarmar` använder `KopListRad` i listan.
- `KopViewModel` exponerar sorterad lista (efter Cursor-rättning).
- Inga nya beroenden, behörigheter eller nätverk; databasschemat oförändrat.
- Inga hårdkodade UI-strängar i nya komponenten; strängar från `strings.xml`.
- `KopSorteringTest` (JUnit 4) grönt lokalt via `:garantivalvet:testDebugUnitTest`; testerna matchar uppgiftens sorteringsexempel (befintliga assertions på garantislut och ordning).

## Rättat av Cursor
- `KopViewModel`: lade bara till import utan `map { sorteraEfterGaranti(...) }`; implementerade sortering med injicerbar `idag: LocalDate = LocalDate.now()`.
- `KopListRad`: `heightIn(min = 48.dp)`, import av `GarantiStatus`, borttagna oanvända imports.
- `KopSkarmar`: tog bort oanvänd `KopRadSkelett` och import `clickable`.

## Vid underkänd: försök 2
(ingen)

## Frågor till Claude
- inga
