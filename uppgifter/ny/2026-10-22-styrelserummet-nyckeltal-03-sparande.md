# Uppgift: Sparande per kvm

uppdrag: uppdrag/claude/2026-10-22-styrelserummet-nyckeltal.md
försök: 1
beror-på: 2026-10-22-styrelserummet-nyckeltal-02-skuld.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Nyckeltal.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/NyckeltalTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Nyckeltal.kt

## Uppgift
Lägg till:

```kotlin
fun justeratResultat(u: NyckeltalUnderlag): Long
fun sparandePerKvm(u: NyckeltalUnderlag): Long?
```

- `justeratResultat` = aretsResultat + avskrivningar + utrangeringar + planeratUnderhall + justeringar. (`justeringar` är redan med rätt tecken: positivt läggs till, negativt dras av.)
- `sparandePerKvm` = `avrundaKvot(justeratResultat(u), totalYta(u))`, eller `null` om `totalYta` är 0 eller mindre. Kan bli negativt.

## Exempel
Samma underlag som uppgift 02:
- `justeratResultat` → 1 700 000
- `sparandePerKvm` → 309
- Med justeringar −2 000 000 → justerat −300 000, sparande −55 (−54,5 avrundas bort från noll)

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*NyckeltalTest"

## Gör inte
- Ändra andra filer eller tidigare funktioner.
- Använda flyttal.
