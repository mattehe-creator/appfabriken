# Uppgift: kvittoikon i listraden

uppdrag: uppdrag/2026-10-09-kvittofoto.md
försök: 1
beror-på: 2026-10-09-02-kvitto-strangar-f2.md

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopSkarmar.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/data/Kop.kt

## Uppgift
1. I **`KopRadSkelett`** (eller motsvarande listrad): när `kop.kvittoFil != null`, visa en liten ikon (t.ex. Material `Icons.Outlined.Receipt` eller `Description`) bredvid titeln.
2. Sätt `contentDescription` till `stringResource(R.string.kvitto_har_kvitto)`.
3. Ikonen ska inte vara klickbar separat; hela raden öppnar fortfarande detaljen.

## Exempel
- Köp med kvitto → ikon syns i listan.
- Köp utan kvitto → ingen ikon.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*KopSorteringTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.

utfall: underkänd 2026-10-09 av Cursor (PR #19). Ersatt av 2026-10-09-06-lista-kvitto-ikon-f2.md
