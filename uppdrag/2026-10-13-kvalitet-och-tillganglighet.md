# Uppdrag: lint, tillgänglighet (tryckytor och kontrast) och release-kontroll

Läs CLAUDE.md, ARBETSFLODE.md och uppgifter/MALL.md först och följ dem. Ändra inget utöver uppdraget. Uppdraget är tungt (många filer, `:karna`, byggsystem): gör det själv direkt i en PR, dela inte upp det för den lokala modellen.

## Bakgrund

Claude har idag byggt mycket i `:garantivalvet` utan Android SDK i sin miljö (bara GitHub Actions och Robolectric har kört koden): kamera och rotation, bilder på felet, automatisk säkerhetskopia, hemförsäkringsunderlag, AI-delning, mörkt läge (val i Inställningar), uppföljning av reklamation och ett release-bygge med R8 och `targetSdk` 36. Appen ska snart till Google Play. Vi vill hitta det som tester och granskning missat, innan Mattias provar på telefon. Tillgänglighetskraven står i CLAUDE.md punkt 6: kontrast minst 4,5:1 för brödtext, tryckytor minst 48 dp, innehållsbeskrivning på ikoner.

## Uppgift (Cursor, en PR)

**1. Android lint.** Kör `gradle :garantivalvet:lintDebug :garantivalvet:lintRelease :karna:lintDebug`. Rätta alla fel och de varningar som rör korrekthet (till exempel `MissingTranslation`, `NewApi`, `HardcodedText`, `ContentDescription`, `ObsoleteSdkInt`, `UnusedResources` som är riktiga fynd, felaktig `PendingIntent`-flagga, `Instantiatable`). Tysta inte fynd med en baslinjefil eller `@SuppressLint` utan att skriva varför i PR-beskrivningen. Stilvarningar rättas inte.

**2. Tryckytor (48 dp).** Lägg Robolectric Compose-tester (finns redan: `ui-test-junit4`, se `ButikSkarmbilderTest.kt` och `GranskaUtseendeTest.kt` för hur skärmarna ritas utan aktivitet) som kontrollerar att varje klickbar nod (`hasClickAction()`) på dessa skärmar är minst 48 dp hög och bred, i både ljust och mörkt läge och vid `fontScale` 1,0 och 1,5: `KopListaSkarm`, `KopDetaljSkarm` (med och utan reklamation), `KopFormularSkarm`, `ReklamationsSkarm`, `InstallningarSkarm`. Rätta brister i skärmarna (till exempel `FilterChip` och `AssistChip` som är lägre än 48 dp, `TextButton` utan `heightIn(min = 48.dp)`). Där Material själv garanterar minsta tryckyta via `minimumInteractiveComponentSize` räcker det att testet bekräftar det.

**3. Innehållsbeskrivning.** Test eller granskning som visar att varje `Icon` och `IconButton` i `:garantivalvet/src/main/` antingen har en `contentDescription` ur `strings.xml` eller är rent dekorativ (`contentDescription = null` med motivering i en kort kommentar). Rätta brister. Beskrivningarna ska finnas på svenska och engelska.

**4. Kontrast.** `:karna` har `KontrastTest` och `Kontrast.kt`. Utöka så att alla par text/bakgrund som appen använder är minst 4,5:1 (brödtext) i både ljust och mörkt färgschema: `onSurface/surface`, `onSurfaceVariant/surface`, `onSurfaceVariant/surfaceContainerLow`, `onPrimary/primary`, `onPrimaryContainer/primaryContainer`, `onSecondaryContainer/secondaryContainer`, `onTertiaryContainer/tertiaryContainer`, `onErrorContainer/errorContainer`, `primary/surface` (länkfärg), `error/surface`, samt statuschipparen i `StatusFarger.kt`. Mörkt läge är nytt och inte kontrollerat. Rätta färgvärden i `AppfabrikFarger.kt` som inte når 4,5:1, med minsta möjliga förändring, och skriv i PR-beskrivningen vilka. En ändring i `:karna` påverkar alla appar: testerna för alla appmoduler ska vara gröna.

**5. Release-kontroll.** Kör `gradle :garantivalvet:assembleRelease` (R8 är på, se `garantivalvet/build.gradle.kts` och `proguard-rules.pro`):
   - Läs R8:s varningar och `build/outputs/mapping/release/missing_rules.txt` om filen finns. Lägg till nödvändiga `-keep`-regler i `proguard-rules.pro` för sådant som annars skulle brytas vid körning (Room, WorkManager-arbetare, ML Kit, Play Billing). Lägg inte till regler på chans.
   - Lista sammanslagen release-manifest: alla behörigheter och komponenter (`processReleaseMainManifest`, `build/intermediates/merged_manifests/release/`). Kontrollera mot `garantivalvet/butik/data-safety.md`. Stämmer det inte (till exempel en ny behörighet eller komponent från ett bibliotek) uppdatera `data-safety.md` så att den beskriver verkligheten, och skriv det i PR-beskrivningen. `INTERNET` och `ACCESS_NETWORK_STATE` ska inte finnas (CLAUDE.md punkt 5).
   - Skriv APK-storleken (release) i PR-beskrivningen.

## Klart när

- `gradle testDebugUnitTest` och `gradle assembleDebug assembleRelease` är gröna för alla moduler.
- Lint har inga fel kvar (varningar som lämnats med motivering listas i PR-beskrivningen).
- Testerna för tryckytor, innehållsbeskrivning och kontrast finns och är gröna, och alla rättade brister är listade i PR-beskrivningen (före/efter).
- `data-safety.md` stämmer med sammanslagen release-manifest.

## Gör inte

- Lägga till nya beroenden i `:garantivalvet` eller `:karna` (testbiblioteken som redan finns räcker).
- Ändra databasschema, behörigheter (utom att ta bort) eller appens funktion och texter.
- Ändra `butik/sv/*.txt`, `butik/en/*.txt` och skärmbilderna i `butik/skarmbilder/` (de genereras av workflowet Skärmbilder).
- Ändra `.github/workflows/`, `verktyg/`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md` eller `release.json`.
- Skriva något i repot som är en signeringsnyckel eller hemlighet.

Skriv frågor under "Frågor till Claude" i PR-beskrivningen.
