# Uppgift: Avskrivningsplan för ett utkast

uppdrag: uppdrag/claude/2026-10-21-styrelserummet-avskrivningsplan.md
försök: 1
beror-på: 2026-10-21-styrelserummet-plan-06-csv-f2.md

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/PlanForUtkast.kt (ny)
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/PlanForUtkastTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/ApiModell.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivningsplan.kt

## Uppgift
Skapa `PlanForUtkast.kt` i paketet `se.tmconnect.styrelserummet.server`:

```kotlin
const val PLAN_ANTAL_AR = 10

fun planForUtkast(utkast: UtkastDto, antalAr: Int = PLAN_ANTAL_AR): List<PlanAr>
```

Planen börjar året efter räkenskapsårets slut: `avskrivningsplan(utkast.komponenter.map { franDto(it) }, utkast.arsslut + 1, antalAr)`. Importera `PlanAr` och `avskrivningsplan` från `se.tmconnect.styrelserummet.regler`.

Läs `ApiModell.kt` för hur `UtkastDto` och `KomponentDto` skapas i testet.

## Exempel
Utkast med årsslut 2025 och komponenterna A 1 200 000 kr, 40 år från 2010, och B 300 000 kr, 10 år från 2020:
- `planForUtkast(utkast, 5)` → (2026, 60 000, 780 000), (2027, 60 000, 720 000), (2028, 60 000, 660 000), (2029, 60 000, 600 000), (2030, 30 000, 570 000)
- `planForUtkast(utkast)` har 10 rader, första år 2026, sista 2035
- Utkast utan komponenter, `antalAr` 2 → (2026, 0, 0), (2027, 0, 0)

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*PlanForUtkastTest"

## Gör inte
- Ändra andra filer (rutter och utskrift kopplar Claude in).
- Lägga till beroenden.

utfall: godkänd 2026-10-22 (flytt från repots rot, JUnit 4, exempelvärden och tredje testfall)
