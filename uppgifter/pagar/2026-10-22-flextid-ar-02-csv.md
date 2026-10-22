# Uppgift: CSV för årsöversikten

uppdrag: uppdrag/claude/2026-10-22-flextid-arsoversikt.md
försök: 1
beror-på: 2026-10-22-flextid-ar-01-oversikt

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/ArsOversiktCsv.kt (ny)
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/ArsOversiktCsvTest.kt (ny)

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/ArsOversikt.kt
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/TidFormatering.kt

## Uppgift
```kotlin
fun arsOversiktCsv(
    rader: List<ManadsRad>,
    rubriker: List<String>,   // 4 rubriker: månad, arbetat, utfall, saldo
    manadsnamn: List<String>, // 12 namn, index 0 = januari
    locale: Locale,
): String
```

- Semikolon som avgränsare, `\n` som radslut, även efter sista raden.
- Första raden: rubrikerna.
- Sedan en rad per `ManadsRad`: `manadsnamn[manad - 1]`, `formateraMinuterSomTid(arbetadMin, locale)`, `formateraSaldoMinuter(utfallMin, locale)`, `formateraSaldoMinuter(saldoVidSlutMin, locale)`.
- Fel antal rubriker (inte 4) eller månadsnamn (inte 12) → `IllegalArgumentException`.

## Exempel
Rubriker `Månad, Arbetat, Utfall, Saldo`, svenska månadsnamn, locale sv-SE.
Rader `ManadsRad(1, 510, 30, 150)` och `ManadsRad(3, 420, -60, 90)` ger:
```
Månad;Arbetat;Utfall;Saldo
januari;8:30;+0:30;+2:30
mars;7:00;-1:00;+1:30
```

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*ArsOversiktCsvTest"

## Gör inte
- Ändra andra filer.
