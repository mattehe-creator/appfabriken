# Uppgift: gratisgräns vid nytt köp

uppdrag: uppdrag/2026-10-11-pro-gratisgrans.md
försök: 1
beror-på: 2026-10-11-18-pro-strangar.md

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopViewModel.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopSkarmar.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/MainActivity.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/GratisGrans.kt
- se.tmconnect.karna.pro.ProKalla

## Uppgift
När användaren trycker på FAB «Lägg till» i listan:

1. Beräkna `kanLaggaTill(kopLista.size, arPro)` där `arPro` kommer från `KopViewModel` (`proKalla.arPro`, redan kopplat av Cursor).
2. Om `true`: anropa befintlig `visaLaggTill()` som idag.
3. Om `false`: sätt state så att **Pro-dialogen** öppnas (uppgift 20) i stället för formulärskärmen — lägg till t.ex. `proDialogSynlig: StateFlow<Boolean>` och `fun visaProDialog()` / `fun stangProDialog()` i ViewModel.

Befintliga köp ska alltid gå att öppna, ändra och ta bort via listan/detalj, även om antalet överstiger 15 (t.ex. efter återställning). Begränsa **bara** vägen till nytt köp via FAB.

Skicka `arPro`, `proDialogSynlig`, callbacks och `onKopPro` (Activity) från `MainActivity` till `KopListaSkarm` / dialog enligt uppgift 20.

## Exempel
- 14 köp, inte Pro → FAB öppnar formulär.
- 15 köp, inte Pro → FAB öppnar Pro-dialog, inte formulär.
- 15 köp, Pro → FAB öppnar formulär.
- 20 köp, inte Pro → användaren kan fortfarande öppna och redigera köp 1–20 från listan.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*GratisGransTest" --tests "*StringsNycklarTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Blockera visning, ändring eller borttag av befintliga köp.
