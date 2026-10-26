# Uppgift: Inspelningens längd

uppdrag: uppdrag/vantar/2026-10-25-rostfangst-lokal-ai.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/lokal/Inspelningslangd.kt (ny)
- rostfangst/src/test/kotlin/se/tmconnect/rostfangst/lokal/InspelningslangdTest.kt (ny)

## Läs som förebild (ändra inte)
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/wear/UtforMeddelande.kt
- rostfangst/src/test/kotlin/se/tmconnect/rostfangst/wear/UtforMeddelandeTest.kt

## Uppgift
Paket `se.tmconnect.rostfangst.lokal`. Ren Kotlin. JUnit 4, utan Robolectric.

1. `fun inspelningenSlut(ms: Long, maxMs: Long): Boolean` — `true` när `ms >= maxMs`.
2. `fun formateraLangd(ms: Long): String` — `M:SS`: hela sekunder avrundade nedåt, minuter utan inledande nolla, sekunder alltid med två siffror. Negativt värde ger `0:00`.

## Exempel
- `inspelningenSlut(59_999, 60_000)` → `false`; `(60_000, 60_000)` → `true`; `(61_000, 60_000)` → `true`
- `formateraLangd(0)` → `0:00`
- `formateraLangd(7_400)` → `0:07`
- `formateraLangd(59_999)` → `0:59`
- `formateraLangd(60_000)` → `1:00`
- `formateraLangd(125_000)` → `2:05`
- `formateraLangd(-5)` → `0:00`

## Test som ska gå igenom
gradle :rostfangst:testDebugUnitTest --tests "*InspelningslangdTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
