# Uppdrag: fel från Mattias prov på telefon (Garantivalvet)

Läs CLAUDE.md och ARBETSFLODE.md först och följ dem. Ändra inget utöver uppdraget. Hela uppdraget görs av Cursor i en PR (få filer, rör `MainActivity` och Pro-logiken). Inga uppgifter till den lokala modellen.

## Bakgrund

Mattias provade APK:n från `senaste-main` 2026-10-12. På detaljsidan gör "Exportera PDF" och "Dela" ingenting. Orsak: utan Pro anropar knapparna `onVisaProDialog`, men Pro-dialogen ritas bara i `KopSkarmar.kt` (listan). På detaljsidan sätts tillståndet utan att något visas. Dessutom kan Pro inte köpas förrän appen ligger i ett testspår i Play Console, så PDF och säkerhetskopia går inte att prova alls.

## Uppgift

1. **Pro-dialogen på alla skärmar.** Flytta dialogen ut ur `KopSkarmar.kt` så att den ritas en gång på toppnivå (i `MainActivity` eller en egen composable där), oberoende av vilken skärm som visas. Listan, detaljsidan och säkerhetskopian använder samma dialog.
2. **Testläge för Pro i debug-bygget.** I debug-bygget ska Pro kunna slås på utan Play: en `ProKalla` för debug som läser en flagga i DataStore, och en rad "Testläge: Pro" (sv) / "Test mode: Pro" (en) med en switch som bara visas när `BuildConfig.DEBUG` är sant. Release-bygget använder alltid `PlayProKalla` och har ingen sådan rad. Slå på `buildConfig` i `:garantivalvet` om det behövs.
3. **Kontrollera hela exportflödet med Pro på** i ett instrumenterat eller Robolectric-test där det går: spara PDF via dokumentväljaren, dela PDF via `FileProvider` (intent med rätt MIME-typ och läsrättighet), spara och hämta säkerhetskopia. Om dela-flödet inte går att testa automatiskt, beskriv i PR:en vad som kontrollerats för hand i koden (`FileProvider`-authority i manifestet stämmer med koden, sökvägen finns i `file_paths.xml`).

## Klart när

- `gradle testDebugUnitTest` och `gradle assembleDebug` är gröna.
- Utan Pro visar PDF och Dela på detaljsidan Pro-dialogen.
- I debug-APK:n kan Pro slås på med switchen, och då fungerar PDF, Dela och säkerhetskopia.
- Release-varianten innehåller inte testläget (kontrollera med ett test eller genom att visa att koden ligger i `src/debug/`).

## Gör inte

- Ändra databasschema, behörigheter eller lägga till beroenden.
- Ändra utseendet i övrigt. Det görs i uppdrag `2026-10-12-ny-design`.
- Ändra `.github/workflows/`, `verktyg/`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md` eller `release.json`.
