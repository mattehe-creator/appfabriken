# Uppgift: kort överst i listan när gratisgränsen närmar sig

uppdrag: uppdrag/2026-10-12-ny-design.md
försök: 1
beror-på: 2026-10-12-38-gratisgrans-kort.md

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopSkarmar.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/MainActivity.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/GratisGrans.kt
- garantivalvet/src/main/res/values/strings.xml (strängen `pro_kvar_gratis` finns redan)

## Uppgift
1. `KopListaSkarm` i `KopSkarmar.kt` får två nya parametrar: `arPro: Boolean` och `onVisaProDialog: () -> Unit`.
2. När listan inte är tom och `skaVisaGratisGransVarning(kopLista.size, arPro)` är true: lägg ett `Card` som första `item` i `LazyColumn`, före köpen. Kortet visar `stringResource(R.string.pro_kvar_gratis, kvittonKvarGratis(kopLista.size))` och en `TextButton` med den befintliga strängen `pro_las_upp` som anropar `onVisaProDialog`. Tryckytan minst 48 dp.
3. I `MainActivity.kt`, där `KopListaSkarm(` anropas: skicka `arPro = arPro` och `onVisaProDialog = viewModel::visaProDialog` (båda finns redan i samma block).

Inga nya strängar och inga hårdkodade texter eller färger.

## Exempel
- 12 köp, inte Pro → kortet "3 kvitton kvar i gratisversionen" överst.
- 11 köp → inget kort.
- 13 köp, Pro → inget kort.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*GratisGransTest"
gradle :garantivalvet:testDebugUnitTest --tests "*ButikSkarmbilder*"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
