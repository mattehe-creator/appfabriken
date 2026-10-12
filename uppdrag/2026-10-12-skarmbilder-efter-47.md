# Uppdrag: generera om formulär- och listbilden efter #112 och #114

Läs CLAUDE.md, ARBETSFLODE.md och uppgifter/MALL.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

#113 genererade om skärmbilderna från en gren som utgick från main före #112. Därför visar `en/03-formular.png` fortfarande svenskt köpdatum, fast #112 rättade det i appen. Efter #113 har #114 (uppgift 47) begränsat statuschippen i listraden till högst 120 dp och två rader, så att titel och butik inte trycks ihop. Listbilden visar därför inte den aktuella listraden.

## Uppgift

Del A (Cursor, en PR från aktuell main efter #114): generera om `03-formular.png` och listbilden (`01-lista.png`) i både `garantivalvet/butik/skarmbilder/sv/` och `en/` med Roborazzi från `ButikSkarmbilderTest`. Kontrollera bild för bild: köpdatum i enhetens språk i formuläret (engelskt i `en/`), statuschippen i listan tar inte bredden från titel och butik. Övriga bilder genereras bara om om de ändras av samma körning; då ska de också stämma med main.

Del B: inget, om inte en bild visar ett fel i appen. Hittar du ett sådant fel, rätta det inte själv: beskriv det under "Frågor till Claude" i PR-beskrivningen, med förslag på uppgift för den lokala modellen enligt uppgifter/MALL.md.

## Klart när

- `gradle testDebugUnitTest` och `gradle assembleDebug` är gröna för alla moduler.
- `01-lista.png` och `03-formular.png` på båda språken är omgenererade från main efter #114.
- Grenen innehåller senaste main vid PR:en.

## Gör inte

- Ändra appkod, databasschema, manifest, behörigheter eller beroenden.
- Ändra funktionsgrafiken eller butikstexterna.
