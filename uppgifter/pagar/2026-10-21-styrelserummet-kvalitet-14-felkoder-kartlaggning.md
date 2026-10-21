# Uppgift: kartlägg serverns felkoder

uppdrag: uppdrag/2026-10-21-styrelserummet-kvalitet-utkast.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Felkoder.kt (ny)
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/FelkoderTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Application.kt
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/JobbKo.kt

## Uppgift
Skapa `object Felkoder` med **publika strängkonstanter** (UPPER_SNAKE) för varje fel som idag kan nå användaren via API eller jobbstatus. Minst dessa (kod → ungefärlig källa):

| Kod | Källa |
|-----|--------|
| `FOR_MANGA_JOBB` | Application POST, 429 |
| `FOR_MANGA_FILER` | Application, max filer |
| `FOR_STORA_FILER` | Application, 25 MB |
| `INTE_PDF` | Application |
| `FORENING_SAKNAS` | valideraForeningsnamn tomt |
| `FORENING_FOR_LANG` | valideraForeningsnamn längd |
| `FORENING_OGILTIGA_TECKEN` | valideraForeningsnamn kontrolltecken |
| `ARSSLUT_SAKNAS` | Application arsslut |
| `INGA_PDF` | Application filAntal 0 |
| `JOBB_SAKNAS` | NotFound jobb |
| `JOBB_EJ_KLART` | PUT rader före KLAR |
| `SAKNAR_UTKAST` | PUT utkast saknas |
| `SAKNAR_JOBB_ID` / `OGILIG_LANK` | ogiltigt id |
| `INGA_FILER` | JobbKo tom lista |
| `PDF_EJ_LASBAR` | JobbKo pdf |
| `INGEN_PLAN` | JobbKo underhållsplan tom |
| `BEARBETNING_MISSLYCKADES` | JobbKo catch |
| `INTERNT_FEL` | StatusPages Throwable |

Lägg till `fun allaKoder(): Set<String>` som returnerar alla definierade kodvärden (strängarna, t.ex. `"FOR_MANGA_JOBB"`).

**Ingen** användartext i denna fil – bara konstanter och `allaKoder`.

## Exempel
- `Felkoder.FOR_MANGA_JOBB` är en icke-tom sträng
- `allaKoder()` innehåller minst 15 koder och samma antal som unika konstanter

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*FelkoderTest.kartlaggning"

## Gör inte
- Ändra Application.kt eller JobbKo.kt i denna uppgift.
- Lägga till beroenden.
