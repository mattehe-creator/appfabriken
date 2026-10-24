# Uppgift: Test av tidsformatering i Flextid

uppdrag: uppdrag/claude/2026-10-24-testtackning-natt.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/TidFormateringTest.kt (ny)

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/TidFormatering.kt
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/TidReglerTest.kt

## Uppgift
JUnit 4-tester för funktionerna i `TidFormatering.kt`. Använd `Locale.ROOT` där funktionen tar en locale.

## Exempel
- `minuterTillLocalTime(510)` → `LocalTime.of(8, 30)`
- `localTimeTillMinuter(LocalTime.of(16, 45))` → `1005`
- `formateraTid(null, Locale.ROOT)` → `""`
- `formateraTid(LocalTime.of(8, 5), Locale.ROOT)` → `"08:05"`
- `formateraMinuterSomTid(-75, Locale.ROOT)` → `"-1:15"`
- `formateraMinuterSomTid(0, Locale.ROOT)` → `"0:00"`
- `formateraMinuterSomTid(605, Locale.ROOT)` → `"10:05"`
- `formateraSaldoMinuter(75, Locale.ROOT)` → `"+1:15"`
- `formateraSaldoMinuter(-75, Locale.ROOT)` → `"-1:15"`
- `formateraSaldoMinuter(0, Locale.ROOT)` → `"0:00"`
- `tolkaTidText("8:05")` → `LocalTime.of(8, 5)`
- `tolkaTidText(" 07:30 ")` → `LocalTime.of(7, 30)`
- `tolkaTidText("24:00")` → `null`
- `tolkaTidText("8")` → `null`
- `tolkaTidText("")` → `null`

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*TidFormateringTest"

## Gör inte
- Ändra andra filer.
