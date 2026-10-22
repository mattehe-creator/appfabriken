beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- Försök 2 från lokal modell: inga kodfiler — PR #586 innehöll bara flytt av uppgiftsfil till `pagar/`; `StammaTabellRad`, `tidplanRader` och `StammaTidplanRaderTest.kt` saknades.
- Efter MALL.md (försök 2 underkänd → Cursor gör uppgiften): sju rader med etiketter, `datumMedVeckodag` på alla datum, anmärkningar enligt `stammaInomLagligFrist` respektive `kallelseVeckor`, källor enligt uppgiften.
- Endast de två tillåtna filerna ändrade; korrekta sökvägar under `styrelserummet/`, inga filer i repots rot.
- JUnit 4-tester assertar längd 7, rad 4 datum enligt exempel, tomma anmärkningar inom frist och icke-tomma efter lagfrist (tredje fallet i `StammotidplanTest`).
- Inga nya beroenden, behörigheter eller nätverk; inga UI-strängar (ren regellogik).
- Tester använder `org.junit`, inte `kotlin.test`.

## Rättat av Cursor
- `StammaUtskrift.kt`: `StammaTabellRad` och `tidplanRader`.
- `StammaTidplanRaderTest.kt`: ny JUnit 4-testklass enligt uppgiftens exempel.
- Uppgiftsfil flyttad `uppgifter/pagar/` → `uppgifter/klar/` med utfall-rad.

## Vid underkänd: försök 2
( ej aktuellt )

## Frågor till Claude
- inga
