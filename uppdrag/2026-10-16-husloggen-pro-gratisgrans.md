# Uppdrag: Husloggen, gratisgräns och Pro

Läs CLAUDE.md, ARBETSFLODE.md, uppgifter/MALL.md och husloggen/APP.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Affärsmodellen i CLAUDE.md punkt 6: gratisversionen är en hel app, och Pro (engångsköp, 49 kr) tar bara bort gränsen. I Husloggen är gränsen 15 poster i loggen (APP.md). Uppgifter och påminnelser är obegränsade. Pro-köpet finns redan i `:karna` (`se.tmconnect.karna.pro`: `ProKalla`, `PlayProKalla`, cache i DataStore) från Garantivalvet. Det här uppdraget kopplar in det i `:husloggen`. Garantivalvet är funktionsfryst och får inte ändras.

Uppdraget skickas medvetet i förväg, medan bilder och påminnelser fortfarande körs (Mattias 2026-10-16: kön ska aldrig vara tom). Håll dina ändringar små i filer som andra uppdrag också rör (`HusViewModel.kt`, `MainActivity.kt`) och skriv frågor under "Frågor till Claude" om du hamnar i konflikt.

## Beslut som redan är fattade (APP.md, BESLUTSLOGG.md 2026-10-16)

- Gränsen 15 ligger på ett enda ställe i `:husloggen` (`regler/`). Produkt-id `pro` läses från appmodulen, inte från `:karna`.
- "Markera som gjord" flyttar alltid nästa datum. När gränsen är nådd sparas ingen ny post utan Pro, men uppgiften flyttas ändå.
- Befintliga poster kan alltid visas, ändras och tas bort, även över gränsen (till exempel efter återställning).
- Bilder räknas inte mot gränsen.
- Inget databasschema ändras. Inget pris i appen (Play visar priset). Ingen prenumeration.

## Uppdraget görs i två PR:er (ARBETSFLODE.md, "Uppdrag i två steg")

**PR 1, bara uppgiftsfiler.** Dela upp del B i uppgifter i `uppgifter/ny/` enligt `uppgifter/MALL.md` och CLAUDE.md punkt 4. Uppgifter som bygger på din kod får `berör-på` med uppgiftsfilen eller `cursor:2026-10-16-husloggen-pro-gratisgrans` (körskriptet matchar nu mergade `cursor/`-PR:er som nämner uppdragsnamnet). Öppna PR 1 först.

**PR 2, del A.** Öppna den när PR 1 är öppnad. Frågor under "Frågor till Claude" i PR-beskrivningen.

## Del A (Cursor)

1. **`:husloggen`:** koppla in `PlayProKalla` i `MainActivity` och ViewModel-fabriken så att del B kan läsa `arPro` och anropa `kop`. Produkt-id `pro` från appmodulen.
2. **Regler:** `GRATIS_MAX_POSTER = 15` och en ren funktion `kanLaggaTillPost(antal: Int, arPro: Boolean): Boolean` i `regler/`. Test: (14, false) → true, (15, false) → false, (15, true) → true, (500, true) → true.
3. **Repository/ViewModel:** `sparaPost` med `id == 0` och återställning av nya poster respekterar gränsen och ger ett tydligt resultat (till exempel `GransNadd`) i stället för att spara. "Markera som gjord" flyttar uppgiftens datum även när posten inte kan sparas. Test mot Room i minnet.
4. **Manifest:** kontrollera att sammanslagna manifestet för `:husloggen` inte får `INTERNET` eller `ACCESS_NETWORK_STATE` (använd `tools:node="remove"` om biblioteket tar med dem). Lista behörigheterna i PR-beskrivningen.

## Del B (uppgifter för den lokala modellen)

Förslag som du får justera. Högst 3 filer per uppgift och med test.

- **Strängar** (svenska och engelska): "Pro", "Lås upp Pro", förklaring ("Gratisversionen sparar upp till 15 poster i loggen. Pro är ett engångsköp utan prenumeration och tar bort gränsen. Uppgifter och påminnelser är alltid obegränsade."), "Du har Pro", "Köpet kunde inte genomföras. Försök igen.", "Gränsen är nådd: 15 poster". Test som `StringsNycklarTest`.
- **Pro-dialog:** förklaringen, knappen "Lås upp Pro" som anropar `kop`, och "Inte nu". Tryckytor minst 48 dp.
- **Gräns i loggen:** när `kanLaggaTillPost` är false öppnar knappen för ny post Pro-dialogen i stället för formuläret.
- **Efter "Markera som gjord" vid gränsen:** visa ett kort meddelande att uppgiften flyttades men att posten inte sparades utan Pro.
- **Pro-rad** i menyn eller på loggfliken: "Du har Pro" eller "Lås upp Pro".

## Klart när

- `gradle testDebugUnitTest` och `gradle assembleDebug` är gröna för alla moduler.
- Testerna täcker exemplen ovan.
- Sammanslagna manifestet för `:husloggen` saknar `INTERNET`. Databasschemat är oförändrat (schema 1).
- Uppgifterna för del B ligger i `uppgifter/ny/` via PR 1.
- Ingen fil i `garantivalvet/` är ändrad.

## Gör inte

- Ändra databasschemat eller något i `garantivalvet/`.
- Lägga till andra beroenden än de som redan finns i `:karna`.
- Låsa uppgifter, påminnelser eller befintliga poster bakom Pro.
- Lägga in pris i appen.
- Ändra `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md` eller `release.json`.
