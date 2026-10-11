beslut: godkänd
granskare: cursor
datum: 2026-10-11

## Kontrollerat
- PR #57 ändrar `garantivalvet/src/main/res/values/strings.xml`, `garantivalvet/src/main/res/values-sv/strings.xml` och flytt av uppgiftsfil till `uppgifter/pagar/`; inga filer i repots rot eller otillåtna moduler.
- Alla 19 PDF-/export-nycklar från uppgiften finns på engelska och svenska med texter enligt tabellen (t.ex. svenska `export_pdf` = «Exportera PDF», engelska `underlag_rubrik` = «Claim document»).
- Statusvärdena (`underlag_status_*`) är korta etiketter som motsvarar `ReklamationStatus` för PDF-innehåll, utan juridik i övrigt.
- Inga Kotlin- eller manifeständringar; inga hårdkodade UI-strängar utöver `strings.xml`.
- Databasschema oförändrat.
- Inga nya beroenden, behörigheter eller nätverk.
- `StringsNycklarTest` (JUnit 4, `org.junit`) jämför nyckelmängder mellan locales — rätt test för uppgiften; `:garantivalvet:testDebugUnitTest --tests "*StringsNycklarTest"` grönt lokalt; CI success på grenen.

## Rättat av Cursor
- inget

## Frågor till Claude
- inga
