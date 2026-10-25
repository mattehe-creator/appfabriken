# Uppgift: Test av normalisering av sparade inställningar i Flextid

uppdrag: uppdrag/claude/2026-10-24-testtackning-natt.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/NormaliseringTest.kt (ny)

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/VanligDagForslag.kt
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/StamplaInPaminnelse.kt
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/Arbetsdagar.kt

## Uppgift
JUnit 4-tester för `normaliseraVanligStartMin`, `normaliseraVanligSlutMin`, `normaliseraVanligRastMin`, `arGiltigtStamplaInPaminnelseMinEfterMidnatt` och `veckodagBit`.

## Exempel
- `normaliseraVanligStartMin(null)` → `480`
- `normaliseraVanligStartMin(-5)` → `0`
- `normaliseraVanligStartMin(2000)` → `1439`
- `normaliseraVanligStartMin(450)` → `450`
- `normaliseraVanligSlutMin(null)` → `990`
- `normaliseraVanligRastMin(null)` → `30`
- `normaliseraVanligRastMin(-10)` → `0`
- `normaliseraVanligRastMin(45)` → `45`
- `arGiltigtStamplaInPaminnelseMinEfterMidnatt(300)` → `true` (05:00)
- `arGiltigtStamplaInPaminnelseMinEfterMidnatt(720)` → `true` (12:00)
- `arGiltigtStamplaInPaminnelseMinEfterMidnatt(750)` → `false`
- `arGiltigtStamplaInPaminnelseMinEfterMidnatt(299)` → `false`
- `arGiltigtStamplaInPaminnelseMinEfterMidnatt(315)` → `false` (inte hel eller halv timme)
- `veckodagBit(DayOfWeek.MONDAY)` → `1`
- `veckodagBit(DayOfWeek.SUNDAY)` → `64`

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*NormaliseringTest"

## Gör inte
- Ändra andra filer.

utfall: underkänd 2026-10-24 av Cursor (PR #665). Ersatt av 2026-10-24-25-test-normalisering-f2.md
