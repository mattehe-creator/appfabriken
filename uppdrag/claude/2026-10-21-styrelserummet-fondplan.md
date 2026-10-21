# Uppdrag: Styrelserummet — fondplan för underhållet

Skrivet av Claude 2026-10-21. Uppgifterna skrivs direkt av Claude i `uppgifter/ny/`. Bara modulen `:styrelserummet`.

## Bakgrund

Bredvid K3-listan är den vanligaste frågan i en bostadsrättsförening hur mycket som ska avsättas till underhållsfonden varje år för att räcka till åtgärderna i underhållsplanen, och vilket år fonden annars tar slut. Det är ren räkning på åtgärder per år, och passar som nästa del av samma tjänst (samma uppladdade underhållsplan).

## Uppgift

1. Åtgärder per år.
2. Jämn årlig avsättning för hela perioden.
3. Fondens utveckling år för år.
4. Första år med underskott, och minsta avsättning utan underskott.
5. Fondplanen som CSV, märkt som utkast.

Belopp i hela kronor (`Long`), inga flyttal. Avsättningen räknas in före årets uttag.

## Klart när

Alla uppgifter mergade med gröna tester.

## Gör inte

- Inga råd om avgifter eller vad föreningen bör besluta. Utdata är utkast.
- Servern rörs inte (Claude kopplar in fondplanen senare).
