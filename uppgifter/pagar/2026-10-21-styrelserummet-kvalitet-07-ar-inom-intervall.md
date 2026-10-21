# Uppgift: arInomIntervall för nyttjandeperiod

uppdrag: uppdrag/2026-10-21-styrelserummet-kvalitet-utkast.md
försök: 1
beror-på: 2026-10-21-styrelserummet-kvalitet-06-forslag-for-grupp.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/NyttjandeperiodForslag.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/NyttjandeperiodForslagTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/NyttjandeperioderTabell.kt

## Uppgift
`fun arInomIntervall(ar: Int, rad: NyttjandeperiodRad): Boolean`

- Om `rad.intervallAr` inte är null: `ar` ska ligga inklusive i intervallet (t.ex. `40..100`).
- Om `rad.intervallAr` är null (fast värde i tabellen, t.ex. STAMMAR `50`): returnera `ar == rad.forslagAr`.

## Exempel
- Rad FASAD (intervall 40–100), `ar = 40` → `true`
- Rad FASAD, `ar = 100` → `true`
- Rad FASAD, `ar = 30` → `false`
- Rad STAMMAR (intervall null, förslag 50), `ar = 50` → `true`
- Rad STAMMAR, `ar = 49` → `false`

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*NyttjandeperiodForslagTest.arInomIntervall"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra `nyttjandeperioder.csv`.
