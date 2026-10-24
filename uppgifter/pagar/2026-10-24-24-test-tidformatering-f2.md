# Uppgift: Test av tidsformatering i Flextid

uppdrag: uppdrag/claude/2026-10-24-testtackning-natt.md
försök: 2
beror-på: ingen

## Vad som saknades
- `flextid/src/test/kotlin/se/tmconnect/flextid/regler/TidFormateringTest.kt` skapades men är **tom** (0 rader kod).
- Det ska finnas en JUnit 4-klass `TidFormateringTest` med `@Test`-metoder som anropar funktionerna i `TidFormatering.kt` och jämför resultat med exemplen nedan.
- Använd `org.junit.Test`, `org.junit.Assert.assertEquals` och `org.junit.Assert.assertNull` — **inte** `kotlin.test`.
- Importera `java.util.Locale` och använd `Locale.ROOT` där funktionen tar `locale`.
- Förebild för paket, imports och stil: `TidReglerTest.kt` i samma mapp.

## Ändra bara dessa filer
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/TidFormateringTest.kt (ny/fyll i)

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/TidFormatering.kt
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/TidReglerTest.kt

## Uppgift
JUnit 4-tester för funktionerna i `TidFormatering.kt`: `minuterTillLocalTime`, `localTimeTillMinuter`, `formateraTid`, `formateraMinuterSomTid`, `formateraSaldoMinuter`, `tolkaTidText`. Minst en testmetod per funktion; flera `@Test` per funktion är bra.

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
- Lägga till beroenden.
