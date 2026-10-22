beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- Försök 2 från lokal modell: ingen kod — PR #584 hade bara commit «Uppgift startad»; `Overlatelseavgift.kt` och `OverlatelseavgiftBeraknaTest.kt` saknades.
- Efter MALL.md (försök 2 underkänd → Cursor gör uppgiften): `Avgiftstyp`, `avgiftFranPromille` med heltalsdivision (nedåt), validering av negativa indata, `hogstaOverlatelseavgift` / `hogstaPantsattningsavgift` via `referens_*_0` i data, `hogstaAndrahandsavgift` med 100 promille.
- Endast de två tillåtna filerna tillagda; korrekta sökvägar under `styrelserummet/`, inga filer i repots rot.
- JUnit 4-tester med `assertEquals` mot alla fyra uppgiftsexempel plus överlåtelse/pantsättning via `lasStammaData`; ingen `kotlin.test`.
- Inga nya beroenden, behörigheter eller nätverk; inga hårdkodade UI-strängar.
- `:styrelserummet:test --tests "*OverlatelseavgiftBeraknaTest*"` och `:styrelserummet:testDebugUnitTest` grönt lokalt.

## Rättat av Cursor
- Hela `Overlatelseavgift.kt` och `OverlatelseavgiftBeraknaTest.kt` (lokal modell levererade ingen kod på försök 2).

## Vid underkänd: försök 2
( ej aktuellt )

## Frågor till Claude
- inga
