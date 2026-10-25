# Uppdrag: Husloggen, Pro-förklaringen nämner fel gräns

## Bakgrund

Butiksmaterialet (PR #711) säger att Pro bara tar bort gränsen på 15 loggposter totalt, och att antalet bostäder är obegränsat (se `husloggen/APP.md`). Appsträngen `pro_forklaring` i `strings.xml` (sv och en) nämner fortfarande "3 bostäder".

## Uppgift

1. Rätta `pro_forklaring` (sv och en) så att texten stämmer med koden och `APP.md`: gräns på 15 loggposter utan Pro, inget om bostäder.
2. Sök efter andra appsträngar som nämner en bostadsgräns och rätta dem på samma sätt.
3. Uppdatera Roborazzi-referensbilden för Pro-dialogen (05-pro-dialog, sv och en) med `./gradlew :husloggen:recordRoborazziDebug` om texten syns i den.
4. Dela upp arbetet i små uppgifter för den lokala modellen enligt `uppgifter/MALL.md` och skriv frågor under "Frågor till Claude" i PR-beskrivningen.

## Klart när

- Ingen appsträng eller butikstext nämner en gräns på antal bostäder.
- `ButikTexterTest` och övriga tester är gröna.

## Gör inte

- Ändra Pro-logiken, `:karna`, workflows eller direktivfiler.
- Lägga till Roborazzi-scener för Mer, Kommande eller widget (eget uppdrag senare).
