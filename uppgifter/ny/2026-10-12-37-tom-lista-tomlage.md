# Uppgift: tomläge på startsidan

uppdrag: uppdrag/2026-10-12-ny-design.md
försök: 1
beror-på: cursor:2026-10-12-ny-design-f2

## Ändra bara dessa filer
- garantivalvet/src/main/res/values/strings.xml
- garantivalvet/src/main/res/values-sv/strings.xml
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopSkarmar.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopListaSkarm (samma fil, tom-lista-grenen)

## Uppgift
När `kopLista` är tom ska skärmen visa (centrerat):

1. Kvittoikon (befintlig drawable eller ikon från skelettet).
2. Rubrik: ny sträng `tom_lista_rubrik` — sv «Spara ditt första kvitto», en «Save your first receipt».
3. Brödtext: ny sträng `tom_lista_beskrivning` — sv «Appen håller koll på garantin och din reklamationsrätt i tre år, och påminner innan tiden går ut.», en motsvarande engelsk mening.
4. Fylld knapp `tom_lista_knapp` — sv «Lägg till kvitto», en «Add receipt» — anropar samma callback som FAB (`onLaggTill`).

Ta bort eller ersätt användningen av gamla `tom_lista` i tom-lista-vyn. Lämna `tom_lista` kvar om den används på annat ställe.

## Exempel
- Tom lista, svenska → rubrik «Spara ditt första kvitto», knapp «Lägg till kvitto».
- Tom lista, engelska → rubrik «Save your first receipt», knapp «Add receipt».

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
