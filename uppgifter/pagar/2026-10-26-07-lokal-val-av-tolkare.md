# Uppgift: Val av tolkare och reserv

uppdrag: uppdrag/vantar/2026-10-25-rostfangst-lokal-ai.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/lokal/ValAvTolkare.kt (ny)
- rostfangst/src/test/kotlin/se/tmconnect/rostfangst/lokal/ValAvTolkareTest.kt (ny)

## Läs som förebild (ändra inte)
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/wear/UtforMeddelande.kt
- rostfangst/src/test/kotlin/se/tmconnect/rostfangst/wear/UtforMeddelandeTest.kt

## Uppgift
Paket `se.tmconnect.rostfangst.lokal`. Ren Kotlin, ingen `android.*`. JUnit 4, utan Robolectric.

```kotlin
enum class TolkningsLage { ANTHROPIC, MIN_DATOR, MIN_DATOR_ANNARS_ANTHROPIC }
enum class LokalFel { NATFEL, TIMEOUT, OGILTIGT_SVAR }
sealed interface Val {
    object AnvandAnthropic : Val
    object AnvandDatorn : Val
    data class Fel(val fel: LokalFel) : Val
}
fun valjTolkning(lage: TolkningsLage, lokalFel: LokalFel?): Val
```

`lokalFel` är `null` när datorn ännu inte har provats. Regler:
- `ANTHROPIC` → `AnvandAnthropic`, oavsett `lokalFel`.
- `MIN_DATOR`: `null` → `AnvandDatorn`; ett fel → `Fel(fel)`.
- `MIN_DATOR_ANNARS_ANTHROPIC`: `null` → `AnvandDatorn`; `NATFEL` eller `TIMEOUT` → `AnvandAnthropic`; `OGILTIGT_SVAR` → `Fel(OGILTIGT_SVAR)` (ingen reserv vid ogiltigt svar).

## Exempel
- `valjTolkning(MIN_DATOR_ANNARS_ANTHROPIC, TIMEOUT)` → `AnvandAnthropic`
- `valjTolkning(MIN_DATOR_ANNARS_ANTHROPIC, OGILTIGT_SVAR)` → `Fel(OGILTIGT_SVAR)`
- `valjTolkning(MIN_DATOR, NATFEL)` → `Fel(NATFEL)`
- `valjTolkning(MIN_DATOR, null)` → `AnvandDatorn`
- `valjTolkning(ANTHROPIC, NATFEL)` → `AnvandAnthropic`
Testa alla 3 × 4 = 12 kombinationer.

## Test som ska gå igenom
gradle :rostfangst:testDebugUnitTest --tests "*ValAvTolkareTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
