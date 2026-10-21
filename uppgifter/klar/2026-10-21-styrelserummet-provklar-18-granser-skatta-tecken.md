# Uppgift: Skatta tecken för tolkning

uppdrag: uppdrag/2026-10-21-styrelserummet-provklar.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Granser.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/GranserTest.kt (ny)

## Uppgift
1. Lägg till `const val MAX_TECKEN_TILL_TOLKNING = 400_000` (tecken per jobb, summa av extraherad plan+årsredovisningstext som skickas till tolkning).
2. `fun skattaTecken(sidor: List<String>): Int` — summera längd på alla strängar i listan (planens sidor/delar som ska tolkas; enkel summa av `length`).

Test:
- Tom lista → 0.
- Tre strängar med längd 100, 200, 50 → 350.
- Lista vars summa > 400_000 ska användas i nästa uppgift (här bara test av `skattaTecken`).

## Exempel
- `skattaTecken(listOf("a".repeat(200_000), "b".repeat(200_001)))` → 400_001.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*GranserTest*"

## Gör inte
- Koppla till JobbKo i denna uppgift.
- Ändra andra filer.

utfall: godkänd 2026-10-21 (JUnit 4-import i GranserTest)
