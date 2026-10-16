# Uppgift: ikon i logglistan när post har bilder

uppdrag: uppdrag/2026-10-16-husloggen-bilder.md
försök: 1
beror-på: 2026-10-16-17-bild-filnamnslista.md, 2026-10-16-20-bild-strangar.md

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/HusListRader.kt
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/BildTolkningTest.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopListRad.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/BildTolkning.kt

## Uppgift
I **`PostListRad`**: visa en liten bild-/bilaga-ikon när `tolkaBilder(post.bilder)` inte är tom. Ikonen ska ha innehållsbeskrivning `bild_har_bilder`. Raden ska fortfarande ha minst 48 dp tryckyta.

Lägg ett enhetstest i **`BildTolkningTest`** (eller utöka befintligt) som verifierar att en postrad skulle räknas som «har bilder» när strängen innehåller minst ett filnamn (ren logik, ingen Compose-test).

## Exempel
- Post med `bilder = "a.jpg"` → ikon syns.
- Post med `bilder = null` → ingen ikon.

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*BildTolkningTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
