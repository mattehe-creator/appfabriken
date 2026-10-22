# Uppgift: svensk veckodag i datumtext

uppdrag: uppdrag/2026-10-22-styrelserummet-stammotidplan.md
försök: 2
beror-på: 2026-10-22-styrelserummet-stamma-09-tidplan-samlad.md

## Vad som saknades
- Båda tillåtna filerna skapades men lämnades tomma (0 byte).
- Funktionen `datumMedVeckodag` saknas helt.
- Ingen testklass med `@Test` som jämför utdata mot exemplen nedan — Gradle rapporterar "No tests found" för `*StammaVeckodagTest`.

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/StammaUtskrift.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/StammaVeckodagTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/regler/STAMMA.md
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/StammotidplanTest.kt (JUnit 4, `org.junit`)

## Uppgift
Fyll i `StammaUtskrift.kt` med:

fun datumMedVeckodag(d: LocalDate): String

Format: `2026-05-31 (söndag)` — ISO-datum (`LocalDate` som sträng ger `yyyy-MM-dd`), svensk veckodag i **gemener** med åäö (måndag … söndag). Mappa `d.dayOfWeek` (`DayOfWeek`) till svenska namn utan locale eller externa bibliotek (t.ex. en `Map<DayOfWeek, String>` eller `when`).

## Exempel
- 2026-05-31 → `2026-05-31 (söndag)`
- 2026-05-15 → `2026-05-15 (fredag)`
- 2026-06-06 → `2026-06-06 (lördag)`

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*StammaVeckodagTest"

Skriv minst ett test (gärna tre `assertEquals` för datumen ovan) med JUnit 4: `import org.junit.Test`, `import org.junit.Assert.assertEquals`, `import java.time.LocalDate`.

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.

utfall: godkänd 2026-10-22 (Cursor implementerade StammaUtskrift.kt och StammaVeckodagTest.kt; lokal modell levererade ingen kod)
