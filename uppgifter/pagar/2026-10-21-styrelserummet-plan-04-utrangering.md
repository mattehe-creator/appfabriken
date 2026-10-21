# Uppgift: Utrangeringsbelopp vid utbyte

uppdrag: uppdrag/claude/2026-10-21-styrelserummet-avskrivningsplan.md
försök: 1
beror-på: 2026-10-21-styrelserummet-plan-03-hela-listan.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Utbyte.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/UtbyteTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/BokfortVarde.kt

## Uppgift
Skapa `Utbyte.kt` i paketet `se.tmconnect.styrelserummet.regler`:

```kotlin
fun utrangeringsbelopp(k: Komponent, utbytesAr: Int): Long
```

När en komponent byts ut år `utbytesAr` kostnadsförs det som återstår av den gamla: bokfört värde vid slutet av året före.

- `utbytesAr <= k.anskaffningsar`: hela `k.anskaffningsvarde`.
- Annars: `bokfortVarde(k, utbytesAr - 1)` (det inkluderar ett eventuellt restvärde).

## Exempel
- 300 000 kr, 10 år från 2020, byts 2026 → 120 000
- 1 200 000 kr, 40 år från 2010, byts 2026 → 720 000
- 300 000 kr, 10 år från 2020, byts 2035 → 0
- 500 000 kr, restvärde 50 000, 10 år från 2018, byts 2030 → 50 000
- 300 000 kr från 2020, byts 2020 → 300 000

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*UtbyteTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
