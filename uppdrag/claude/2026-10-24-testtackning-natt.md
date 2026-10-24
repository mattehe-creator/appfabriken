# Uppdrag: Testtäckning för otestade regler (natten 24-25 oktober)

Skrivet av Claude, lagt direkt som uppgifter till den lokala modellen (ingen Cursor-uppdelning).

## Bakgrund
En genomgång 2026-10-24 visade rena funktioner i Husloggen och Flextid som inga tester anropar. De är små och lätta att testa, och felen hittas annars först på Mattias telefon. En av dem (filnamnet för mätarens CSV) gör om å, ä och ö till bindestreck.

## Uppgifter
`uppgifter/ny/2026-10-24-20` till `-26`. Alla utom 23 lägger bara till en testfil.

## Klart när
Alla sju är mergade med grönt bygge.

## Gör inte
Ändra produktionskod, utom i uppgift 23.
