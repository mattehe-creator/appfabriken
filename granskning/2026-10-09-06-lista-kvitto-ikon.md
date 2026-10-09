beslut: underkänd
granskare: cursor
datum: 2026-10-09

## Kontrollerat
- Endast `KopSkarmar.kt` och flytt av uppgiftsfil till `pagar/` ändrades; inga filer i repots rot.
- Uppgiftens beteende är inte implementerat: inga ändringar i listraden, bara oanvända imports (`Icon`, `Icons.Outlined.Receipt`) som gav kompileringsfel (`Unresolved reference 'Receipt'` — ikonen finns inte utan `material-icons-extended`, som uppgiften förbjuder).
- Listraden ligger i `KopListRad.kt` (ersatte `KopRadSkelett` i tidigare uppgift), inte i `KopSkarmar.kt`; fel fil enligt uppgiftens avsikt.
- Inga nya Gradle-beroenden, behörigheter eller nätverk.
- `KopSorteringTest` (JUnit 4) körs enligt uppgiften men testar bara sortering, inte kvittoikon; det räcker som angivet test men verifierar inte ikonen.
- Inga hårdkodade UI-strängar tillagda (ingen UI-kod tillagd).

## Rättat av Cursor
- Tog bort de trasiga, oanvända ikon-imports i `KopSkarmar.kt` så grenen kompilerar och `KopSorteringTest` är grönt igen.

## Vid underkänd: försök 2

```
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
```

## Frågor till Claude
- Ska `androidx.compose.material:material-icons-extended` läggas till i `:garantivalvet` (Cursor-uppgift) så att `Icons.Outlined.Receipt` går att använda enligt uppdragets förslag, eller räcker en kärnikon tills vidare?
