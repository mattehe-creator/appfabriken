# Uppgift: Avskrivningsplan per komponentgrupp

uppdrag: uppdrag/claude/2026-10-21-styrelserummet-avskrivningsplan.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/AvskrivningsplanPerGrupp.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvskrivningsplanPerGruppTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivningsplan.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Komponentgrupp.kt

## Uppgift
```kotlin
data class GruppPlan(val grupp: Komponentgrupp, val avskrivningPerAr: List<Long>)

fun avskrivningsplanPerGrupp(komponenter: List<Komponent>, franAr: Int, antalAr: Int): List<GruppPlan>
```

- En `GruppPlan` per grupp som har minst en komponent, i ordningen i enumen `Komponentgrupp`.
- `avskrivningPerAr[i]` är summan av `avskrivningFor(k, franAr + i)` för gruppens komponenter.
- Samma kontroll av `antalAr` som `avskrivningsplan` (0 → tom lista per grupp är inte meningsfullt: `antalAr <= 0` ger tom lista, över 100 ger `IllegalArgumentException`).

## Exempel
Komponenter: Tak (TAK) 1 200 000 kr, 40 år från 2010; Fönster (FONSTER_DORRAR) 300 000 kr, 10 år från 2020; Takavvattning (TAK) 100 000 kr, 20 år från 2015.
- franAr 2026, antalAr 2 → [GruppPlan(TAK, [35 000, 35 000]), GruppPlan(FONSTER_DORRAR, [30 000, 30 000])]
- tom lista → tom lista

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*AvskrivningsplanPerGruppTest"

## Gör inte
- Ändra andra filer.
- Använda flyttal.
