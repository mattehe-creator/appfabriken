# Uppdrag: grundplattan (rotbygge, :karna och skalet till :garantivalvet)

Läs CLAUDE.md först och följ den. Ändra inget utöver uppdraget.

## Bakgrund

Repot är nytt och har inget Gradle-bygge. Det här uppdraget lägger grunden som alla appar i portföljen bygger på. Det är tungt arbete enligt CLAUDE.md punkt 2 (byggsystem, arkitektur), så du gör det själv. Dela inte upp det i uppgifter för den lokala modellen.

Versionerna följer CLAUDE.md punkt 5: Kotlin 2.2.10, AGP 9.4.1, Gradle 9.7.1, Compose-kompilatorplugin 2.2.10, Compose BOM 2024.10.01, Material3, activity-compose 1.9.3, KSP 2.2.10-2.0.2, JVM 17. Room och WorkManager läggs inte till än.

Gradle-inställningarna i heros-run fungerar med just dessa versioner, och kan användas som förebild om något krånglar: `gradle.properties` behöver bland annat `android.builtInKotlin=false` och `android.newDsl=false`.

## Uppgift

1. **Rotbygget.**
   - `settings.gradle.kts`: rotprojektet heter `appfabriken` och inkluderar `:karna` och `:garantivalvet`. Repositories som i ett vanligt Android-projekt (google, mavenCentral, gradlePluginPortal för plugins).
   - `build.gradle.kts` i roten med plugin-versionerna (`apply false`).
   - `gradle.properties` och `gradle/wrapper/gradle-wrapper.properties` (Gradle 9.7.1).
2. **Biblioteket `:karna`** (Android-bibliotek, namespace `se.tmconnect.karna`, minSdk 26, compileSdk 36):
   - `Tema.kt`: `AppfabrikTema(content)` med Material 3-färgschema i ljust och mörkt läge (följer systemet). Färger:
     - Ljust: bakgrund `#FAFAF7`, yta `#FFFFFF`, text `#1C1B19`, sekundär text `#5E5B55`, primär `#1F5F5B`, text på primär `#FFFFFF`, fel `#B3261E`.
     - Mörkt: bakgrund `#121413`, yta `#1B1E1D`, text `#E7E5DF`, sekundär text `#A9A59C`, primär `#7FC4BC`, text på primär `#0B2422`, fel `#F2B8B5`.
   - `Kontrast.kt`: ren Kotlin, `fun kontrastkvot(a: Long, b: Long): Double` enligt WCAG 2.x (relativ luminans, sRGB-linjärisering). Färger som `0xFFRRGGBB`.
   - Test `KontrastTest`: kontrollera kvoten för svart mot vitt (21,0) och vitt mot vitt (1,0), och att varje textfärg (text, sekundär text, text på primär, fel) mot sin bakgrund i båda lägena är minst 4,5. Testet läser färgerna från samma konstanter som temat använder, inte från kopior.
3. **Appmodulen `:garantivalvet`** (namespace och applicationId `se.tmconnect.garantivalvet`, minSdk 26, targetSdk 35, compileSdk 36, versionCode 1, versionName 0.1.0):
   - Beror på `:karna`.
   - `MainActivity.kt` med `AppfabrikTema`, en `Scaffold` med toppfält som visar appens namn, och en tom vy med texten för tomt läge mitt på skärmen.
   - `res/values/strings.xml` (engelska, standard): `app_name` = "Warranty Vault", `tom_lista` = "No purchases yet. Add your first receipt to keep track of warranties and return deadlines."
   - `res/values-sv/strings.xml`: `app_name` = "Garantivalvet", `tom_lista` = "Inga köp än. Lägg till ditt första kvitto för att hålla koll på garantier och reklamationsfrister."
   - Ingen behörighet i manifestet. Ingen `INTERNET`.
   - En enkel ikon (adaptiv, vektor) i primärfärgen. Det får vara en enkel geometrisk form, till exempel ett kvitto med en bock.
   - Ett test som kontrollerar att `strings.xml` och `values-sv/strings.xml` har samma nycklar (läs filerna som text från modulens källmapp i testet).
4. **Dokumentation:** skriv en rad per ny fil eller mapp i CLAUDE.md punkt 11 om den inte redan täcks, och inget annat i CLAUDE.md.

## Klart när

- `gradle testDebugUnitTest` och `gradle assembleDebug` går igenom i roten, och workflowet `Bygg` är grönt på din gren.
- Appen startar och visar toppfältet och texten för tomt läge, på svenska när telefonen är på svenska.
- `:karna` har inga beroenden till `:garantivalvet`.

## Gör inte

- Inte Room, WorkManager, Billing eller DataStore än. De kommer i egna uppdrag.
- Inga behörigheter, inget nätverk, inga analys- eller annons-SDK:er.
- Ändra inte `.github/workflows/`, `CLAUDE.md` (utom punkt 11), `STATUS.md`, `BESLUTSLOGG.md` eller `IDEURVAL.md`.
- Inga uppgifter i `uppgifter/` för det här uppdraget.
- Pusha inte till main. Arbeta bara på din egen gren.
