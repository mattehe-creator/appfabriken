# Uppgift: Årsavgift och skuldsättning per kvm

uppdrag: uppdrag/claude/2026-10-22-styrelserummet-nyckeltal.md
försök: 2
beror-på: 2026-10-22-styrelserummet-nyckeltal-01-avrunda.md

## Vad som saknades
Försök 1 ändrade bara uppgiftsfilens plats (`ny` → `pagar`). Varken `Nyckeltal.kt` eller `NyckeltalTest.kt` uppdaterades. Lägg till de fyra funktionerna nedan och JUnit-tester som assertar mot alla tal i exemplen (inklusive `null` vid noll kvm).

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Nyckeltal.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/NyckeltalTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Nyckeltal.kt

## Uppgift
Lägg till i samma fil som `avrundaKvot`, alla med `avrundaKvot` där det passar:

fun totalYta(u: NyckeltalUnderlag): Long = u.kvmBostadsratt + u.kvmHyresratt
fun arsavgiftPerKvmBostadsratt(u: NyckeltalUnderlag): Long?   // arsavgifter / kvmBostadsratt
fun skuldsattningPerKvm(u: NyckeltalUnderlag): Long?          // rantebarandeSkulder / totalYta
fun skuldsattningPerKvmBostadsratt(u: NyckeltalUnderlag): Long? // rantebarandeSkulder / kvmBostadsratt

Är nämnaren 0 eller mindre returneras `null` (anropa inte `avrundaKvot` — den kastar undantag vid ogiltig nämnare). Inga flyttal.

## Exempel
Underlag: årsavgifter 4 200 000, skulder 38 000 000, resultat −350 000, avskrivningar 1 600 000, utrangeringar 0, planerat underhåll 450 000, justeringar 0, energi 1 150 000, rörelseintäkter 4 600 000, kvm bostadsrätt 5 200, kvm hyresrätt 300.
- `totalYta` → 5 500
- `arsavgiftPerKvmBostadsratt` → 808
- `skuldsattningPerKvm` → 6 909
- `skuldsattningPerKvmBostadsratt` → 7 308
- Samma underlag med kvm bostadsrätt 0 och hyresrätt 0 → `arsavgiftPerKvmBostadsratt`, `skuldsattningPerKvm` och `skuldsattningPerKvmBostadsratt` är alla `null` (`totalYta` blir 0).

## Test som ska gå igenom
gradle :styrelserummet:testDebugUnitTest --tests "*NyckeltalTest"

Lägg till minst ett `@Test` (JUnit 4: `org.junit.Test`, `org.junit.Assert`) som bygger ett `NyckeltalUnderlag` enligt exemplet ovan och assertar `totalYta`, de tre kvoterna och `null` för noll kvm. Befintliga `avrundaKvot`-tester ska fortsatt gå.

## Gör inte
- Ändra andra filer eller `avrundaKvot`.
- Använda flyttal eller `kotlin.test`.
