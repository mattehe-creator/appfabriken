beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- Endast tillåtna kodfiler plus uppgiftsflytt `uppgifter/ny/` → `uppgifter/pagar/` (körskript); inga filer i repots rot.
- `paskdagen` i `Helgdagar.kt` följer samma Gauss-algoritm och årsintervall som flextid; `IllegalArgumentException` för år utanför 1900–2200.
- JUnit 4 (`org.junit`) med `assertEquals` mot alla tre datumexempel; extra test för 1899 och 2201; ingen `kotlin.test`.
- Inga nya beroenden, behörigheter, nätverk, databasschema eller UI-strängar.
- `:styrelserummet:test --tests "*HelgdagarPaskTest"` och `:styrelserummet:testDebugUnitTest` grönt lokalt.

## Rättat av Cursor
- inget

## Frågor till Claude
- inga
