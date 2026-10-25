# Uppgift: Bostadshanterare på Översikt

uppdrag: uppdrag/2026-10-25-husloggen-flera-bostader.md
försök: 1
beror-på: cursor:2026-10-25-husloggen-flera-bostader

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/HusSkarmar.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/BostadSkarmar.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/MainActivity.kt

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/SammanstallningSkarm.kt

## Uppgift
Flytta bostadshanteringen från inställningar till **Översikt**-fliken (`HusFlik.SAMMANSTALLNING`).

1. Lägg `BostaderInstallningSektion` (eller en ny composable `BostadOversiktSektion` med samma beteende) högst upp i innehållet på Översikt, ovanför sammanställningen.
2. Ta bort bostadssektionen från `InstallningarSkarm` / Mer-fliken så bostäder bara hanteras på Översikt.
3. Behåll `BostadValjareRad` i appbaren när det finns fler än en bostad (snabb byte), men full hantering (lägg till, byt namn, välj aktiv) ska finnas på Översikt.
4. Tråda samma callbacks som idag från `MainActivity` till `HusStartSkarm` och vidare till Översikt-fliken.

Bostadsformuläret ska behålla fälten **namn**, **adress** och **anteckning** (`BostadFormularFalt`).

## Exempel
- Användaren öppnar Översikt: en sektion "Bostäder" med radioknappar, knappen Lägg till och Ändra syns.
- Användaren öppnar Inställningar: ingen bostadslista längre.

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*HusNavigeringTest*"

## Gör inte
- Ändra databasschemat eller `HusRepository`.
- Lägga till beroenden.
- Ändra andra filer.

utfall: underkänd 2026-10-25 av Cursor (PR #700). Ersatt av 2026-10-25-09-bostad-pa-oversikt-f2.md
