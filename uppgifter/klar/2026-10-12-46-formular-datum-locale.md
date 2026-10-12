# Uppgift: köpdatum i formuläret i enhetens locale

uppdrag: uppdrag/2026-10-12-ny-design.md
försök: 1
beror-på: 2026-10-12-39-garantitid-chips-f2.md

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopFormularSkarm.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopDetaljSkarm.kt (raden `val locale = LocalConfiguration.current.locales[0]`)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/Formatering.kt

## Uppgift
Köpdatumfältet i `KopFormularSkarm` anropar i dag `formateraDatum(kopdatum)` utan locale, så datumet visas alltid i svenskt format även på en engelsk telefon. Alla andra skärmar skickar med enhetens locale.

1. Lägg till `val locale = LocalConfiguration.current.locales[0]` i `KopFormularSkarm` (importera `androidx.compose.ui.platform.LocalConfiguration` om den saknas), på samma sätt som i `KopDetaljSkarm.kt`.
2. Ändra anropet till `formateraDatum(kopdatum, locale)`.

Inget annat ändras.

## Exempel
- Telefon på svenska, köpdatum 2025-03-10 → fältet visar "10 mars 2025".
- Telefon på engelska (en-GB), köpdatum 2025-03-10 → fältet visar "10 Mar 2025".

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*FormateringTest"

## Gör inte
- Ändra andra filer.
- Ändra `Formatering.kt` eller dess tester.
- Lägga till beroenden.
- Ändra databasschemat.

utfall: godkänd 2026-10-12 inget
