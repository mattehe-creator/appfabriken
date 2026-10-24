# Uppgift: Test av datum utan år (sommartid) i Flextid

## Vad som saknades
- Filen `DagManadTest.kt` fanns men var helt tom. Du ska skriva JUnit 4-tester i den filen enligt uppgiften och exemplen nedan.

uppdrag: uppdrag/claude/2026-10-24-testtackning-natt.md
försök: 2
beror-på: ingen

## Ändra bara dessa filer
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/DagManadTest.kt (ny)

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/Arbetstid.kt
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/ArbetstidTest.kt

## Uppgift
JUnit 4-tester för `tolkaManadDag(text: String?): MonthDay?` och `formateraDagManad(md: MonthDay): String`, och att de fungerar ihop med `tolkaDagManadText`.

Använd `org.junit.Test` och `org.junit.Assert` (t.ex. `assertEquals`, `assertNull`) — inte `kotlin.test`.

## Exempel
- `tolkaManadDag("--06-01")` → `MonthDay.of(6, 1)`
- `tolkaManadDag(" --08-31 ")` → `MonthDay.of(8, 31)`
- `tolkaManadDag("1/6")` → `null`
- `tolkaManadDag(null)` → `null`
- `formateraDagManad(MonthDay.of(8, 31))` → `"31/8"`
- `formateraDagManad(MonthDay.of(1, 5))` → `"5/1"`
- För `MonthDay.of(6, 1)`, `MonthDay.of(2, 29)` och `MonthDay.of(12, 31)`: `tolkaDagManadText(formateraDagManad(md))` → `md`

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*DagManadTest"

## Gör inte
- Ändra andra filer.
