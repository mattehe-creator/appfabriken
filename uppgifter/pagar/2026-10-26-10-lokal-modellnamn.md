# Uppgift: Giltigt modellnamn

uppdrag: uppdrag/vantar/2026-10-25-rostfangst-lokal-ai.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/lokal/Modellnamn.kt (ny)
- rostfangst/src/test/kotlin/se/tmconnect/rostfangst/lokal/ModellnamnTest.kt (ny)

## Läs som förebild (ändra inte)
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/wear/UtforMeddelande.kt
- rostfangst/src/test/kotlin/se/tmconnect/rostfangst/wear/UtforMeddelandeTest.kt

## Uppgift
Paket `se.tmconnect.rostfangst.lokal`. Ren Kotlin. JUnit 4, utan Robolectric.

`fun giltigtModellnamn(text: String): Boolean`

Texten trimmas först. Den är giltig när den inte är tom, är högst 100 tecken och bara består av bokstäverna a–z och A–Z, siffror samt tecknen `.`, `_`, `-`, `:` och `/`. Allt annat (till exempel mellanslag mitt i, `@`, `å`) är ogiltigt.

## Exempel
- `qwen3:8b` → `true`
- `llama3.1:8b-instruct-q4_K_M` → `true`
- `hf.co/user/model:Q4_K_M` → `true`
- `  qwen3:8b  ` → `true` (trimmas)
- tom sträng och `   ` → `false`
- `qwen 3` → `false`
- `modell@1` → `false`
- `mödell` → `false`
- 100 gånger `a` → `true`; 101 gånger `a` → `false`

## Test som ska gå igenom
gradle :rostfangst:testDebugUnitTest --tests "*ModellnamnTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
