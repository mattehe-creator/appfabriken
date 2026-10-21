# Uppgift: Plan för hela komponentlistan

uppdrag: uppdrag/claude/2026-10-21-styrelserummet-avskrivningsplan.md
försök: 1
beror-på: 2026-10-21-styrelserummet-plan-02-per-komponent.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivningsplan.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvskrivningsplanTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Summering.kt

## Uppgift
Lägg till i `Avskrivningsplan.kt`:

```kotlin
fun avskrivningsplan(komponenter: List<Komponent>, franAr: Int, antalAr: Int): List<PlanAr>
```

En rad per år (samma årsintervall och samma kontroll av `antalAr` som `planForKomponent`). `avskrivning` är summan av alla komponenters avskrivning det året och `bokfortVarde` summan av deras bokförda värden vid årets slut. Tom lista med komponenter ger rader med 0 och 0.

## Exempel
Komponenter: A 1 200 000 kr, 40 år från 2010; B 300 000 kr, 10 år från 2020 (restvärde 0 för båda).
- franAr 2026, antalAr 5 →
  (2026, 60 000, 780 000), (2027, 60 000, 720 000), (2028, 60 000, 660 000), (2029, 60 000, 600 000), (2030, 30 000, 570 000)
- Tom komponentlista, franAr 2026, antalAr 2 → (2026, 0, 0), (2027, 0, 0)

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*AvskrivningsplanTest"

## Gör inte
- Ändra andra filer.
- Ändra `planForKomponent`.
- Lägga till beroenden.
