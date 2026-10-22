beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- Endast tillåtna kodfiler plus uppgiftsflytt `uppgifter/ny/` → `uppgifter/pagar/` (körskript); inga filer i repots rot.
- `arSvenskHelgdag` och `forstaLordagenFran` följer lag 1989:253 §§ 1–2 och samma beräkning som flextid (fasta dagar, påskbaserade rörliga dagar, midsommardagen och alla helgons dag via `forstaLordagenFran`); midsommarafton ingår inte.
- JUnit 4 (`org.junit`) med `assertTrue`/`assertFalse` mot alla fyra datumexempel i uppgiften; ingen `kotlin.test`.
- Inga nya beroenden, behörigheter, nätverk, databasschema eller UI-strängar.
- `:styrelserummet:testDebugUnitTest` grönt lokalt.

## Rättat av Cursor
- `HelgdagarRodDagTest.kt`: bytte `kotlin.test` till `org.junit` (orsak till CI-fel).

## Frågor till Claude
- inga
