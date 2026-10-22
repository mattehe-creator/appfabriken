# Uppgift: Alla nyckeltal med etiketter

uppdrag: uppdrag/claude/2026-10-22-styrelserummet-nyckeltal.md
försök: 2
beror-på: 2026-10-22-styrelserummet-nyckeltal-04-ranta-energi-f2.md

## Vad som saknades
Försök 1 ändrade bara uppgiftsfilens plats (`ny` → `pagar`). Skapa båda filerna nedan: `NyckeltalRader.kt` med `nyckeltalRader` och `NyckeltalRaderTest.kt` med JUnit 4-tester som jämför hela listan mot strängarna i exemplen (inklusive hårt mellanslag i tusental och `–` vid `null`).

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/NyckeltalRader.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/NyckeltalRaderTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Nyckeltal.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Formatering.kt

## Uppgift

data class NyckeltalRad(val etikett: String, val varde: String)
fun nyckeltalRader(u: NyckeltalUnderlag): List<NyckeltalRad>

Sju rader i denna ordning. När underliggande nyckeltalsfunktion ger `null`, visa värdet som `"–"` (Unicode `\u2013`, samma som i `Formatering.kt`). Tusental grupperas med hårt mellanslag (`\u00A0`) som i `kronor`; minus som `−` (`\u2212`).

1. "Årsavgift per kvm upplåten med bostadsrätt" → `arsavgiftPerKvmBostadsratt(u)` formaterat som kr/kvm (t.ex. `"808 kr/kvm"`)
2. "Skuldsättning per kvm" → `skuldsattningPerKvm(u)` → `"6\u00A0909 kr/kvm"`
3. "Skuldsättning per kvm upplåten med bostadsrätt" → `skuldsattningPerKvmBostadsratt(u)` → `"7\u00A0308 kr/kvm"`
4. "Sparande per kvm" → `sparandePerKvm(u)` → `"309 kr/kvm"`
5. "Räntekänslighet" → `rantekanslighetTiondelar(u)` som tiondelar med decimalkomma, t.ex. `"9,0"` (internt 90)
6. "Energikostnad per kvm" → `energikostnadPerKvm(u)` → `"209 kr/kvm"`
7. "Årsavgifternas andel av totala rörelseintäkter" → `arsavgiftsandelTiondelsProcent(u)` som procent med en decimal, t.ex. `"91,3 %"` (internt 913)

Tips:
- Kr/kvm: `kronor(x)` ger `"808 kr"`; byt suffix `" kr"` mot `" kr/kvm"`. Gäller inte när värdet är `null`.
- Tiondelar `t` (heltal, kan vara negativt): tecken `−` om `t < 0`, annars tomt; absolutvärde `a = abs(t)`; text `"${a / 10},${a % 10}"` med komma (en decimal).
- Tiondels procent `p` (913 = 91,3 %): samma som tiondelar men avsluta med `" %"` → `"${a / 10},${a % 10} %"`.

Anropa befintliga funktioner i `Nyckeltal.kt`; inga flyttal.

## Exempel
Samma underlag som i `NyckeltalTest` (årsavgifter 4_200_000, räntebärande skulder 38_000_000, resultat −350_000, avskrivningar 1_600_000, utrangeringar 0, planerat underhåll 450_000, justeringar 0, energi 1_150_000, rörelseintäkter 4_600_000, kvm bostadsrätt 5_200, kvm hyresrätt 300) → sju rader med värdena ovan.

Underlag med `kvmBostadsratt = 0`, `kvmHyresratt = 0`, `arsavgifter = 0`, `rorelseintakter = 0` (övriga fält valfria) → alla sju värden `"–"`.

## Test som ska gå igenom
gradle :styrelserummet:testDebugUnitTest --tests "*NyckeltalRaderTest"

Minst två `@Test` (JUnit 4: `org.junit.Test`, `org.junit.Assert.assertEquals`):
- Ett test bygger basunderlaget och assertar att `nyckeltalRader(u)` har exakt sju rader med rätt etiketter och exakta `varde`-strängar (inklusive `\u00A0` i skuldsättning).
- Ett test med noll-yta/noll-intäkter assertar att alla värden är `"–"`.

## Gör inte
- Ändra andra filer.
- Använda flyttal eller `kotlin.test`.

utfall: godkänd 2026-10-22 (hela NyckeltalRader.kt och NyckeltalRaderTest.kt; tomma filer från lokal modell försök 2)
