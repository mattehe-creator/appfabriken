# Uppdrag: butiksmaterial för Google Play (Garantivalvet 1.0)

Läs CLAUDE.md, ARBETSFLODE.md och uppgifter/MALL.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Säkerhetskopian är sista tekniska steget i 1.0. Kvar före release är butiksmaterialet i `garantivalvet/butik/` (CLAUDE.md punkt 7, "En release är klar när"). Mattias lägger in materialet i Play Console själv (punkt 9). Allt annat ska vara färdigt i repot så att han bara kopierar.

## Beslut som redan är fattade (Claude, BESLUTSLOGG.md 2026-10-12)

- Svenska och engelska. Svenska är huvudspråk i butiken.
- Texterna ligger som rena textfiler, en per fält och språk: `garantivalvet/butik/sv/titel.txt`, `kort.txt`, `lang.txt`, och samma under `en/`. Gränser enligt Play: titel högst 30 tecken, kort beskrivning högst 80, lång högst 4 000.
- Ingen text lovar juridiskt utfall. Reklamationsrätten beskrivs som information om konsumentköplagen (2022:260), inte rådgivning, och bara för köp i Sverige. Inga jämförelser med namngivna konkurrenter, inga betyg eller citat som påstås komma från användare.
- Säljargument som stämmer med appen och får användas: fungerar helt offline, ingen `INTERNET`-behörighet, inga konton, ingen reklam, ingen analys. Gratis upp till 15 köp; Pro (engångsköp) ger obegränsat antal, PDF-underlag och säkerhetskopia.
- Skärmbilder genereras automatiskt i JVM-test med Roborazzi (Robolectric finns redan). Roborazzi läggs bara till som `testImplementation` i `:garantivalvet`. Det är det enda nya beroendet och är godkänt här. Bilderna visar påhittade köp (t.ex. diskmaskin, mobiltelefon, cykel), inga riktiga personer, butiker som finns på riktigt eller varumärken.
- Data safety: svaren skrivs i `garantivalvet/butik/data-safety.md` med motivering per fråga, grundad i det sammanslagna manifestet och beroendena.

## Uppdraget görs i två PR:er (ARBETSFLODE.md, "Uppdrag i två steg")

**PR 1, bara uppgiftsfiler.** Dela upp del B i uppgifter i `uppgifter/ny/` enligt `uppgifter/MALL.md` och CLAUDE.md punkt 4. Uppgifter som bygger på del A får `beror-på: cursor:2026-10-12-butiksmaterial`. Numrera från 31. Öppna PR 1 först, innan du börjar på del A.

**PR 2, del A.** Öppna den som en riktig PR (inte bara en gren) så fort den är klar. Skriv frågor under "Frågor till Claude" i PR-beskrivningen.

## Del A (Cursor)

1. **Kontroll av texter:** ett enhetstest `ButikTexterTest` i `:garantivalvet` som läser filerna under `garantivalvet/butik/` och kontrollerar att alla sex filer finns, inte är tomma, håller teckengränserna ovan och inte innehåller `http` eller `www`. Lägg tomma platshållarfiler så att testet har något att läsa; del B fyller dem.
2. **Skärmbilder:** Roborazzi-test som renderar listan, detaljvyn med reklamationsrätt, formuläret och Pro-dialogen med påhittad data, på svenska och engelska, i porträtt 1080 × 1920. Plus en funktionsgrafik 1024 × 500 (appnamn och en kort rad, i appens tema). Bilderna sparas i `garantivalvet/butik/skarmbilder/<sv|en>/` och committas. Beskriv i `garantivalvet/butik/README.md` vilket gradle-kommando som återskapar dem.
3. **Data safety:** `garantivalvet/butik/data-safety.md` med svar och motivering på Play-formulärets frågor. Ta särskilt ställning till vad Google Play Billing innebär för formuläret och om ikonen 512 × 512 finns; om den saknas, rendera den från launcher-ikonen på samma sätt som skärmbilderna.

## Del B (uppgifter för den lokala modellen)

Förslag, som du får justera (varje uppgift högst 3 filer, `ButikTexterTest` som test):
- Svensk titel och kort beskrivning.
- Svensk lång beskrivning: vad appen gör, reklamationsrätten som information, offline och integritet, gratis och Pro.
- Engelsk titel, kort och lång beskrivning (engelska butiken nämner att reklamationsreglerna gäller Sverige och att appen annars visar garantitiden).

## Klart när

- `gradle testDebugUnitTest` och `gradle assembleDebug` är gröna för alla moduler.
- Skärmbilderna, funktionsgrafiken och ikonen finns i `garantivalvet/butik/` och visar påhittad data.
- `data-safety.md` finns med motivering per svar.
- Uppgifterna för del B ligger i `uppgifter/ny/` via PR 1.
- Det sammanslagna manifestet för `:garantivalvet` är oförändrat.

## Gör inte

- Ändra appens beteende, strängar i appen, manifest eller databasschema.
- Lägga till andra beroenden än Roborazzi som `testImplementation`.
- Lägga in något i Play Console eller skriva priser.
- Ändra `.github/workflows/`, `verktyg/`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md` eller `release.json`.
