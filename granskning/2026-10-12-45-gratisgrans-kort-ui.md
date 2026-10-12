beslut: underkänd
granskare: cursor
datum: 2026-10-12

## Kontrollerat
- Endast `KopSkarmar.kt`, `MainActivity.kt` och flytt av uppgiftsfil till `pagar/` ändrade; inga filer i repots rot.
- `MainActivity` skickar `arPro` och `onVisaProDialog` korrekt till `KopListaSkarm`.
- Varningskortet använder `skaVisaGratisGransVarning`, `kvittonKvarGratis`, `pro_kvar_gratis` och `pro_las_upp` utan hårdkodade UI-texter; `TextButton` har `heightIn(min = 48.dp)`.
- **Utanför uppgiften:** `KopSkarmar.kt` byggde på en äldre version utan grupperad lista (`ListaGrupp`, `listaGrupp`, rubriker). Hela grupp-logiken på main är borttagen och ersatt med platt `items(kopLista)`. `TomListaInnehall` är borttagen och tom-lista-UI är omskrivet (ikon, typografi, padding) — inte begärt.
- **Bygge:** CI fail — `ButikSkarmbilderTest` kompilerar inte eftersom `arPro` och `onVisaProDialog` saknar standardvärden och testfilen får inte ändras i denna uppgift.
- Inga nya beroenden, behörigheter eller nätverk i diffen.
- `GratisGransTest` påverkas inte av kompileringsfelet; beteendet för kortet testas inte i enhetstest (acceptabelt för UI-uppgiften om bygget går igenom).

## Rättat av Cursor
- inget (regression och kompileringsfel är för stora för försök 1)

## Vid underkänd: försök 2

```
# Uppgift: kort överst i listan när gratisgränsen närmar sig

uppdrag: uppdrag/2026-10-12-ny-design.md
försök: 2
beror-på: 2026-10-12-38-gratisgrans-kort.md

## Vad som saknades
- Utgå från **nuvarande main** av `KopSkarmar.kt`: behåll grupperad lista (`LocalDate`, `listaGrupp`, `ListaGrupp`, rubriker per grupp) och hela `TomListaInnehall` oförändrad.
- Lägg **bara** till varningskortet och de två nya parametrarna; rör inte tom-lista-layouten och ta inte bort grupp-raderna.
- Nya parametrar måste ha standardvärden så att befintliga anrop (t.ex. `ButikSkarmbilderTest`) kompilerar utan att ändra testfiler: `arPro: Boolean = false`, `onVisaProDialog: () -> Unit = {}`.
- Bygget ska vara grönt: `compileDebugUnitTestKotlin` får inte faila på saknade parametrar.

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopSkarmar.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/MainActivity.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/GratisGrans.kt
- garantivalvet/src/main/res/values/strings.xml (strängen `pro_kvar_gratis` finns redan)

## Uppgift
1. `KopListaSkarm` i `KopSkarmar.kt` får två nya parametrar: `arPro: Boolean = false` och `onVisaProDialog: () -> Unit = {}`.
2. När listan inte är tom och `skaVisaGratisGransVarning(kopLista.size, arPro)` är true: lägg ett `Card` som **första** `item` i `LazyColumn`, **före** grupp-rubrikerna och köpen. Kortet visar `stringResource(R.string.pro_kvar_gratis, kvittonKvarGratis(kopLista.size))` och en `TextButton` med den befintliga strängen `pro_las_upp` som anropar `onVisaProDialog`. Tryckytan minst 48 dp.
3. I `MainActivity.kt`, där `KopListaSkarm(` anropas: skicka `arPro = arPro` och `onVisaProDialog = viewModel::visaProDialog` (båda finns redan i samma block).

Inga nya strängar och inga hårdkodade texter eller färger. Ta bort oanvända imports (t.ex. `itemsIndexed` om den inte behövs).

## Exempel
- 12 köp, inte Pro → kortet "3 kvitton kvar i gratisversionen" överst (ovanför grupp-rubriker).
- 11 köp → inget kort.
- 13 köp, Pro → inget kort.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*GratisGransTest"
gradle :garantivalvet:testDebugUnitTest --tests "*ButikSkarmbilder*"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
- Ta bort eller förenkla grupperad lista eller `TomListaInnehall`.
```

## Frågor till Claude
- inga
