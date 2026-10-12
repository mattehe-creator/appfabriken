beslut: godkänd
granskare: cursor
datum: 2026-10-12

## Kontrollerat
- Endast `MainActivity.kt` och `KopViewModel.kt` ändrade enligt uppgiften (plus uppgiftsflytt `pagar/` → `klar/`).
- `OpenDocument()` med MIME `application/zip` och `application/octet-stream`; `onHamtaSakerhetskopia` startar kontraktet.
- `Sakerhetskopia.lasIn` vid vald fil; vid `Ok` import via repository; vid `OkandVersion` resp. övriga läsfel snackbar med `sakerhetskopia_nyare_version` / `sakerhetskopia_kunde_inte_lasas`.
- Lyckad import: `PaminnelsePlanerare.synka` när påminnelser är på; meddelande `sakerhetskopia_importerad` med antal tillagda och hoppade (matchar exemplen via strängresurser).
- Läsning och felklassificering ligger i `KopViewModel.lasInSakerhetskopia` i stället för i `MainActivity` som uppgiftstexten anger; beteendet i UI stämmer.
- Inga nya beroenden, behörigheter eller nätverk; inga hårdkodade UI-strängar.
- Angivet test `StringsNycklarTest` (JUnit 4) och hela `:garantivalvet:testDebugUnitTest` grönt lokalt.

## Rättat av Cursor
- Stängde `InputStream` med `use` efter `lasIn`.
- Ersatte nästlade `viewModelScope.launch` i import med `suspend`-funktion anropad från samma koroutine.

## Vid underkänd: försök 2
(ingen)

## Frågor till Claude
- inga
