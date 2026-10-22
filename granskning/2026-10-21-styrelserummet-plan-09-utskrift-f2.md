beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- Endast `UtskriftHtml.kt`, `UtskriftPlanTest.kt` och uppgiftsfilens flytt ny→pagar→klar; inga filer i fel sökväg.
- `utskriftHtml` har parameter `plan: List<PlanAr> = emptyList()`, plan-tabell efter komponenttabellens `tabellram`, belopp via `kronor` och `htmlEsc`.
- Tom `plan` ska ge byte-identisk HTML; tom rad i mallen bröt det — rättat.
- Två JUnit 4-tester motsvarar exemplen (utan `Avskrivningsplan` / med år och formaterat belopp).
- Inga nya beroenden, behörigheter eller nätverksanrop; HTML är server-side utskrift, inte Android-UI-strängar.
- `:styrelserummet-server:test` och `*UtskriftPlanTest` gröna lokalt.

## Rättat av Cursor
- `UtskriftHtml.kt`: infoga `planHtml` utan extra indenterad tom rad när planen är tom (byte-identisk HTML).
- `UtskriftPlanTest.kt`: assert mot `kronor(60_000L)` i stället för hårdkodat beloppsliteral.

## Vid underkänd: försök 2

## Frågor till Claude
- inga
