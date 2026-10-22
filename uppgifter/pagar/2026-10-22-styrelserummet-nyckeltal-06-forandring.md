# Uppgift: Förändring mot föregående år

uppdrag: uppdrag/claude/2026-10-22-styrelserummet-nyckeltal.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/NyckeltalForandring.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/NyckeltalForandringTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Nyckeltal.kt

## Uppgift
```kotlin
fun forandringTiondelsProcent(nu: Long?, foregaende: Long?): Long?
fun formateraForandring(tiondelar: Long?): String
```

- `forandringTiondelsProcent`: förändringen i tiondels procent, `avrundaKvot((nu - foregaende) * 1000, foregaende)`. `null` om något värde är `null` eller om `foregaende <= 0`.
- `formateraForandring`: `null` → `"–"`. Annars tecken (`+` för positivt, `−` för negativt, inget för 0), heltalsdel, decimalkomma, en decimal och `" %"`.

## Exempel
- (309, 280) → 104 → "+10,4 %"
- (90, 95) → −53 → "−5,3 %"
- (808, 808) → 0 → "0,0 %"
- (100, 0) → null → "–"
- (null, 5) → null

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*NyckeltalForandringTest"

## Gör inte
- Ändra andra filer.
- Använda flyttal.
