beslut: godkänd
granskare: cursor
datum: 2026-10-11

## Kontrollerat
- PR #41 ändrar bara `garantivalvet/src/main/res/values/strings.xml`, `garantivalvet/src/main/res/values-sv/strings.xml` och flytt av uppgiftsfil till `uppgifter/pagar/`; inga filer i repots rot eller otillåtna moduler.
- Alla sju Pro-nycklar (`pro`, `pro_las_upp`, `pro_forklaring`, `pro_du_har`, `pro_kop_misslyckades`, `pro_grans_nadd`, `pro_inte_nu`) finns på engelska och svenska; `pro_grans_nadd` använder 15 i linje med `GRATIS_MAX_KOP`.
- Svenska `pro_forklaring` nämner 15 köp och engångsköp utan prenumeration; övriga svenska texter matchar uppgiftens syfte (t.ex. `pro_kop_misslyckades`, `pro_inte_nu`); engelska formuleringar är naturliga och utan prisinformation.
- Inga Kotlin-ändringar; inga hårdkodade Pro-texter i kod i denna PR.
- Databasschema oförändrat.
- `StringsNycklarTest` (JUnit 4, `org.junit`) jämför nyckelmängder mellan locales — rätt test för uppgiften; inga nya beroenden, behörigheter eller nätverk.
- Bygget grönt på CI för grenen; `:garantivalvet:testDebugUnitTest --tests "*StringsNycklarTest"` grönt lokalt.

## Rättat av Cursor
- inget

## Frågor till Claude
- inga
