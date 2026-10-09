# Uppgift: kvittominiatyr i detaljvyn

uppdrag: uppdrag/2026-10-09-kvittofoto.md
försök: 1
beror-på: 2026-10-09-02-kvitto-strangar-f2.md

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopSkarmar.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/MainActivity.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopViewModel.kt

## Uppgift
1. Utöka **`KopDetaljSkelett`** med valfri parameter `kvittoUri: Uri?` och `onOppnaKvitto: () -> Unit`.
2. När `kvittoUri != null`: visa miniatyr (ca 160 dp höjd) med innehållsbeskrivning `kvitto_fornhandsvisning`; klick anropar `onOppnaKvitto`.
3. I **`MainActivity`** på detaljskärmen: `kvittoUri = viewModel.sparatKvittoUri(kop)`, `onOppnaKvitto` navigerar till helskärmsvyn (uppgift 05) eller öppnar en enkel fullskärms-dialog om 05 inte är klar — använd samma callback som helskärmsuppgiften kommer använda.

## Exempel
- Köp med `kvittoFil` satt → miniatyr syns på detaljsidan.
- Köp utan kvitto → ingen miniatyr.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*KopDaoTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.

utfall: underkänd 2026-10-09 av Cursor (PR #18). Ersatt av 2026-10-09-04-detalj-kvitto-miniatur-f2.md (verkställt av Claude, rött bygge stoppade cursor-beslut)
