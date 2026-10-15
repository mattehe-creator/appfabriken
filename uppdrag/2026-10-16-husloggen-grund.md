# Uppdrag: Husloggen, grunden (ny appmodul, databas, två flikar)

Läs CLAUDE.md, ARBETSFLODE.md, uppgifter/MALL.md och husloggen/APP.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

App 2 i portföljen är Husloggen (arbetsnamn): husets återkommande underhåll med påminnelser, och en logg över utfört arbete och utgifter med kvitton (IDEURVAL.md, omprövning 2026-10-16). Det här uppdraget lägger grunden: ny appmodul, databas version 1 och en startsida med två flikar som visar data. Formulär, kvitton, påminnelser, Pro och export kommer i senare uppdrag.

Garantivalvet är funktionsfryst och väntar på Google. Inget i `garantivalvet/` får ändras.

## Beslut som redan är fattade (BESLUTSLOGG.md 2026-10-16)

- Modul `:husloggen`, `namespace` och `applicationId` `se.tmconnect.husloggen`. Appnamnet är "Husloggen" på svenska och "House Log" på engelska tills Mattias beslutar annat.
- Samma versioner, inställningar och release-bygge (R8, `proguard-rules.pro`) som `garantivalvet/build.gradle.kts`. Beroenden nu: `:karna`, Compose (BOM, ui, material3, material-icons-core, ui-tooling-preview), activity-compose, Room med KSP och schemaexport till `husloggen/schemas/`, lifecycle. Testberoenden som i Garantivalvet. Inga andra.
- Ingen `INTERNET`. Lista den sammanslagna manifestets behörigheter i PR-beskrivningen.
- Databasen `HusDatabas`, version 1, med alla fält som 1.0 behöver, så att senare uppdrag inte kräver migrering:
  - `uppgift`: `id` (autogenererat), `namn`, `intervall_manader` (Int), `start_datum` (LocalDate), `senast_gjord` (LocalDate?), `paminnelse` (Boolean, standard true), `anteckning` (String?), `skapad` (Long, millisekunder).
  - `post`: `id`, `datum` (LocalDate), `vad`, `typ` (namnet på `PostTyp`: `UNDERHALL`, `REPARATION`, `OMBYGGNAD`), `belopp_ore` (Long?), `utforare` (String?), `anteckning` (String?), `kvitto_fil` (String?), `bilder` (String?, kommaseparerade filnamn), `uppgift_id` (Long?, främmande nyckel till `uppgift.id` med `ON DELETE SET NULL`, index), `skapad` (Long).
- Formateringen lyfts till `:karna` som nya klasser i `se.tmconnect.karna.format`: belopp i kronor ur öre och datum i enhetens språk, med samma beteende och tester som `garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/Formatering.kt` (inklusive hårt mellanslag i svenska tal). Garantivalvet byter inte nu.

## Uppdraget görs i två PR:er (ARBETSFLODE.md, "Uppdrag i två steg")

**PR 1, bara uppgiftsfiler.** Dela upp del B i uppgifter i `uppgifter/ny/` enligt `uppgifter/MALL.md` och CLAUDE.md punkt 4. Alla bygger på din kod och får `beror-på: cursor:2026-10-16-husloggen-grund`. Öppna PR 1 först.

**PR 2, del A.** Öppna den när PR 1 är öppnad. Frågor under "Frågor till Claude" i PR-beskrivningen.

## Del A (Cursor)

1. Modulen: `settings.gradle.kts`, `husloggen/build.gradle.kts`, manifest, `proguard-rules.pro`, en enkel adaptiv ikon (ett hus, egen ritning i temats färger, ersätts senare med butiksmaterialet) och `strings.xml` på svenska och engelska för det som del A visar.
2. Databasen: entiteterna `Uppgift` och `Post`, `PostTyp`, typkonverterare, `HusDao`, `HusDatabas`, `HusRepository` och exporterat schema 1. DAO:n ger `Flow` med alla uppgifter, alla poster (senaste datum först, sedan senast skapad) och antalet poster.
3. `MainActivity`, `HusViewModel` med fabrik och en startsida med två flikar i en nedre rad: "Att göra" (uppgifter) och "Logg" (poster), samt ett kugghjul överst till en inställningssida som bara visar appens version. Varje flik visar en tomtext när listan är tom, och annars en enkel rad per uppgift (namn) och per post (datum, vad, belopp). Temat från `:karna`. Del B förfinar raderna.
4. `:karna`: formateringen enligt besluten ovan, med tester.
5. Robolectric-test för DAO:n (mönster `garantivalvet/.../data/KopDaoTest.kt`): spara, ändra och ta bort uppgift och post; postordningen; att en post finns kvar med `uppgift_id = null` när dess uppgift tas bort; antalet poster.

## Del B (uppgifter för den lokala modellen)

Förslag som du får justera. Varje uppgift högst 3 filer och med test, i `husloggen/.../regler/` om inget annat sägs.

- **Nästa datum**: `nastaDatum(senastGjord: LocalDate?, startDatum: LocalDate, intervallManader: Int): LocalDate`. Exempel: (null, 2026-11-01, 12) → 2026-11-01; (2026-03-15, 2025-01-01, 6) → 2026-09-15; (2026-01-31, 2025-01-01, 1) → 2026-02-28; (2024-02-29, 2024-01-01, 12) → 2025-02-28.
- **Status**: `uppgiftStatus(nasta: LocalDate, idag: LocalDate)` ger `FORSENAD` (före idag), `SNART` (idag till och med 30 dagar fram) eller `SENARE`. Exempel med idag 2026-10-16: 2026-10-15 → FORSENAD, 2026-10-16 → SNART, 2026-11-15 → SNART, 2026-11-16 → SENARE.
- **Sortering**: `sorteraUppgifter(uppgifter, idag)` efter nästa datum, sedan namn utan hänsyn till stora och små bokstäver.
- **Loggen per år**: `grupperaPerAr(poster)` ger åren i fallande ordning, vart och ett med sina poster och summan i öre; poster utan belopp räknas inte i summan. Exempel: 2025-05-01 (1 000 kr), 2026-03-01 (500 kr) och 2026-04-01 (inget belopp) → [2026: 500 kr, 2 poster], [2025: 1 000 kr, 1 post].
- **Strängar**: flikarna, tomtexterna, statusarna och typerna på svenska och engelska ("Underhåll" / "Maintenance", "Reparation" / "Repair", "Ny-, till- eller ombyggnad" / "New build, extension or conversion", "Försenad" / "Overdue", "Snart" / "Due soon"), med test som `StringsNycklarTest`.
- **Raderna**: uppgiftsraden visar namn, nästa datum och status (färger ur `StatusFarger` i `:karna`); loggen har en rubrik per år med summan. Använd funktionerna ovan. Tryckytor minst 48 dp.

## Klart när

- `gradle testDebugUnitTest` och `gradle assembleDebug` är gröna för alla moduler, och `senaste-main` får en `husloggen-<sha>.apk` (bygg.yml gör det redan för varje appmodul).
- Den sammanslagna manifestet för `:husloggen` saknar `INTERNET`.
- Schema 1 ligger i `husloggen/schemas/`, och DAO-testet täcker punkt 5.
- Appen startar och visar två flikar med tomtext.
- Uppgifterna för del B ligger i `uppgifter/ny/` via PR 1.
- Ingen fil i `garantivalvet/` är ändrad.

## Gör inte

- Ändra något i `garantivalvet/`.
- Lägga till andra beroenden än de som står ovan. WorkManager, DataStore och ML Kit kommer med sina uppdrag.
- Formulär, kvittofoto, påminnelser, Pro, export eller färdiga förslag på uppgifter (senare uppdrag).
- Ändra `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md` eller `release.json`.
