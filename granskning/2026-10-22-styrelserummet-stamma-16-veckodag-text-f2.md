beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- Försök 2 från lokal modell: inga kodfiler — PR #582 innehöll bara flytt av uppgiftsfil till `pagar/`; `StammaUtskrift.kt` och `StammaVeckodagTest.kt` saknades.
- Efter MALL.md (försök 2 underkänd → Cursor gör uppgiften): `datumMedVeckodag` med ISO-datum och svenska veckodagar i gemener via `when` på `DayOfWeek`; JUnit 4-test med tre `assertEquals` enligt exemplen.
- Endast de två tillåtna filerna ändrade; korrekta sökvägar under `styrelserummet/`, inga filer i repots rot.
- Inga nya beroenden, behörigheter eller nätverk; inga UI-strängar (ren regellogik).
- Tester använder `org.junit`, inte `kotlin.test`.

## Rättat av Cursor
- `StammaUtskrift.kt`: ny fil med `datumMedVeckodag`.
- `StammaVeckodagTest.kt`: ny JUnit 4-testklass enligt uppgiftens exempel.
- Uppgiftsfil flyttad `uppgifter/pagar/` → `uppgifter/klar/` med utfall-rad.

## Vid underkänd: försök 2
( ej aktuellt )

## Frågor till Claude
- inga
