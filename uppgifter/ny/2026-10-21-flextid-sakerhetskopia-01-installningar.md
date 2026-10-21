# Uppgift: Nya inställningar i säkerhetskopian

uppdrag: uppdrag/claude/2026-10-21-flextid-helgdagar.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/export/SakerhetskopiaModell.kt
- flextid/src/main/kotlin/se/tmconnect/flextid/export/SakerhetskopiaJson.kt
- flextid/src/test/kotlin/se/tmconnect/flextid/export/SakerhetskopiaInstallningarTest.kt (ny)

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/data/UiInstallningarLager.kt

## Uppgift
Säkerhetskopian ska ta med de tre inställningar som finns i `UiInstallningarLager`: aftnar lediga, semesterdagar per år och semesterårets startmånad.

1. Lägg till tre fält sist i `SakerhetskopiaInstallning`, med standardvärden:
   `val aftnarLediga: Boolean = true`, `val semesterdagarPerAr: Int = 25`, `val semesterarStartmanad: Int = 4`.
2. I `skrivInstallningObjekt` (SakerhetskopiaJson.kt): skriv dem efter `stamplaInPaminnelseMinEfterMidnatt` som `"aftnarLediga":true`, `"semesterdagarPerAr":25`, `"semesterarStartmanad":4`.
3. I `lasInstallning`: läs dem. Saknas ett fält (äldre säkerhetskopia) används standardvärdet. `semesterdagarPerAr` begränsas till 0–60. `semesterarStartmanad` blir 1 om värdet är 1, annars 4. Läs ett booleskt värde med samma objekt-API som filen redan använder; finns ingen funktion för booleskt värde, jämför den råa texten med `true`.

## Exempel
- Skriv och läs tillbaka `aftnarLediga = false, semesterdagarPerAr = 30, semesterarStartmanad = 1` → samma värden.
- JSON utan de tre fälten → `true`, `25`, `4`.
- `"semesterdagarPerAr":500` → 60. `"semesterarStartmanad":7` → 4.

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*Sakerhetskopia*"

## Gör inte
- Ändra versionsnumret `FLEXTID_SAKERHETSKOPIA_VERSION` (fälten är valfria och bakåtkompatibla).
- Ändra andra filer (Claude kopplar in export och import).
- Lägga till beroenden.
