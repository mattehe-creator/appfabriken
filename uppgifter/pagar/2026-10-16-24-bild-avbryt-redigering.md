# Uppgift: avbryt vid redigering får inte radera sparade bilder

uppdrag: uppdrag/2026-10-16-husloggen-bilder.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/HusViewModel.kt
- husloggen/src/test/kotlin/se/tmconnect/husloggen/ui/HusViewModelBilderTest.kt (ny)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/data/HusRepository.kt (`laggTillBild`, `taBortBild`)

## Uppgift
I redigeringsläge (`redigerarId != 0`) skriver `laggTillBildUri` och `taBortBildFranFormular` direkt till databasen via repository. `stangPostFormular` anropar ändå `rensaOsparadeBilder`, som raderar filer som lades till under sessionen. Då pekar `post.bilder` på filer som inte finns när användaren avbryter.

Rätta så att `rensaOsparadeBilder` bara raderar filer i nyskapa-läge (`redigerarId == 0`). I redigeringsläge är ändringarna redan sparade och ska ligga kvar.

## Exempel
- Redigera post med 1 bild, lägg till en andra, avbryt → båda filerna finns kvar och `post.bilder` har båda.
- Ny post, lägg till en bild, avbryt → filen raderas.
- Ny post, lägg till en bild, spara → filen finns kvar.

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*HusViewModelBilderTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
