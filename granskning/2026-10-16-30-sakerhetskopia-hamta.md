beslut: godkänd
granskare: cursor
datum: 2026-10-16

## Kontrollerat
- Den lokala modellen ändrade inte rätt filer: hela `MainActivity.kt` och `HusViewModel.kt` lades i repots rot; appkoden på `husloggen/…` var oförändrad. Grenen innehöll också ändringar utanför uppgiften (`BESLUTSLOGG.md`, `uppdrag/2026-10-17-korskript-gradle-daemon-hang.md`).
- Implementationen i rotfilerna mappade fel till snackbar (nyare version och importresultat), använde fel fältnamn (`kvittoFiler`, `SaknarKopJson`) och kopplade inte `onHamtaSakerhetskopia`.
- Efter Cursor-rättning: `OpenDocument` med zip/octet-stream, `lasInSakerhetskopia`, meddelanden via `strings.xml`, ingen delvis import vid fel.
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Inga hårdkodade UI-strängar; databasschema oförändrat.
- `gradle testDebugUnitTest` grönt lokalt efter rättning.

## Rättat av Cursor
- Flyttade bort felplacerade rotfiler; implementerade uppgiften i `husloggen/src/main/kotlin/se/tmconnect/husloggen/MainActivity.kt` och `…/ui/HusViewModel.kt` enligt Garantivalvet-förebilden (rätt felmeddelanden, importräknare, MIME-typer).
- Tog bort grenens ändringar utanför uppgiften (`BESLUTSLOGG.md`, uppdrag om körskript).
- Kompileringsfel: `stringResource` i `LaunchedEffect` ersatt med `getString` för formaterad importtext.

## Vid underkänd: försök 2

## Frågor till Claude
- inga
