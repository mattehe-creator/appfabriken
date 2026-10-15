# Uppgift: föreslå garantitid i formuläret när kvittot anger den

uppdrag: ingen (Mattias önskemål 2026-10-14)
försök: 1
beror-på: 2026-10-14-02-garanti-i-kvittoforslag-f2.md

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopFormularSkarm.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/GarantitidVal.kt (`CHIP_MANADER` är 12, 24, 36; chip-index 3 är "Annat")
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/KvittoTolkare.kt (`KvittoForslag.garantiManader: Int?`, finns efter föregående uppgift)

## Uppgift
Efter att kvittot lästs av i `KopFormularSkarm` (där förslagen från `lasKvitto` läggs in i fälten, se `if (forslag != null) { ... }` i `LaunchedEffect(kvittoUri, lasIgen)`) ska garantitiden också föreslås.

Beteende:
1. Nytt tillstånd: `var garantiAndrad by remember { mutableStateOf(false) }` och `var forslagGaranti by remember { mutableStateOf(false) }`, bredvid `forslagOppetKop`.
2. `garantiAndrad` sätts till `true` när användaren själv trycker på en av garantichipsen (`onClick` på de fyra `FilterChip` för 12, 24, 36 och Annat) eller skriver i fältet "Annat" (`onValueChange` för `annatText`). Samma ställen sätter `forslagGaranti = false`.
3. I blocket som tar emot förslaget: om `forslag.garantiManader` inte är `null` och `garantiAndrad` är `false` sätts garantivalet:
   - är värdet ett av `GarantitidVal.CHIP_MANADER` sätts `valdChipIndex` till dess index och `annatText = ""`,
   - annars sätts `valdChipIndex = 3` och `annatText = värdet.toString()`,
   - och `forslagGaranti = true`.
4. När `forslagGaranti` är sant visas en liten text direkt under raden med garantichips och fältet "Annat": `Text` med `MaterialTheme.typography.bodySmall` och färgen `MaterialTheme.colorScheme.onSurfaceVariant`, med den befintliga strängen `R.string.kvitto_forslag` (finns redan, "Förslag från kvittot – kontrollera").
5. Ett val som användaren själv gjort ändras aldrig (`garantiAndrad` är `true`), och ett köp utan garantitid på kvittot ändrar ingenting.
6. Inga nya strängar, inga andra filer.

## Exempel
- Nytt köp, kvittot anger "3 års garanti": chipen "36 månader" blir vald och texten "Förslag från kvittot – kontrollera" visas.
- Kvittot anger "Garanti 18 mån": "Annat" blir valt med 18 i fältet, texten visas.
- Användaren har själv valt 12 månader innan kvittot lästs: valet ligger kvar och ingen text visas.
- Kvittot anger ingen garanti: standardvalet (24) ligger kvar, ingen text.
- Användaren trycker "Läs av igen" efter att ha ändrat garantin själv: valet ändras inte.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*ButikSkarmbilder*"
gradle :garantivalvet:testDebugUnitTest --tests "*FormularValidering*"
gradle testDebugUnitTest

## Gör inte
- Ändra andra filer, strängfiler eller skärmbilderna i `butik/`.
- Lägga till beroenden.
- Ändra databasschemat.
- Ändra `onSpara` eller andra funktioners signaturer.

utfall: godkänd 2026-10-15 (flytt av valdChipIndex/annatText före LaunchedEffect)
