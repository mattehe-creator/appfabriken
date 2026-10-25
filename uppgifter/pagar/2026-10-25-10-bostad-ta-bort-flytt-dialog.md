# Uppgift: Bekräftelse vid borttagning av bostad (flytt eller radera)

uppdrag: uppdrag/2026-10-25-husloggen-flera-bostader.md
försök: 1
beror-på: 2026-10-25-09-bostad-pa-oversikt-f2.md

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/BostadSkarmar.kt
- husloggen/src/main/res/values/strings.xml
- husloggen/src/main/res/values-sv/strings.xml

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/HusViewModel.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/data/HusRepository.kt

## Uppgift
När användaren tar bort en bostad som har kopplade poster (antal från `hamtaRaderingInfo` > 0), visa en dialog som kräver val:

1. **Flytta till annan bostad** – lista övriga bostäder (radioknappar), anropa `onTaBortBostad` med `BostadBorttagningSatt.FLYTTA_TILL` och vald `flyttTillId`.
2. **Radera allt innehåll** – tydlig varning, anropa med `BostadBorttagningSatt.RADERA_INNEHALL`.

Om antalet kopplade poster är 0, behåll en enkel bekräftelse som idag.

Utöka callback-typen `onTaBortBostad` i `BostaderInstallningSektion` så den tar `(Bostad, BostadBorttagningSatt, flyttTillId: Long?, onSista, onBorttagen)` och uppdatera `MainActivity` så den anropar `viewModel.taBortBostad` direkt (ersätt nuvarande wrapper som alltid raderar).

Lägg nya strängar på svenska och engelska, till exempel rubriker för flytt respektive radera och en fråga som visar antal poster.

## Exempel
- Bostad "Stuga" har 3 kopplade rader, bostad "Mitt hem" finns: användaren väljer flytt till Mitt hem → Stugan försvinner, posterna har `bostad_id` för Mitt hem.
- Samma setup, användaren väljer radera → posterna försvinner.

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*HusRepositoryBostadTest.taBortBostad*"

## Gör inte
- Ändra repository eller migrationer.
- Lägga till beroenden.
- Ändra andra filer.
