beslut: godkänd
granskare: cursor
datum: 2026-10-11

## Kontrollerat
- Efter granskning: endast `kvittofil_paths.xml`, `KopDetaljSkarm.kt`, `MainActivity.kt` samt uppgifts-/granskningsfiler; försök 1 hade lagt samma tre filer i repots rot utan att ändra modulen.
- `cache-path` `export/` tillagd; Dela-knapp `export_dela` minst 48 dp med samma Pro-gate som export; Pro skriver PDF till `cacheDir/export/` och öppnar systemdelning via FileProvider och `ACTION_SEND`.
- Inga nya beroenden, manifestbehörigheter eller nätverksanrop.
- UI-text via `stringResource`/`getString(R.string.export_dela)`; inga hårdkodade UI-strängar.
- Databasschemat oförändrat.
- `StringsNycklarTest` (JUnit 4) grönt lokalt efter flytt till rätt sökvägar.

## Rättat av Cursor
- Flyttade implementation från repots rot till `garantivalvet/…`, tog bort rotfiler.
- Saknade imports (`File`, `FileOutputStream`, `FileProvider`); `KvittoLager.fileProviderAuthority` och `startActivity` enligt `KvittoHelskarm` i stället för onödig `StartActivityForResult`-kontrakt.

## Vid underkänd: försök 2

## Frågor till Claude
- inga
