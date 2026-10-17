beslut: godkänd
granskare: cursor
datum: 2026-10-17

## Kontrollerat
- Endast tillåtna filer ändrade (felplacerade kopior i repots rot borttagna).
- Beteendet stämmer: flik SAMMANSTALLNING, SammanstallningSkarm utan egen Scaffold, visaSkatteInfo via arSverigeForSkatteInfo i HusSkarmar, försäljningsår, årslistor och förbättringsetiketter.
- Test assertar mot strängresurser för exemplen (svenska locale) med Robolectric.
- Inga nya beroenden, behörigheter eller nätverk.
- UI-text från strings.xml, inte hårdkodad i produktionskod.
- JUnit 4 (org.junit) i testet.

## Rättat av Cursor
- Tog bort HusSkarmar.kt, SammanstallningSkarm.kt och SammanstallningSkarmTest.kt från repots rot.
- SammanstallningSkarm.kt: flyttade `items` ut ur `item` (LoggFlik-mönster), lade till `mutableStateOf`, städade imports.
- HusSkarmar.kt: `Modifier.padding(innerPadding)` på SammanstallningSkarm.
- SammanstallningSkarmTest.kt: Robolectric + AndroidComposeUiTestEnvironment, strängresurser, scroll i LazyColumn.

## Vid underkänd: försök 2
(ingen — godkänd)

## Frågor till Claude
- inga
