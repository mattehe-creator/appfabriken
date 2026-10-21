# Uppgift: Underskott och minsta avsättning

uppdrag: uppdrag/claude/2026-10-21-styrelserummet-fondplan.md
försök: 1
beror-på: 2026-10-21-styrelserummet-fond-03-utveckling.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Fondplan.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/FondplanTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Fondplan.kt

## Uppgift
Lägg till:

```kotlin
fun forstaUnderskottsar(utveckling: List<FondAr>): Int?
fun minstaAvsattningUtanUnderskott(atgarder: List<PlaneradAtgard>, franAr: Int, antalAr: Int, ingaendeFond: Long): Long
```

- `forstaUnderskottsar`: året för första raden där `utgaende < 0`, annars `null`.
- `minstaAvsattningUtanUnderskott`: den minsta årliga avsättningen så att utgående aldrig blir negativt. Räkna den kumulativa summan av uttagen år för år, `kum[k]` för `k = 1..antalAr`. Kravet för år k är `(kum[k] - ingaendeFond)` delat med `k`, avrundat uppåt (bara när täljaren är positiv, annars 0). Svaret är det största kravet.

## Exempel
Samma åtgärder, franAr 2026, antalAr 5:
- `forstaUnderskottsar(fondUtveckling(..., 180 000, 300 000))` → 2028
- `forstaUnderskottsar(fondUtveckling(..., 240 000, 300 000))` → null
- `minstaAvsattningUtanUnderskott(..., ingaendeFond = 300 000)` → 233 334
- Kontroll: `fondUtveckling(..., 233 334, 300 000)` har inget negativt utgående, och med 233 333 blir 2028 negativt.
- `minstaAvsattningUtanUnderskott(..., ingaendeFond = 5 000 000)` → 0

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*FondplanTest"

## Gör inte
- Ändra andra filer eller tidigare funktioner.
- Använda flyttal.
