beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- PR #519 ändrade bara flytt av uppgiftsfil till `uppgifter/pagar/` — samma fel som underkänt försök 1: varken `ManadsCsv.kt` eller testfilen fanns på grenen.
- Uppgiften (försök 2) krävde `typEtiketter`, `helgdagNamn`, helgdagskolumn i CSV och JUnit 4-test mot exemplen — saknades helt.
- Endast tillåtna filer ändrade: `ManadsCsv.kt` och ny `ManadsCsvHelgdagTest.kt` (sökvägar under `flextid/`, inte reporoten).
- `ManadsCsvTexter` har `typEtiketter` och `helgdagNamn`; `byggManadsCsv` använder etikett eller enum-namn samt `helgdagPa(..., medAftnar = true)` med `helgdagNamn`.
- Tester (JUnit 4) kontrollerar radsuffix enligt uppgiftens exempel (Långfredagen, ARBETE utan helgdag, överstyrt `helgdagNamn` på julafton).
- Inga nya beroenden, behörigheter eller nätverk; inga hårdkodade UI-strängar tillagda.

## Rättat av Cursor
- Hel implementation i `ManadsCsv.kt` och ny `ManadsCsvHelgdagTest.kt` (lokal modell levererade ingen kod igen).
- `:flextid:testDebugUnitTest --tests "*ManadsCsv*"` grönt lokalt.

## Vid underkänd: försök 2


## Frågor till Claude
- inga
