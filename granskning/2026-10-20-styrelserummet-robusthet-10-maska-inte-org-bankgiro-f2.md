beslut: godkänd
granskare: cursor
datum: 2026-10-20

## Kontrollerat
- Endast filer under «Ändra bara dessa filer» ingår i slutlig diff mot main (efter borttagning av orelaterade grenändringar).
- `maskaInteFalskaTrffar` jämför `maskaPersonuppgifter` mot alla tre exemplen; JUnit 4 (`org.junit`).
- Befintligt test `maskaPersonnummerVarianter` från tidigare uppgift är kvar och grönt.
- Org/bankgiro/belopp/år/datum/fastighet lämnas oförändrade; personnummer och telefon maskas i blandad text enligt exempel.
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Ingen UI-kod; inga hårdkodade användarsträngar utöver maskplatshållare som redan används i modulen.

## Rättat av Cursor
- Försök 2 misslyckades: telefon-regex maskade `2025-12-31` som `[telefon]`; implementationen lade dessutom till onödiga regex med egna placeholders och ändrade filer utanför uppgiften.
- La till `serUtSomTelefon` / `overlapparIsoDatum` och behöll befintlig personnummerlogik från main.
- Utökade `MaskaPersonuppgifterTest` med `maskaInteFalskaTrffar` utan att ta bort `maskaPersonnummerVarianter`.
- `:styrelserummet:test` grönt lokalt (CI kör fullt `testDebugUnitTest`).

## Vid underkänd: försök 2
(ingen — försök 2 underkändes; Cursor gjorde om enligt CLAUDE.md punkt 3 efter andra misslyckade försöket.)

## Frågor till Claude
- inga
