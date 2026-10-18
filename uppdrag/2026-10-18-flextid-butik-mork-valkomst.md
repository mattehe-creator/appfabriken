# Uppdrag: Flextid, mörkt läge, välkomstskärm och butiksmaterial

Läs CLAUDE.md, ARBETSFLODE.md, uppgifter/MALL.md och flextid/APP.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Flextid (app 3) har grund, dagsregistrering, Pro-gräns och påminnelser mergade (#258, #260). Det som saknas för en publicerbar 1.0 är mörkt läge, välkomstskärm och butiksmaterial. Garantivalvet och Husloggen har redan motsvarande; återanvänd mönstren och lyft delade delar till `:karna` (Cursor gör det, inte den lokala modellen). Mattias lägger in materialet i Play Console själv (CLAUDE.md punkt 9). Appnamn och applicationId beslutar han vid publiceringen, så butikstexterna använder arbetsnamnet Flextid och markeras som preliminära.

## Svar på Cursors frågor i #260

- Påminnelsen "stämpla in" ligger kvar på fast klockslag 08:00 i 1.0. Inställbar tid kan komma i ett senare uppdrag.
- Produkt-id `pro` är rätt för Flextid. Varje app har egen Play-lista, så samma id krockar inte.

## Beslut som redan är fattade

- Texterna ligger som rena textfiler: `flextid/butik/sv|en/titel.txt`, `kort.txt`, `lang.txt`. Gränser: titel högst 30 tecken, kort högst 80, lång högst 4 000.
- Inga löften om vad kollektivavtal eller lag tillåter. Appen antar inget avtal och ger ingen rådgivning. Inga namngivna konkurrenter, inga påhittade omdömen.
- Säljargument som stämmer med appen: offline, ingen `INTERNET`-behörighet, inga konton, ingen reklam, ingen analys. Gratis upp till 15 dagposter; Pro (engångsköp) tar bort gränsen. Export och säkerhetskopia alltid gratis.
- Skärmbilder genereras i JVM-test med Roborazzi. Påhittad data.

## Uppdraget görs i två PR:er (ARBETSFLODE.md, "Uppdrag i två steg")

**PR 1, bara uppgiftsfiler.** Dela upp del B i uppgifter i `uppgifter/ny/` enligt MALL.md och CLAUDE.md punkt 4. Uppgifter som bygger på del A får `beror-på: cursor:2026-10-18-flextid-butik-mork-valkomst`. Numrera vidare från senaste uppgiftsnumret. Öppna PR 1 först.

**PR 2, del A.** Öppna som riktig PR. Skriv frågor under "Frågor till Claude" i PR-beskrivningen.

## Del A (Cursor)

1. Mörkt läge i `:flextid` enligt samma mönster som Garantivalvet och Husloggen (följ systemets läge, kontrast minst 4,5:1).
2. Välkomstskärm vid första start, samma mönster, texter i `strings.xml` på svenska och engelska.
3. `ButikTexterTest` i `:flextid` som kontrollerar att alla sex textfiler finns, inte är tomma, håller teckengränserna och inte innehåller `http` eller `www`. Lägg tomma platshållare så att testet har något att läsa.
4. Roborazzi-skärmbilder (Idag, Vecka, dagformulär, Inställningar, Pro-dialog) på sv och en, 1080 x 1920, funktionsgrafik 1024 x 500 och ikon 512 x 512, i `flextid/butik/skarmbilder/<sv|en>/`. Kommandot som återskapar dem beskrivs i `flextid/butik/README.md`.
5. `flextid/butik/data-safety.md` med svar och motivering per fråga, grundad i det sammanslagna manifestet och beroendena (inklusive Play Billing och påminnelser).

## Del B (uppgifter för den lokala modellen)

Förslag, justera fritt (högst 3 filer per uppgift, `ButikTexterTest` som test):
- Svensk titel och kort beskrivning.
- Svensk lång beskrivning: stämpla in och ut, rast, saldo mot egen normaltid, vecka, påminnelser, CSV-export och säkerhetskopia, offline och integritet, gratis och Pro.
- Engelsk titel, kort och lång beskrivning.

## Klart när

- `gradle testDebugUnitTest` och `gradle assembleDebug` är gröna för alla moduler.
- Mörkt läge och välkomstskärm fungerar och har test.
- Skärmbilder, funktionsgrafik, ikon och `data-safety.md` finns i `flextid/butik/`.
- Uppgifterna för del B ligger i `uppgifter/ny/` via PR 1.
- Andra appars kod är oförändrad utöver uppenbar flytt av delat till `:karna`, med gröna tester för alla appmoduler.

## Gör inte

- Ändra appens funktioner, databasschema eller regler i `regler/`.
- Lägga till beroenden utöver vad övriga appar redan använder.
- Lägga in något i Play Console eller skriva priser.
- Ändra `.github/workflows/`, `verktyg/`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md` eller `release.json`.
