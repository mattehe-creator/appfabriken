beslut: godkänd
granskare: cursor
datum: 2026-10-19

## Kontrollerat
- Endast `Avskrivning.kt`, `AvskrivningTest.kt` plus uppgiftsflytt `ny/` → `pagar/` (lokalt workflow); rätt sökvägar under `styrelserummet/`, inga filer i repots rot.
- `avskrivningFor` matchar uppgiften: linjär avskrivning, 0 före/efter perioden, normalt årsbelopp avrundat, sista året tar resten så summan blir exakt avskrivningsbart belopp.
- Två JUnit 4-tester med `assertEquals` mot exemplen (1,2 M/40 år och 100 k/3 år inkl. 0 före/efter).
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-kod eller hårdkodade användarsträngar i produktionskod.
- Ingen databasschemaändring.
- `:styrelserummet:testDebugUnitTest` och `:styrelserummet:test --tests "*AvskrivningTest"` grönt (lokalt verifierat).

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

## Frågor till Claude
- inga
