# Uppdrag: datum och pris i enhetens språk, engelska exempeldata i skärmbilderna

Läs CLAUDE.md, ARBETSFLODE.md och uppgifter/MALL.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Efter #90 är butiksmaterialet nästan klart. De engelska skärmbilderna (`garantivalvet/butik/skarmbilder/en/`) visar två fel:

1. Datum visas på svenska även i engelskt gränssnitt ("Warranty ends 1 aug. 2026"). Orsaken är att `formateraDatum`, `formateraPrisKr` och `byggUnderlag` i `garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/` har `sv-SE` som standardvärde, och anroparna i `ui/KopDetaljSkarm.kt`, `ui/KopListRad.kt` och `MainActivity.kt` skickar ingen locale. Engelska användare ser alltså svenska datum i appen. CLAUDE.md punkt 6 kräver svenska och engelska från start.
2. Exempeldatan i `ButikSkarmbilderTest.kt` (`exempelLista`) är på svenska även i de engelska bilderna ("Mobiltelefon", "Cykel", "Telefonbutiken i centrum").

## Uppgift

Del A (Cursor, en PR): inget. Uppdraget är litet nog för den lokala modellen. Dela upp det i uppgifter enligt uppgifter/MALL.md och punkt 4, till exempel:

1. Anroparna i UI och `MainActivity.kt` skickar enhetens aktuella locale (från Compose `LocalConfiguration` eller `Locale.getDefault()`, välj en väg och använd den överallt) till `formateraDatum`, `formateraPrisKr` och `byggUnderlag`. Standardvärdet `sv-SE` i funktionerna får vara kvar så att befintliga tester fortsätter gå igenom. Pris visas fortfarande i kronor ("kr"), men med tusentals- och decimaltecken enligt locale. Exempel: `formateraDatum(2026-08-01, Locale.ENGLISH)` ger "Aug 1, 2026"; med `sv-SE` "1 aug. 2026". `formateraPrisKr(123450, Locale.US)` ger "1,234.50 kr"; med `sv-SE` "1 234,50 kr". Lägg enhetstester för de engelska fallen.
2. `ButikSkarmbilderTest.kt` får exempeldata per språk: svenska namn i `sv/`, engelska i `en/` (till exempel "Dishwasher", "Local appliance store", "Mobile phone", "Phone shop downtown", "Bicycle", "Sports shop"). Generera om alla skärmbilder i `garantivalvet/butik/skarmbilder/en/` och de bilder i `sv/` som ändras av uppgift 1. Funktionsgrafiken genereras om bara om den ändras.

Skärmbilderna kan inte genereras av den lokala modellen om Roborazzi kräver något den saknar; gör i så fall bilduppgiften själv.

## Klart när

- `gradle testDebugUnitTest` och `gradle assembleDebug` är gröna för alla moduler.
- De engelska skärmbilderna visar engelska datum och engelsk exempeldata. De svenska visar svenska.
- Inga hårdkodade strängar i appen (exempeldatan i testet är undantagen).

## Gör inte

- Ändra databasschema, manifest, behörigheter eller beroenden.
- Ändra lagrade värden (pris lagras i öre, datum som `LocalDate`).
- Ändra `garantivalvet/butik/sv/*.txt`, `en/*.txt` eller `data-safety.md`.
- Ändra `.github/workflows/`, `verktyg/`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md` eller `release.json`.

Skriv frågor under "Frågor till Claude" i PR-beskrivningen.
