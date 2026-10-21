beslut: underkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast tillåtna filer ändrade (Utskrift.kt, UtskriftTest.kt, uppgiftsflytt ny → pagar); inga filer i repots rot.
- Ingen implementation: båda Kotlin-filerna är **tomma** (0 byte). Uppgiften kräver `UtskriftsRad`, `utskriftsRader` och testet `utskriftsRader`.
- `:styrelserummet-server:test --tests "*UtskriftTest*"` misslyckas lokalt (`No tests found` — ingen testklass).
- Inga nya beroenden, behörigheter eller nätverksanrop i diffen.
- Ingen UI-kod; inget att granska för hårdkodade strängar i produktion.

## Rättat av Cursor
- inget (saknad implementation — inte småfel)

## Vid underkänd: försök 2

# Uppgift: utskriftsrader för komponentlista

uppdrag: uppdrag/2026-10-21-styrelserummet-kvalitet-utkast.md
försök: 2
beror-på: 2026-10-21-styrelserummet-kvalitet-09-formatering-procent-arsintervall.md

## Vad som saknades
- PR #381 skapade `Utskrift.kt` och `UtskriftTest.kt` på rätt sökvägar men lämnade dem **tomma**. Ingen `data class UtskriftsRad`, ingen `utskriftsRader`, inget JUnit 4-test.
- Enda kodrelevanta innehållet saknas; uppgiftsfilen flyttades bara till `uppgifter/pagar/`.
- Testet i uppgiften (`*UtskriftTest.utskriftsRader`) finns inte och kan inte köras.

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Utskrift.kt (ny — fyll i, filen finns redan tom)
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/UtskriftTest.kt (ny — fyll i, filen finns redan tom)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Formatering.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivning.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/BokfortVarde.kt
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/CsvExport.kt (servermodul, samma domän)

## Uppgift
Implementera i paketet `se.tmconnect.styrelserummet.server`:

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

Importera `Komponent` från `se.tmconnect.styrelserummet.modell`, samt `kronor`, `avskrivningFor` och `bokfortVarde` från `se.tmconnect.styrelserummet.regler`.

- En rad per komponent, sorterad på `grupp.svNamn` sedan `namn`.
- `grupp` = `Komponentgrupp.svNamn`.
- Belopp med `kronor(Long)`; nyttjandeperiod som `"N år"` (t.ex. `"50 år"`).
- `avskrivningAret` = `kronor(avskrivningFor(k, arsslut))`; `bokfortVarde` = `kronor(bokfortVarde(k, arsslut))`.
- Sista rad: `arSumma = true`, `namn = "Summa"`, `grupp = ""`, numeriska fält = `kronor` av **summan av respektive Long-kolumn** (inte summan av formaterade strängar). `nyttjandeperiod = ""` på summaraden.

Ingen HTML.

## Exempel
- En komponent: grupp EL, namn `"Belysning"`, 500_000 kr, anskaffningsår 2015, nyttjandeperiod 50, restvärde 0, arsslut 2025 →
  - `grupp` `"El"`, `anskaffningsvarde` `"500\u00A0000 kr"`, `nyttjandeperiod` `"50 år"`, `avskrivningAret` `"10\u00A0000 kr"`, `bokfortVarde` `"390\u00A0000 kr"`, `arSumma` false.
- Summaraden (en komponent): samma belopp som ovan i respektive kolumn, `namn` `"Summa"`, `grupp` `""`, `nyttjandeperiod` `""`, `arSumma` true.
- Två komponenter: kontrollera sortering på gruppnamn sedan komponentnamn.

## Test som ska gå igenom
Skapa `UtskriftTest` med JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals`). Testmetoden ska heta `utskriftsRader` och asserta minst en komponent enligt exemplet ovan plus summaraden (jämför hela `UtskriftsRad` eller fält för fält).

```
gradle :styrelserummet-server:test --tests "*UtskriftTest.utskriftsRader"
```

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Koppla in i webb eller API.

## Frågor till Claude
- inga
