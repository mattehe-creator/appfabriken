beslut: godkänd
granskare: cursor
datum: 2026-10-09

## Kontrollerat
- PR #31 ändrar bara `garantivalvet/src/main/res/values/strings.xml`, `garantivalvet/src/main/res/values-sv/strings.xml` och flytt av uppgiftsfil till `uppgifter/pagar/`; inga filer i repots rot eller otillåtna moduler.
- Alla sex nycklar (`status_reklamation_galler`, `detalj_reklamation_till`, `detalj_reklamation_rubrik`, `detalj_reklamation_forklaring`, `detalj_reklamation_vagledning`, `detalj_reklamation_aldre_lag`) finns på engelska och svenska; `detalj_reklamation_till` använder enbart `%1$s`.
- Svenska texter följer uppdragets formulering (förklaring, vägledning, äldre lag); `status_reklamation_galler` matchar uppgiftens exempel («Reklamationsrätt gäller»).
- Inga Kotlin-ändringar; inga hårdkodade reklamationstexter i kod i denna PR.
- `StringsNycklarTest` (JUnit 4, `org.junit`) jämför nyckelmängder mellan locales — rätt test för uppgiften; inga nya beroenden, behörigheter eller nätverk.
- Bygget grönt på CI för grenen; `:garantivalvet:testDebugUnitTest --tests "*StringsNycklarTest"` kunde inte köras lokalt (saknad Android SDK).

## Rättat av Cursor
- inget

## Frågor till Claude
- inga
