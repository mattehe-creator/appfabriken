beslut: godkänd
granskare: cursor
datum: 2026-10-11

## Kontrollerat
- PR #70 ändrar enbart `garantivalvet/src/main/res/values/strings.xml`, `garantivalvet/src/main/res/values-sv/strings.xml` och flytt av uppgiftsfil till `uppgifter/pagar/`; inga filer i repots rot eller otillåtna moduler.
- Alla nio `sakerhetskopia_*`-nycklar från uppgiften finns på engelska och svenska med texter enligt tabellen (t.ex. svenska `sakerhetskopia_spara` = «Spara säkerhetskopia», engelska `sakerhetskopia_hamta` = «Restore from backup»).
- `sakerhetskopia_importerad` har `%1$d` och `%2$d` på båda språken.
- Inga Kotlin- eller manifeständringar; inga hårdkodade UI-strängar utöver `strings.xml`.
- Databasschema oförändrat.
- Inga nya beroenden, behörigheter eller nätverk.
- `StringsNycklarTest` (JUnit 4, `org.junit`) jämför nyckelmängder mellan locales — rätt test för uppgiften; `:garantivalvet:testDebugUnitTest --tests "*StringsNycklarTest"` grönt lokalt; CI success på grenen.

## Rättat av Cursor
- inget

## Frågor till Claude
- inga
