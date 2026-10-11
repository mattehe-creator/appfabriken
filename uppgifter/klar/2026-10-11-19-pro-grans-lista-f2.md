# Uppgift: gratisgräns vid nytt köp

uppdrag: uppdrag/2026-10-11-pro-gratisgrans.md
försök: 2
beror-på: 2026-10-11-18-pro-strangar.md

## Vad som saknades
- Listans sortering får inte ändras: behåll `sorteraEfterNarmasteFrist(lista, idag)` i `KopViewModel.kopLista` (återställ import av `sorteraEfterNarmasteFrist`).
- Gräns för nytt köp ska bara styras via `kanLaggaTill` / `kanLaggaTillNy()` — ta bort hårdkodad `15` och dubbel kontroll i `KopListaSkarm`; FAB ska anropa `onLaggTill` direkt som tidigare.
- Implementera inte `AlertDialog` i denna uppgift (det är uppgift 20). Använd befintliga strängnycklar (`pro_las_upp`, `pro_forklaring`, `pro_grans_nadd`, `pro_inte_nu`) först där.
- Bygget måste kompilera (inga `LocalContext.current` inuti icke-composable-lambdas).

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopViewModel.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopSkarmar.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/MainActivity.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/GratisGrans.kt
- se.tmconnect.karna.pro.ProKalla

## Uppgift
När användaren trycker på FAB «Lägg till» i listan:

1. I `MainActivity`, i callback till `KopListaSkarm`: beräkna via `viewModel.kanLaggaTillNy()` (som anropar `kanLaggaTill(kopLista.size, arPro)` med `arPro` från ViewModel).
2. Om `true`: anropa `viewModel.visaLaggTill()` som idag.
3. Om `false`: anropa `viewModel.visaProDialog()` — sätter `proDialogSynlig` till true. Själva dialog-UI läggs i uppgift 20.

Lägg till i `KopViewModel`:
- `proDialogSynlig: StateFlow<Boolean>`
- `fun visaProDialog()` / `fun stangProDialog()`
- `fun kanLaggaTillNy(): Boolean`

Nollställ `proDialogSynlig` när användaren navigerar bort från listan (samma mönster som övriga skärmbyten i ViewModel).

Utöka signaturen på `KopListaSkarm` med parametrar som uppgift 20 behöver (skicka från `MainActivity`): `arPro`, `proDialogSynlig`, `onVisaProDialog`, `onStangProDialog`, `onKopPro: (Activity) -> Unit`. Parametrarna behöver inte användas i composable-kroppen i försök 2 om dialogen kommer i nästa uppgift — men signaturen ska finnas och `MainActivity` ska koppla `viewModel::kopPro`.

`KopListaSkarm`: FAB `onClick = onLaggTill` utan egen gränslogik.

Befintliga köp ska alltid gå att öppna, ändra och ta bort via listan/detalj, även om antalet överstiger 15. Begränsa **bara** vägen till nytt köp via FAB.

## Exempel
- 14 köp, inte Pro → FAB öppnar formulär.
- 15 köp, inte Pro → FAB sätter `proDialogSynlig` true (dialog visas när uppgift 20 är mergad); formulär öppnas inte.
- 15 köp, Pro → FAB öppnar formulär.
- 20 köp, inte Pro → användaren kan fortfarande öppna och redigera köp 1–20 från listan.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*GratisGransTest" --tests "*StringsNycklarTest"

## Gör inte
- Ändra andra filer.
- Ändra listans sortering eller andra ViewModel-beteenden utöver det som står ovan.
- Lägga till `AlertDialog` eller nya strängnycklar (uppgift 20).
- Lägga till beroenden.
- Blockera visning, ändring eller borttag av befintliga köp.

utfall: godkänd 2026-10-11 (import av kanLaggaTill i KopViewModel)
