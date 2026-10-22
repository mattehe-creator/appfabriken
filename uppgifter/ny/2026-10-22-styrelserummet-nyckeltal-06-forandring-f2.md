# Uppgift: Förändring mot föregående år

uppdrag: uppdrag/claude/2026-10-22-styrelserummet-nyckeltal.md
försök: 2
beror-på: ingen

## Vad som saknades
Försök 1 ändrade bara uppgiftsfilens plats (`ny` → `pagar`). Varken `NyckeltalForandring.kt` eller `NyckeltalForandringTest.kt` skapades. Implementera båda funktionerna nedan och JUnit 4-tester som assertar alla rader i exemplen (inklusive `null` och `"–"`).

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/NyckeltalForandring.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/NyckeltalForandringTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Nyckeltal.kt

## Uppgift
fun forandringTiondelsProcent(nu: Long?, foregaende: Long?): Long?
fun formateraForandring(tiondelar: Long?): String

- `forandringTiondelsProcent`: förändringen i tiondels procent, `avrundaKvot((nu - foregaende) * 1000, foregaende)`. `null` om något värde är `null` eller om `foregaende <= 0`.
- `formateraForandring`: `null` → `"–"` (Unicode en-dash U+2013, samma som i `Formatering.kt` för `procent`). Annars tecken (`+` för positivt, `−` för negativt med Unicode minus U+2212, inget tecken för 0), heltalsdel, decimalkomma, en decimal (sista siffra i `tiondelar`) och `" %"`.

Tips för formatering utan flyttal: `tiondelar` är t.ex. 104 för +10,4 % — heltal = `tiondelar / 10`, decimal = `kotlin.math.abs(tiondelar % 10)`.

## Exempel
- `forandringTiondelsProcent(309, 280)` → 104; `formateraForandring(104)` → "+10,4 %"
- `forandringTiondelsProcent(90, 95)` → −53; `formateraForandring(-53)` → "−5,3 %"
- `forandringTiondelsProcent(808, 808)` → 0; `formateraForandring(0)` → "0,0 %"
- `forandringTiondelsProcent(100, 0)` → null; `formateraForandring(null)` → "–"
- `forandringTiondelsProcent(null, 5)` → null

## Test som ska gå igenom
gradle :styrelserummet:testDebugUnitTest --tests "*NyckeltalForandringTest"

Skriv testklassen med `import org.junit.Test` och `import org.junit.Assert.assertEquals` (inte `kotlin.test`). Minst ett `@Test` som assertar både returvärden och formaterade strängar för alla fem exemplen ovan.

## Gör inte
- Ändra andra filer.
- Använda flyttal.
- Lägga till beroenden.
