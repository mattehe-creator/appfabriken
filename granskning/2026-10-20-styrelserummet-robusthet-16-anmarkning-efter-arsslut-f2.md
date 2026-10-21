beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast filer under "Ändra bara dessa filer" ändrade; korrekta sökvägar under styrelserummet/.
- Signaturen `anmarkningarFor(..., arsslut: Int)` och regeln `anskaffningsar > arsslut` med svensk anmärkning enligt uppgiften; befintliga regler oförändrade.
- `ByggUtkast.byggUtkast` skickar `nuAr` som tredje argument.
- Test `anmarkningAnskaffningsarEfterArsslut` jämför mot exemplen (2026/2025 respektive 2025 utan regel-träff).
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Anmärkningstext i domänlogik enligt befintligt mönster i samma fil (ingen Compose-UI).
- Enhetstest använder JUnit 4 (org.junit).

## Rättat av Cursor
- Återställde testet `anmarkningAnskaffningsarForeByggnadsar` (uppdaterat med `arsslut`) som försök 2 ersatte i stället för att lägga till nytt test.
- Tog bort onödig null-kontroll på `anskaffningsar` (är `Int`, inte nullable).

## Vid underkänd: försök 2
(ingen)

## Frågor till Claude
- inga
