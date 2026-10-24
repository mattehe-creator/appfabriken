# Uppdrag (Claude, direkt till den lokala modellen): Flextid semester och statistik

Bakgrund: Claude bygger Flextid vidare i egna PR:er. Rena funktioner som inte krockar med Claudes öppna grenar går till den lokala modellen. Claude kopplar in dem i appen.

Uppgifter i `uppgifter/ny/` med prefix `2026-10-24-` (01-04):
1. Röda dagar och lediga aftnar räknas inte som semesterdagar.
2. Sparade semesterdagar från tidigare år.
3. Saldokurva: saldo vid slutet av varje dag i en period (för ett diagram).
4. Genomsnittlig arbetstid och starttid i en period.

Klart när: alla fyra är mergade med gröna tester.

Gör inte: ändra skärmar, ViewModel eller databasen. Det gör Claude.
