# Uppgift: statuschip i listraden tar inte textens bredd

uppdrag: uppdrag/2026-10-12-skarmbilder-efter-del-b.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopListRad.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopDetaljSkarm.kt

## Uppgift
I `KopListRad` mäts `AssistChip` med statusen före textkolumnen (som har `weight(1f)`). En lång status, till exempel "Reklamationsrätt gäller" eller "Consumer claim period active", tar då nästan hela radens bredd, och titel och butik bryts bokstav för bokstav.

Rätta så här, bara i `KopListRad`:
1. Ge `AssistChip` modifiern `Modifier.widthIn(max = 120.dp)` (import `androidx.compose.foundation.layout.widthIn`).
2. Ge `Text` i chipens `label` `maxLines = 2` och `overflow = TextOverflow.Ellipsis` (import `androidx.compose.ui.text.style.TextOverflow`).
3. Inget annat i filen ändras. Strängarna ligger kvar i `strings.xml`.

## Exempel
- Köp i reklamationsrätt (köpt 2025-08-01, 12 månaders garanti, idag 2026-10-12), skärmbredd 411 dp: chipen är högst 120 dp bred och visar "Reklamationsrätt gäller" på två rader; titeln "Mobiltelefon" och butiken "Telefonbutiken i centrum" visas på var sin rad utan att brytas mitt i ord.
- Köp som gäller ("Gäller" / "In warranty"): chipen ser ut som förut, på en rad.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*ButikSkarmbilder*"
gradle testDebugUnitTest

## Gör inte
- Ändra andra filer, skärmbilderna eller exempeldatan i testerna.
- Lägga till beroenden.
- Ändra databasschemat.
