beslut: underkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Grenen innehåller endast flytt av uppgiftsfilen från `uppgifter/ny/` till `uppgifter/pagar/`; inga ändringar i `Fondplan.kt` eller `FondplanTest.kt`.
- Funktionerna `forstaUnderskottsar` och `minstaAvsattningUtanUnderskott` saknas helt trots uppgiften.
- Inga tester för underskott eller minsta avsättning; befintliga tester rör tidigare uppgifter.
- Inga filer utanför listan eller i fel sökväg; inga nya beroenden, behörigheter eller nätverk.
- Ingen UI-kod; `FRAGA.md` fanns inte på grenen.
- Bygget är grönt eftersom ingen ny kod tillkom (befintliga tester fortsätter gå).

## Rättat av Cursor
- inget (saknad implementation är inte ett småfel)

## Vid underkänd: försök 2

# Uppgift: Underskott och minsta avsättning

uppdrag: uppdrag/claude/2026-10-21-styrelserummet-fondplan.md
försök: 2
beror-på: 2026-10-21-styrelserummet-fond-03-utveckling-f2.md

## Vad som saknades
Försök 1 flyttade bara uppgiftsfilen till `pagar/` och ändrade ingen Kotlin-kod. Lägg till båda funktionerna i `Fondplan.kt` och JUnit 4-tester i `FondplanTest.kt` som jämför resultat mot alla exempel nedan. Använd `exempelAtgarder()` som redan finns i testfilen; `franAr = 2026`, `antalAr = 5`.

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Fondplan.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/FondplanTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Fondplan.kt

## Uppgift
Lägg till:

    fun forstaUnderskottsar(utveckling: List<FondAr>): Int?
    fun minstaAvsattningUtanUnderskott(atgarder: List<PlaneradAtgard>, franAr: Int, antalAr: Int, ingaendeFond: Long): Long

- `forstaUnderskottsar`: returnera `ar` för första raden i `utveckling` där `utgaende < 0`, annars `null`.
- `minstaAvsattningUtanUnderskott`: minsta årliga avsättning så att utgående aldrig blir negativt vid samma uttag som `summaPerAr` ger. Bygg kumulativ uttagssumma `kum[k]` för år `k = 1..antalAr` (första året i planen = index 0 i listan). För varje `k` (1-baserat): om `kum[k] > ingaendeFond`, krav = `(kum[k] - ingaendeFond + k - 1) / k` (heltalsavrundning uppåt, samma mönster som `jamnAvsattning`); annars krav = 0. Svaret är maximum av alla krav. Använd inte flyttal.

Tips: `minstaAvsattningUtanUnderskott` behöver inte anropa `fondUtveckling`; den räknar bara på uttagslistan och ingående fond.

## Exempel
Samma åtgärder som `exempelAtgarder()`, `franAr` 2026, `antalAr` 5:
- `forstaUnderskottsar(fondUtveckling(atgarder, 2026, 5, 180_000, 300_000))` → `2028`
- `forstaUnderskottsar(fondUtveckling(atgarder, 2026, 5, 240_000, 300_000))` → `null`
- `minstaAvsattningUtanUnderskott(atgarder, 2026, 5, 300_000)` → `233_334`
- Kontroll i test: `fondUtveckling(..., 233_334, 300_000)` har inget negativt `utgaende`, och med `233_333` blir år 2028 negativt (`forstaUnderskottsar` → `2028`).
- `minstaAvsattningUtanUnderskott(atgarder, 2026, 5, 5_000_000)` → `0`

## Test som ska gå igenom
gradle :styrelserummet:testDebugUnitTest --tests "*FondplanTest"

Lägg minst två `@Test` med `org.junit.Test` och `org.junit.Assert.assertEquals` (eller `assertNull`) som täcker exemplen ovan, inklusive kontrollen med 233_334 respektive 233_333.

## Gör inte
- Ändra andra filer eller signaturer på befintliga funktioner (`summaPerAr`, `jamnAvsattning`, `fondUtveckling`).
- Använda flyttal eller `kotlin.test`.

## Frågor till Claude
- inga
