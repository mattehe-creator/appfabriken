# Uppdrag: köp i databasen, lista och formulär (Garantivalvet)

Läs CLAUDE.md och uppgifter/MALL.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Grundplattan finns (`:karna`, `:garantivalvet`). Garantiregeln finns i `garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/Garanti.kt` med tester. Nu ska användaren kunna lägga till köp, se dem i en lista sorterad efter när garantin går ut, och öppna ett köp för att ändra eller ta bort det. Omfånget för version 1.0 står i IDEURVAL.md.

Inte i det här uppdraget: foto av kvittot, påminnelser, reklamationsrätt (regeln kontrolleras först mot lagtexten), gratisgränsen och Pro, export.

## Uppdraget har två delar

**Del A gör du själv** (tungt enligt CLAUDE.md punkt 2: beroenden, databas, arkitektur).
**Del B delar du upp i uppgifter** för den lokala modellen, som filer i `uppgifter/ny/` enligt `uppgifter/MALL.md`.

Allt läggs i samma PR. Uppgifterna körs först när PR:en är mergad, så de får bygga på koden i del A.

## Del A (Cursor)

1. **Beroenden** i `:garantivalvet` (godkända för det här uppdraget, inga andra):
   - KSP-pluginen `com.google.devtools.ksp` 2.2.10-2.0.2 (i rotens `build.gradle.kts` med `apply false`).
   - `androidx.room:room-runtime`, `room-ktx` och `room-compiler` (ksp) 2.8.4. Exportera schemat till `garantivalvet/schemas/` (`room.schemaLocation`), och committa schemafilen.
   - `androidx.lifecycle:lifecycle-viewmodel-compose` och `lifecycle-runtime-compose` 2.8.7.
   - Endast som testberoenden: Robolectric och `androidx.test:core`, om DAO-testerna behöver dem.
2. **Datamodell** i paketet `se.tmconnect.garantivalvet.data`:
   - Entiteten `Kop`, tabellen `kop`: `id` (Long, autogenererad), `vad` (String), `var_kopt` (String, nullable), `kopdatum` (LocalDate, lagras som epokdag Long via TypeConverter), `garanti_manader` (Int), `pris_ore` (Long, nullable), `anteckning` (String, nullable), `skapad` (Long, epokmillis).
   - `KopDao`: `allaFlow(): Flow<List<Kop>>`, `hamta(id)`, `infoga(kop): Long`, `uppdatera(kop)`, `taBort(kop)`, `antal(): Int`.
   - `GarantiDatabas`, version 1, filnamnet `garantivalvet.db`. Inget `fallbackToDestructiveMigration`.
   - Tester för DAO:n (infoga, hämta, uppdatera, ta bort, att datumet överlever omvandlingen).
3. **Arkitektur:** ett `KopRepository` och en `KopViewModel` som exponerar listan som `StateFlow`. Enkel navigering mellan skärmarna med tillstånd i MainActivity eller ViewModel, utan navigationsbibliotek. Skärmarna: lista, lägg till, ändra. Skelett och kopplingar gör du; innehållet i skärmarna kan vara uppgifter i del B.

## Del B (uppgifter för den lokala modellen)

Dela upp resten i uppgifter som följer CLAUDE.md punkt 4 (högst 3 filer, cirka 150 rader, minst ett test, konkreta exempel). Förslag, som du får justera:

- Sortering: `sorteraEfterGaranti(lista, idag)` i ren Kotlin. Närmaste garantislut först, utgångna sist, med tester.
- Validering av formuläret i ren Kotlin: `vad` krävs, garantitid 0–120 månader, köpdatum inte i framtiden, pris som text ("1 299,50" och "1299" blir öre). Returnerar en lista med fel. Tester med exempel.
- Formatering för visning: pris i öre till "1 299,50 kr", datum enligt telefonens språk. Tester.
- Composable för en rad i listan: vad, var, garantislut och status med färg och text från `strings.xml` (svenska och engelska). Statusen kommer från `garantiStatus` i regler.
- Formulärskärmen (fält, datumväljare, spara, avbryt) som använder valideringen.
- Detalj- och ändringsläget, med bekräftelse före borttagning.

Varje uppgift anger exakt vilka filer som får ändras, vilka filer som ska läsas som förebild och vilket test som ska gå igenom. Lägg till `beror-på` där ordningen spelar roll, och numrera filnamnen i den ordning de ska köras.

## Klart när

- `gradle testDebugUnitTest` och `gradle assembleDebug` är gröna på din gren.
- Appen startar, och det går att lägga till ett köp med enkla fält (även om formuläret är ett skelett i väntan på del B) som sedan syns i listan.
- Schemafilen för version 1 finns i `garantivalvet/schemas/`.
- Uppgifterna för del B ligger i `uppgifter/ny/` och följer mallen.
- CLAUDE.md punkt 11 har en rad per ny mapp. Inget annat i CLAUDE.md ändras.

## Gör inte

- Inga andra beroenden än de som står i del A.
- Inga behörigheter och inget nätverk. Ändra inte `allowBackup` (det beslutas senare).
- Ingen foto-, påminnelse-, Pro- eller exportfunktion.
- Ändra inte `regler/Garanti.kt` eller dess test, och inget i `:karna`.
- Ändra inte `.github/workflows/`, `verktyg/`, `STATUS.md`, `BESLUTSLOGG.md` eller `IDEURVAL.md`.
- Pusha inte till main.
