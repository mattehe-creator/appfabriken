# Uppdrag: Husloggen, mörkt läge, välkomstskärm och butiksmaterial

Läs CLAUDE.md, ARBETSFLODE.md, uppgifter/MALL.md och husloggen/APP.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Husloggen 1.0 (STATUS.md, "Nu", punkt 9) saknar mörkt läge, välkomstskärm och butiksmaterial. Garantivalvet har redan motsvarande; återanvänd mönstren och lyft delade delar till `:karna` (Cursor gör det, inte den lokala modellen). Mattias lägger in materialet i Play Console själv (CLAUDE.md punkt 9). Appnamn och applicationId beslutar han vid publiceringen, så butikstexterna använder arbetsnamnet Husloggen och markeras som preliminära.

## Beslut som redan är fattade

- Texterna ligger som rena textfiler: `husloggen/butik/sv|en/titel.txt`, `kort.txt`, `lang.txt`. Gränser: titel högst 30 tecken, kort högst 80, lång högst 4 000.
- Inga juridiska löften. Skatteregler beskrivs som information, bara för Sverige, aldrig som råd. Inga namngivna konkurrenter, inga påhittade omdömen.
- Säljargument som stämmer med appen: offline, ingen `INTERNET`-behörighet, inga konton, ingen reklam, ingen analys. Gratis upp till 15 poster; Pro (engångsköp) tar bort gränsen.
- Skärmbilder genereras i JVM-test med Roborazzi (finns redan i Garantivalvet). Påhittad data, inga riktiga adresser eller varumärken.

## Uppdraget görs i två PR:er (ARBETSFLODE.md, "Uppdrag i två steg")

**PR 1, bara uppgiftsfiler.** Dela upp del B i uppgifter i `uppgifter/ny/` enligt MALL.md och CLAUDE.md punkt 4. Uppgifter som bygger på del A får `beror-på: cursor:2026-10-17-husloggen-butik-mork-valkomst`. Numrera vidare från senaste uppgiftsnumret. Öppna PR 1 först.

**PR 2, del A.** Öppna som riktig PR. Skriv frågor under "Frågor till Claude" i PR-beskrivningen.

## Del A (Cursor)

1. Mörkt läge i `:husloggen` enligt samma mönster som Garantivalvet (följ systemets läge, kontrast minst 4,5:1).
2. Välkomstskärm vid första start, samma mönster som Garantivalvet, texter i `strings.xml` på svenska och engelska.
3. `ButikTexterTest` i `:husloggen` som kontrollerar att alla sex textfiler finns, inte är tomma, håller teckengränserna och inte innehåller `http` eller `www`. Lägg tomma platshållare så att testet har något att läsa.
4. Roborazzi-skärmbilder (Att göra, Logg, formulär, Sammanställning, Pro-dialog) på sv och en, 1080 x 1920, funktionsgrafik 1024 x 500 och ikon 512 x 512, i `husloggen/butik/skarmbilder/<sv|en>/`. Kommandot som återskapar dem beskrivs i `husloggen/butik/README.md`.
5. `husloggen/butik/data-safety.md` med svar och motivering per fråga, grundad i det sammanslagna manifestet och beroendena (inklusive Play Billing, påminnelser och säkerhetskopia).

## Del B (uppgifter för den lokala modellen)

Förslag, justera fritt (högst 3 filer per uppgift, `ButikTexterTest` som test):
- Svensk titel och kort beskrivning.
- Svensk lång beskrivning: uppgifter och påminnelser, loggen med kvitton, sammanställning som underlag vid försäljning (information, bara Sverige), offline och integritet, gratis och Pro.
- Engelsk titel, kort och lång beskrivning (nämner att skatteinformationen gäller Sverige).

## Klart när

- `gradle testDebugUnitTest` och `gradle assembleDebug` är gröna för alla moduler.
- Mörkt läge och välkomstskärm fungerar och har test.
- Skärmbilder, funktionsgrafik, ikon och `data-safety.md` finns i `husloggen/butik/`.
- Uppgifterna för del B ligger i `uppgifter/ny/` via PR 1.
- Garantivalvets kod är oförändrad utöver uppenbar flytt av delat till `:karna`, med gröna tester för alla appmoduler.

## Gör inte

- Ändra appens funktioner, databasschema eller regler i `regler/`.
- Lägga till beroenden utöver vad Garantivalvet redan använder.
- Lägga in något i Play Console eller skriva priser.
- Ändra `.github/workflows/`, `verktyg/`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md` eller `release.json`.
