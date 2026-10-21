# Uppgift: utskriftsrader för komponentlista

uppdrag: uppdrag/2026-10-21-styrelserummet-kvalitet-utkast.md
försök: 1
beror-på: 2026-10-21-styrelserummet-kvalitet-09-formatering-procent-arsintervall.md

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Utskrift.kt (ny)
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/UtskriftTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Formatering.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivning.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/BokfortVarde.kt

## Uppgift
```kotlin
data class UtskriftsRad(
    val grupp: String,
    val namn: String,
    val anskaffningsvarde: String,
    val nyttjandeperiod: String,
    val avskrivningAret: String,
    val bokfortVarde: String,
    val arSumma: Boolean = false,
)

fun utskriftsRader(komponenter: List<Komponent>, arsslut: Int): List<UtskriftsRad>
```

- En rad per komponent, sorterad på `grupp.svNamn` sedan `namn`.
- `grupp` = `Komponentgrupp.svNamn`.
- Belopp med `kronor`; nyttjandeperiod som `"N år"` (t.ex. `"50 år"`).
- `avskrivningAret` = `avskrivningFor(k, arsslut)`; `bokfortVarde` = `bokfortVarde(k, arsslut)`.
- Sista rad: `arSumma = true`, `namn = "Summa"`, `grupp = ""`, numeriska fält formaterade som summor av respektive kolumn (nyttjandeperiod tom sträng `""` på summaraden).

Ingen HTML.

## Exempel
- En komponent EL 500_000 kr, period 50, arsslut 2025 → rad med formaterade belopp enligt `FormateringTest`.
- Summaraden: anskaffning = summan av komponenternas anskaffningsvärden, samma för avskrivning och bokfört.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*UtskriftTest.utskriftsRader"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Koppla in i webb eller API.

utfall: underkänd 2026-10-21 av Cursor (PR #381). Försök 2 i granskning/2026-10-21-styrelserummet-kvalitet-16-utskrift-rader.md
