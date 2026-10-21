beslut: underkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- PR #477 ändrade enbart flytt av uppgiftsfilen från `uppgifter/ny/` till `uppgifter/pagar/`; varken `Nyckeltal.kt` eller `NyckeltalTest.kt` uppdaterades.
- Uppgiften krävde `justeratResultat` och `sparandePerKvm` plus JUnit-tester mot exemplen — saknas helt.
- Inga otillåtna filer eller fel sökväg; inga nya beroenden, behörigheter eller nätverk (ingen kod levererad).
- Befintliga `NyckeltalTest`-tester använder JUnit 4 (`org.junit`); oförändrade och fortsatt giltiga som baslinje.
- `:styrelserummet:testDebugUnitTest --tests "*NyckeltalTest"` grönt (ingen regression).

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

# Uppgift: Sparande per kvm

uppdrag: uppdrag/claude/2026-10-22-styrelserummet-nyckeltal.md
försök: 2
beror-på: 2026-10-22-styrelserummet-nyckeltal-02-skuld-f2.md

## Vad som saknades
Försök 1 ändrade bara uppgiftsfilens plats (`ny` → `pagar`). Varken `Nyckeltal.kt` eller `NyckeltalTest.kt` uppdaterades. Lägg till `justeratResultat` och `sparandePerKvm` enligt nedan och JUnit-tester som assertar alla tal i exemplen (inklusive negativt sparande och `null` vid noll totalyta).

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Nyckeltal.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/NyckeltalTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Nyckeltal.kt

## Uppgift
Lägg till i samma fil som övriga nyckeltalsfunktioner:

fun justeratResultat(u: NyckeltalUnderlag): Long
fun sparandePerKvm(u: NyckeltalUnderlag): Long?

- `justeratResultat(u)` = `u.aretsResultat + u.avskrivningar + u.utrangeringar + u.planeratUnderhall + u.justeringar` (`justeringar` har redan rätt tecken).
- `sparandePerKvm(u)` = `avrundaKvot(justeratResultat(u), totalYta(u))` när `totalYta(u) > 0`, annars `null` (anropa inte `avrundaKvot` med ogiltig nämnare). Resultatet kan bli negativt. Inga flyttal.

## Exempel
Samma underlag som uppgift 02: årsavgifter 4 200 000, skulder 38 000 000, resultat −350 000, avskrivningar 1 600 000, utrangeringar 0, planerat underhåll 450 000, justeringar 0, energi 1 150 000, rörelseintäkter 4 600 000, kvm bostadsrätt 5 200, kvm hyresrätt 300.
- `justeratResultat` → 1 700 000
- `sparandePerKvm` → 309

Samma underlag med `justeringar = -2_000_000`:
- `justeratResultat` → −300 000
- `sparandePerKvm` → −55 (−54,5 avrundas bort från noll)

Samma underlag med kvm bostadsrätt 0 och hyresrätt 0 → `sparandePerKvm` är `null`.

## Test som ska gå igenom
gradle :styrelserummet:testDebugUnitTest --tests "*NyckeltalTest"

Lägg till minst ett `@Test` (JUnit 4: `org.junit.Test`, `org.junit.Assert.assertEquals` / `assertNull`) som bygger `NyckeltalUnderlag` enligt exemplen och assertar `justeratResultat`, `sparandePerKvm`, justeringsfallet och `null` vid noll kvm. Befintliga tester ska fortsätta gå. Använd `Long`-literaler i `assertEquals` där värdet är `Long`.

## Gör inte
- Ändra andra filer eller befintliga funktioner (`avrundaKvot`, skuldsättning m.m.).
- Använda flyttal eller `kotlin.test`.

## Frågor till Claude
- inga
