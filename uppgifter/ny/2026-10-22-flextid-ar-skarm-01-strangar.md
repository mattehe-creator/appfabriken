# Uppgift: Strängar för årsöversiktsskärmen

uppdrag: uppdrag/claude/2026-10-22-flextid-arsoversikt-skarm.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- flextid/src/main/res/values/strings.xml
- flextid/src/main/res/values-sv/strings.xml

## Läs som förebild (ändra inte)
- flextid/src/main/res/values/strings.xml (befintliga `csv_*` och `exportera_ars_csv`)

## Uppgift
Lägg till strängnycklar (engelska i `values/`, svenska i `values-sv/`) för årsöversiktsskärmen. Använd `%1$s` där värdet är formaterad tid/saldo från koden (samma formatering som i listan: `formateraMinuterSomTid` / `formateraSaldoMinuter`).

| Nyckel | Svenska (kort) | English |
|--------|----------------|---------|
| `arsoversikt_rubrik` | Årsöversikt | Year overview |
| `arsoversikt_oppna` | Årsöversikt | Year overview |
| `arsoversikt_tillbaka` | Tillbaka | Back |
| `arsoversikt_ar` | År | Year |
| `arsoversikt_prognos_rubrik` | Uppskattat saldo vid årets slut | Estimated balance at year end |
| `arsoversikt_prognos_varde` | %1$s | %1$s |
| `arsoversikt_prognos_forklaring` | Baserat på flexsaldot idag och årets utfall hittills, om samma takt håller resten av året. | Based on today’s flex balance and this year’s outcome so far, if the same pace continues for the rest of the year. |
| `arsoversikt_prognos_ingen_data` | För lite data än (minst 30 dagar på året). | Not enough data yet (at least 30 days in the year). |
| `arsoversikt_dela_csv` | Dela som CSV | Share as CSV |

Listan på skärmen ska använda befintliga `csv_manad`, `csv_arbetad`, `csv_utfall`, `csv_saldo` som kolumnrubriker (lägg inte till duplicerade rubriksträngar).

## Exempel
- `StringsNycklarTest` ska fortsätta ha samma nyckelmängd i båda språkfiler.

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Ändra andra filer.
- Hårdkoda text i Kotlin.
