# Uppgift: kvittoikon i listraden

uppdrag: uppdrag/2026-10-09-kvittofoto.md
försök: 2
beror-på: 2026-10-09-02-kvitto-strangar-f2.md

## Vad som saknades
- Ingen ikon i listraden; bara imports i fel fil (`KopSkarmar.kt`) som inte kompilerade (`Receipt` finns inte utan `material-icons-extended`).
- Listraden är `KopListRad` i `KopListRad.kt`, inte `KopRadSkelett` i `KopSkarmar.kt`.

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopListRad.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/data/Kop.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopSkarmar.kt

## Uppgift
1. I **`KopListRad`**: på titelraden (`kop.vad`), när `kop.kvittoFil != null`, visa en liten ikon bredvid titeln (t.ex. `Icons.Filled.Check` — den finns i standardpaketet utan nytt beroende; `Receipt`/`Description` kräver `material-icons-extended` som uppgiften förbjuder).
2. Använd `Icon(...)` med `contentDescription = stringResource(R.string.kvitto_har_kvitto)`.
3. Lägg titel + ikon i en `Row` med `verticalAlignment = Alignment.CenterVertically`; ikonen ska inte ha egen `clickable` — hela raden behåller befintlig `clickable` på `Column`.
4. När `kop.kvittoFil == null`: ingen ikon, titeln som idag.

## Exempel
- Köp med `kvittoFil = "abc.jpg"` → liten ikon syns bredvid titeln i listan.
- Köp med `kvittoFil = null` → ingen ikon, bara titeln.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*KopSorteringTest"

## Gör inte
- Ändra andra filer (inklusive `KopSkarmar.kt` och `build.gradle.kts`).
- Lägga till beroenden.
- Ändra databasschemat.
