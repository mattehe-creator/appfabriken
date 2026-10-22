# Uppgift: Jämn årlig avsättning

uppdrag: uppdrag/claude/2026-10-21-styrelserummet-fondplan.md
försök: 1
beror-på: 2026-10-21-styrelserummet-fond-01-per-ar.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Fondplan.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/FondplanTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivning.kt

## Uppgift
Lägg till:

```kotlin
fun jamnAvsattning(atgarder: List<PlaneradAtgard>, franAr: Int, antalAr: Int, ingaendeFond: Long): Long
```

`behov = summaPerAr(atgarder, franAr, antalAr).sum() - ingaendeFond`. Är `behov <= 0` returneras 0. Annars avrundas uppåt till hel krona: `(behov + antalAr - 1) / antalAr`.

## Exempel
Samma åtgärder som i uppgift 01:
- franAr 2026, antalAr 5, fond 300 000 → 180 000
- franAr 2026, antalAr 5, fond 0 → 240 000
- franAr 2026, antalAr 3, fond 0 → 333 334
- franAr 2026, antalAr 5, fond 2 000 000 → 0

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*FondplanTest"

## Gör inte
- Ändra andra filer eller `summaPerAr`.
- Använda flyttal.

utfall: godkänd 2026-10-21 (inget)
