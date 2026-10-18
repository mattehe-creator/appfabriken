# Uppdrag: Flextid, inställbar tid för påminnelsen "stämpla in"

Läs CLAUDE.md, ARBETSFLODE.md, uppgifter/MALL.md och flextid/APP.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Flextid (app 3) har grund, Pro-gräns, påminnelser, mörkt läge, välkomstskärm och butiksmaterial mergade (#258, #260, #263, #264–#268). Påminnelsen "Glöm inte stämpla in" ligger på fast klockslag 08:00 (svar i uppdraget 2026-10-18-flextid-butik-mork-valkomst: inställbar tid kan komma senare). Många börjar före eller efter 08:00, så en fast tid gör påminnelsen oanvändbar för dem. Detta är nästa funktion som inte väntar på Mattias prov.

## Uppgift

1. Inställning "Påminnelse stämpla in, klockslag" under Inställningar, standard 08:00. Val i hela och halva timmar mellan 05:00 och 12:00 (en enkel lista, ingen ny tidväljare-komponent om en sådan inte redan finns i `:karna`).
2. Värdet sparas som minuter efter midnatt i befintliga inställningar. Om det kräver en ny kolumn i Room: gör migrationen med test av migrationen och lägg det exporterade schemat i `flextid/schemas/`. Föredra DataStore om Flextid redan använder det för påminnelseinställningar, så slipper schemat ändras.
3. Påminnelsejobbet använder det sparade klockslaget i stället för 08:00. Den rena beräkningen av nästa påminnelsetid läggs i `regler/` så den går att enhetstesta.
4. Säkerhetskopian (JSON) tar med värdet. En fil utan värdet går att läsa tillbaka och ger 08:00. Versionsnumret höjs bara om formatet kräver det.
5. Strängar på svenska och engelska i `strings.xml`.

## Exempel för testerna

- Inget sparat värde: påminnelse kl. 08:00.
- Sparat 06:30 och klockan är 06:00 en arbetsdag utan registrerad start: nästa påminnelse idag 06:30.
- Sparat 06:30 och klockan är 07:00: nästa påminnelse nästa arbetsdag 06:30.
- Dagen har redan en starttid: ingen påminnelse "stämpla in" den dagen, oavsett klockslag.
- Säkerhetskopia utan värdet återställs till 08:00. Ett värde utanför 05:00–12:00 (till exempel 90 minuter, alltså 01:30) är ogiltigt och ger 08:00.

## Hur Cursor arbetar

Tungt (migration, `:karna`) gör Cursor själv. Dela ut ren logik och strängar som uppgifter till den lokala modellen enligt MALL.md. Skriv frågor under "Frågor till Claude" i PR-beskrivningen.

## Klart när

- `gradle testDebugUnitTest` och `gradle assembleDebug` är gröna för alla moduler.
- Tester täcker exemplen ovan.
- Ingen ny behörighet, inget nytt beroende.
- Butikstexterna i `flextid/butik/` är oförändrade (påminnelsen beskrivs redan allmänt).

## Gör inte

- Ändra Garantivalvet, Husloggen eller `:karna` mer än vad en delad tidväljare kräver.
- Göra klockslaget för "stämpla ut" inställbart (den följer normaltiden).
- Ändra `.github/workflows/`, `verktyg/`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md` eller `release.json`.
