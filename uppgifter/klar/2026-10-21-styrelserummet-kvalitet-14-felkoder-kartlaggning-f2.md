# Uppgift: kartlägg serverns felkoder

## Vad som saknades
- Grenen innehåller ingen implementation: varken `Felkoder.kt` eller `FelkoderTest.kt` skapades.
- Endast uppgiftsfilen flyttades till `uppgifter/pagar/` (det gör körskriptet; räcker inte som PR-innehåll).
- Testet `gradle :styrelserummet-server:test --tests "*FelkoderTest.kartlaggning"` kan inte köras eftersom testklassen saknas.

uppdrag: uppdrag/2026-10-21-styrelserummet-kvalitet-utkast.md
försök: 2
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Felkoder.kt (ny)
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/FelkoderTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Application.kt
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/JobbKo.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/IndataTest.kt (JUnit 4-import)

## Uppgift
Skapa `object Felkoder` med **publika strängkonstanter** (`const val`, UPPER_SNAKE) för varje fel som idag kan nå användaren via API eller jobbstatus. Värdet ska vara samma sträng som konstantnamnet (t.ex. `const val FOR_MANGA_JOBB = "FOR_MANGA_JOBB"`). Minst dessa (kod → ungefärlig källa i Application.kt / JobbKo.kt):

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

**Ingen** svensk användartext i denna fil – bara konstanter och `allaKoder`. API:t använder fortfarande befintliga meddelanden i Application/JobbKo; denna uppgift kopplar inte in koderna där.

## Exempel
- `Felkoder.FOR_MANGA_JOBB` är en icke-tom sträng och equals `"FOR_MANGA_JOBB"`.
- `allaKoder()` innehåller minst 15 koder och exakt lika många element som det finns unika `const val`-koder i `Felkoder`.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*FelkoderTest.kartlaggning"

Testmetoden `kartlaggning` ska med `org.junit.Assert` verifiera exemplen ovan (icke-tom sträng, minst 15 koder, storlek på `allaKoder()` matchar antalet konstanter du definierat). Använd `import org.junit.Test`, inte `kotlin.test` eller JUnit Jupiter.

## Gör inte
- Ändra Application.kt eller JobbKo.kt i denna uppgift.
- Ändra `uppgifter/`, `granskning/`, `BESLUTSLOGG.md` eller andra filer utanför listan.
- Lägga till beroenden.

utfall: godkänd 2026-10-21 (hela implementationen — lokal modell levererade ingen kod på försök 2, PR #339)
