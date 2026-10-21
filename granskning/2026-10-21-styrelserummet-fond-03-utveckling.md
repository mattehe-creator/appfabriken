beslut: underkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Diff mot `main`: endast flytt av uppgiftsfil `uppgifter/ny/` → `uppgifter/pagar/`; commit «Uppgift startad» — ingen kod i `Fondplan.kt` eller `FondplanTest.kt`.
- `FondAr` och `fondUtveckling` saknas i `Fondplan.kt`; befintliga `summaPerAr` och `jamnAvsattning` oförändrade (OK enligt «Gör inte», men uppgiftens nya API implementerades inte).
- Inga tester som jämför fondutveckling mot uppgiftens fem exempelrader (2026–2030).
- Endast tillåtna filer i diff; inga filer i repots rot, inga nya beroenden, behörigheter eller nätverk.
- Ingen `FRAGA.md` på grenen.
- JUnit 4 i befintliga `FondplanTest`-metoder; ingen UI-kod.
- `./gradlew :styrelserummet:testDebugUnitTest --tests "*FondplanTest"` grönt (befintliga tester).

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

# Uppgift: Fondens utveckling år för år

uppdrag: uppdrag/claude/2026-10-21-styrelserummet-fondplan.md
försök: 2
beror-på: 2026-10-21-styrelserummet-fond-02-jamn.md

## Vad som saknades
- **`Fondplan.kt` ändrades inte.** Lägg till `data class FondAr(val ar: Int, val avsattning: Long, val uttag: Long, val utgaende: Long)` och funktionen `fondUtveckling(...)` längst ned i samma fil. Anropa befintliga `summaPerAr(atgarder, franAr, antalAr)` för årets `uttag`. För varje år i intervallet `franAr .. franAr + antalAr - 1`: `utgaende` = föregående utgående + `arligAvsattning` − `uttag` (första året är föregående = `ingaendeFond`). `avsattning` på varje rad är `arligAvsattning`. Om `arligAvsattning < 0`, kasta `IllegalArgumentException`. Rör inte `summaPerAr` eller `jamnAvsattning`.
- **`FondplanTest.kt` utökades inte.** Lägg till minst ett test (JUnit 4, `org.junit`) som anropar `fondUtveckling` med samma åtgärdslista som i `test jamnAvsattning`, `franAr` 2026, `antalAr` 5, `arligAvsattning` 180_000, `ingaendeFond` 300_000, och jämför hela listan mot uppgiftens fem rader (år, avsättning, uttag, utgående). Lägg gärna ett test som förväntar `IllegalArgumentException` vid negativ avsättning.

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Fondplan.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/FondplanTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivningsplan.kt

## Uppgift
Lägg till:

```kotlin
data class FondAr(val ar: Int, val avsattning: Long, val uttag: Long, val utgaende: Long)

fun fondUtveckling(
    atgarder: List<PlaneradAtgard>,
    franAr: Int,
    antalAr: Int,
    arligAvsattning: Long,
    ingaendeFond: Long,
): List<FondAr>
```

En rad per år. `uttag` = årets summa från `summaPerAr`. `utgaende` = föregående års utgående (första året `ingaendeFond`) plus `arligAvsattning` minus `uttag`. Utgående får bli negativt. Negativ `arligAvsattning` ger `IllegalArgumentException`.

## Exempel
Samma åtgärder, franAr 2026, antalAr 5, avsättning 180 000, fond 300 000:
- (2026, 180 000, 100 000, 380 000)
- (2027, 180 000, 0, 560 000)
- (2028, 180 000, 900 000, −160 000)
- (2029, 180 000, 0, 20 000)
- (2030, 180 000, 200 000, 0)

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*FondplanTest"

## Gör inte
- Ändra andra filer eller tidigare funktioner.
- Använda flyttal.

## Frågor till Claude
- inga
