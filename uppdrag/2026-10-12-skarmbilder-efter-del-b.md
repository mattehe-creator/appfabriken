# Uppdrag: generera om skärmbilderna efter ny design del B

Läs CLAUDE.md, ARBETSFLODE.md och uppgifter/MALL.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Uppdraget `2026-10-12-ny-design` kräver under "Klart när" att skärmbilderna i `garantivalvet/butik/skarmbilder/` visar den nya designen. De genererades senast i #99 (del A). Efter det har den lokala modellen ändrat utseendet i uppgift 38–46: grupperad lista (#104), tidslinje på detaljsidan (#105, #108), "Läs mer" om reklamationsrätten (#109), garantitid som chips och pris/datum i formuläret (#102, #103, #112), gratisgränskortet i listan (#111). Bilderna i `sv/` och `en/` är därför inaktuella.

## Uppgift

Del A (Cursor, en PR): generera om alla fem skärmbilderna i `garantivalvet/butik/skarmbilder/sv/` och `en/` med Roborazzi från `ButikSkarmbilderTest`. Kontrollera bild för bild att de visar den aktuella designen: grupprubriker i listan, tidslinjen på detaljsidan, chips och datum i enhetens språk i formuläret, engelska datum och engelsk exempeldata i `en/`. Ändra exempeldatan i testet bara om en bild annars blir missvisande (till exempel tom tidslinje). Gratisgränskortet ska inte synas i listbilden.

Del B: inget, om inte en bild visar ett fel i appen. Hittar du ett sådant fel, rätta det inte själv: beskriv det under "Frågor till Claude" i PR-beskrivningen, med förslag på uppgift för den lokala modellen enligt uppgifter/MALL.md.

## Klart när

- `gradle testDebugUnitTest` och `gradle assembleDebug` är gröna för alla moduler.
- Alla tio bilderna är omgenererade och visar designen på main efter #112.
- Butikstexterna i `garantivalvet/butik/` stämmer fortfarande med bilderna; annars föreslå ändringen under "Frågor till Claude".

## Gör inte

- Ändra appkod, databasschema, manifest, behörigheter eller beroenden.
- Ändra funktionsgrafiken om den inte påverkas.
