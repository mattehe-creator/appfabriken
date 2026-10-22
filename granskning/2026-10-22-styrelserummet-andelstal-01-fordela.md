beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- Endast `Andelstal.kt`, `AndelstalTest.kt` och uppgiftsflytt pagar→klar efter rättelse; inga filer kvar i repots rot.
- `fordelaEfterAndelstal` följer största-resten-metoden i uppgiften; heltalsräkning utan flyttal; ogiltiga indata ger `null`.
- `AndelstalTest` jämför mot alla exempel i uppgiften (inkl. noll total, tom lista, negativt total, negativt andelstal, summa noll).
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-kod eller hårdkodade strängar.
- JUnit 4 (`org.junit`) efter rättelse; `:styrelserummet:testDebugUnitTest` grönt lokalt.

## Rättat av Cursor
- Flyttat implementation och test från repots rot till `styrelserummet/.../regler/` (modulens tomma platshållarfiler).
- `AndelstalTest.kt`: bytt från JUnit Jupiter och `kotlin.test` till JUnit 4.
- `Andelstal.kt`: explicit kontroll av negativa andelstal (krav i uppgiften utöver `summa == 0`).

## Vid underkänd: försök 2
—

## Frågor till Claude
- inga
