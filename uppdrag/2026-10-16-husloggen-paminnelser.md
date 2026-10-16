# Uppdrag: Husloggen, påminnelser före nästa datum

Läs CLAUDE.md, ARBETSFLODE.md, uppgifter/MALL.md och husloggen/APP.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Bilderna på poster är klara eller på väg (uppdrag `2026-10-16-husloggen-bilder`, del A mergad som #178). Nästa steg (STATUS.md, Nu, punkt 5) är påminnelser: en notis när en återkommande uppgift närmar sig sitt nästa datum. Garantivalvet har samma mönster (uppdrag `2026-10-09-paminnelser`), men Garantivalvet är funktionsfryst och får inte ändras. Mönstret lyfts i stället till `:karna` som nya klasser, och Husloggen använder dem (BESLUTSLOGG.md 2026-10-16). Garantivalvet byter till `:karna`-versionerna i 1.1.

Det här uppdraget skrivs medvetet innan bilduppdragets uppgifter är klara, så att Cursor kan börja medan den lokala modellen slutför bilderna. Uppdraget rör inte de filer som bilduppgifterna ändrar (`PostFormularSkarm.kt`, `PostDetaljSkarm.kt`, `HusListRader.kt`, `BildHelskarm.kt`) utom `MainActivity.kt`, som du ändrar minimalt. Hamnar du i konflikt med en öppen `lokal/`-PR: vänta inte, gör din ändring och skriv frågan under "Frågor till Claude".

## Beteende

- En påminnelse gäller en **uppgift** (inte en post). Den aviseras när nästa datum är högst 7 dagar bort, och när nästa datum redan har passerat. Varje kombination av uppgift och nästa datum aviseras högst en gång. Markeras uppgiften som gjord får den ett nytt nästa datum och därmed en ny påminnelse längre fram.
- Antalet dagar före (7) ligger på ett ställe i `regler/`.
- En missad körning (telefonen avstängd) får inte tappa påminnelsen. Därför ett fönster och inte exakt dag.
- Påminnelser är av som standard. De slås på med ett reglage i Att göra-fliken. På Android 13+ begärs `POST_NOTIFICATIONS` först när användaren slår på. Nekas den förblir reglaget av och en förklaring visas.
- En notis per uppgift, eller en samlad om fler än en. Tryck på notisen öppnar appen på fliken Att göra.
- Uppgifter och påminnelser är obegränsade (APP.md), så ingen koppling till gratisgränsen.
- Alla texter på svenska och engelska i `strings.xml`.

## Uppdraget görs i två PR:er (ARBETSFLODE.md, "Uppdrag i två steg")

**PR 1, bara uppgiftsfiler.** Dela upp del B i uppgifter i `uppgifter/ny/` enligt `uppgifter/MALL.md` och CLAUDE.md punkt 4. Uppgifter som bygger på din kod får `beror-på` med uppgiftsfilen eller, om det inte går, `cursor:2026-10-16-husloggen-paminnelser` (körskriptet matchar nu mergade `cursor/`-PR:er som nämner uppdragsnamnet, se `uppgifter/MALL.md`). Öppna PR 1 först.

**PR 2, del A.** Öppna den när PR 1 är öppnad. Frågor under "Frågor till Claude" i PR-beskrivningen.

## Del A (Cursor: ny behörighet, bakgrundsjobb, lyft till `:karna`)

1. **Beroenden** (godkända av Claude): `androidx.work:work-runtime-ktx:2.10.1` och `androidx.datastore:datastore-preferences:1.1.1` i `:karna` och, om de behövs direkt, i `:husloggen`. Testberoende `androidx.work:work-testing:2.10.1` om du behöver det. Inga andra beroenden.
2. **Behörighet:** `android.permission.POST_NOTIFICATIONS` i `:husloggens` manifest. Aldrig vid start.
3. **`:karna`, nytt och generellt:**
   - Ren logik `paminnelse/Paminnelse.kt`: given en lista frister (nyckel, slutdatum), dagens datum, antal dagar före och redan aviserade nycklar, returnera de frister som ska aviseras nu. Frist aviseras när dagar kvar ≤ dagar före och nyckeln inte är aviserad. Datum som redan passerat aviseras också (till skillnad från Garantivalvet). Exempel som ska testas: dagar före 7; slut om 7 dagar → aviseras; om 8 → nej; om 0 → aviseras; i går → aviseras; redan aviserad nyckel → nej; samma uppgift med nytt datum (ny nyckel) → aviseras igen.
   - Lager för inställning och aviserade nycklar i DataStore (katalog- eller filnamn som parameter), och ett jobb `PaminnelseWorker` (CoroutineWorker) som app-modulen konfigurerar: hur fristerna hämtas, notisens texter och vart ett tryck leder. Unikt periodiskt arbete, en gång per dygn, policy KEEP.
   - Rör inte Garantivalvets `PaminnelseWorker` eller dess lager.
4. **`:husloggen`:** koppla ihop med uppgifterna (nyckel = uppgift-id och nästa datum), notiskanal med namn från `strings.xml`, `PendingIntent` med `FLAG_IMMUTABLE` till `MainActivity`. Jobbet startas när reglaget slås på och vid appstart om det är på, och avbryts när det slås av. Inget visas och inget kraschar om behörigheten saknas.
5. **ViewModel:** `paminnelserPa: StateFlow<Boolean>` och `sattPaminnelser(pa: Boolean)` med KDoc.
6. Enhetstester för logiken och ett test för att jobbet inte aviserar något när inställningen är av.

## Del B (uppgifter för den lokala modellen)

Förslag som du får justera. Högst 3 filer per uppgift och med test.

- **Strängar** svenska och engelska: notisens titel och text, kanalens namn, reglagets rubrik och förklaring, texten när behörigheten nekats. Test som `StringsNycklarTest`.
- **Reglage** i Att göra-fliken med behörighetsbegäran (`ActivityResultContracts.RequestPermission`) när användaren slår på, och förklaring när den nekats.
- **Text i notisen** byggd med strängarna, som egen uppgift om det behövs.

## Klart när

- `gradle testDebugUnitTest` och `gradle assembleDebug` är gröna för alla moduler, och `senaste-main` får en ny `husloggen-<sha>.apk`.
- Den enda nya behörigheten är `POST_NOTIFICATIONS`, och `:husloggen` saknar fortfarande `INTERNET`.
- Databasschemat är oförändrat (schema 1). Aviserade nycklar ligger i DataStore.
- Testerna i `:karna` täcker exemplen i punkt A3.
- Uppgifterna för del B ligger i `uppgifter/ny/` via PR 1.
- Ingen fil i `garantivalvet/` är ändrad.

## Gör inte

- Ändra något i `garantivalvet/` eller databasschemat.
- Lägga till andra beroenden än de i punkt A1.
- Inget nätverk, inga exakta alarm (`SCHEDULE_EXACT_ALARM`), ingen förgrundstjänst.
- Gratisgräns, Pro, export, säkerhetskopia eller OCR.
- Ändra `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md` eller `release.json`.
