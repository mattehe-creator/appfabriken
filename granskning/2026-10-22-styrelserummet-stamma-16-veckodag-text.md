beslut: underkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- Endast tillåtna filer påverkades (sökvägar under `styrelserummet/`, ingen fil i rot) plus flytt av uppgiftsfil till `pagar/`.
- `StammaUtskrift.kt` och `StammaVeckodagTest.kt` finns men är **tomma** — ingen `datumMedVeckodag`, inga tester.
- Uppgiftens beteende och exempel är därmed inte implementerade; `:styrelserummet:test --tests "*StammaVeckodagTest"` hittar inga tester lokalt.
- Inga nya beroenden, behörigheter eller nätverk i diffen.
- Ingen FRAGA.md på grenen.

## Rättat av Cursor
- inget (saknad implementation är inte ett småfel)

## Vid underkänd: försök 2

```
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

```kotlin
fun datumMedVeckodag(d: LocalDate): String
```

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
```

## Frågor till Claude
- inga
