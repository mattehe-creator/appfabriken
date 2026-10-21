# Uppgift: forklara felkoder på svenska

## Vad som saknades
- Grenen innehåller ingen implementation: varken `Felmeddelanden.kt` eller `FelmeddelandenTest.kt` skapades.
- Endast uppgiftsfilen flyttades till `uppgifter/pagar/` (det gör körskriptet; räcker inte som PR-innehåll).
- Testet `gradle :styrelserummet-server:test --tests "*FelmeddelandenTest.forklara"` kan inte köras eftersom testklassen saknas.

uppdrag: uppdrag/2026-10-21-styrelserummet-kvalitet-utkast.md
försök: 2
beror-på: 2026-10-21-styrelserummet-kvalitet-14-felkoder-kartlaggning-f2.md

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Felmeddelanden.kt (ny)
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/FelmeddelandenTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Felkoder.kt
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Application.kt
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/JobbKo.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/IndataTest.kt (JUnit 4-import)

## Uppgift
Skapa t.ex. `object Felmeddelanden` med:

`fun forklara(kod: String): String`

- För **varje** kod i `Felkoder.allaKoder()`: returnera samma vänliga svenska text som användaren ser idag (Application.kt / JobbKo.kt). Du får formulera om lite, men innebörden ska stämma. Använd `when (kod)` (eller motsvarande) mappat mot `Felkoder.*`-konstanterna.
- Okänd kod (t.ex. `"FINNS_EJ"`): returnera exakt `"Något gick fel. Försök igen."` — **visa aldrig** kodsträngen i svaret.
- Meddelanden får **inte** innehålla ordet `"felkod"` (case-insensitive) eller stacktrace-detaljer.

Referens (kod → dagens användartext):

| Kod | Text att matcha (exakt eller nära om JobbKo har filnamn) |
|-----|--------------------------------------------------------|
| `FOR_MANGA_JOBB` | För många jobb från samma adress. Vänta en timme och försök igen. |
| `FOR_MANGA_FILER` | Högst 10 filer per jobb. |
| `FOR_STORA_FILER` | Filerna får vara högst 25 MB tillsammans. |
| `INTE_PDF` | Bara PDF-filer kan laddas upp. |
| `FORENING_SAKNAS` | Ange föreningens namn. |
| `FORENING_FOR_LANG` | Föreningens namn får vara högst 120 tecken. |
| `FORENING_OGILTIGA_TECKEN` | Föreningens namn innehåller ogiltiga tecken. |
| `ARSSLUT_SAKNAS` | Ange räkenskapsårets slut (år). |
| `INGA_PDF` | Ladda upp minst en PDF. |
| `JOBB_SAKNAS` | Jobbet finns inte. |
| `JOBB_EJ_KLART` | Jobbet är inte klart än. |
| `SAKNAR_UTKAST` | Saknar utkast. |
| `SAKNAR_JOBB_ID` | Saknar jobb-id. |
| `OGILIG_LANK` | Ogiltig länk. |
| `INGA_FILER` | Inga filer hittades. |
| `PDF_EJ_LASBAR` | Filen innehåller ingen läsbar text. Ladda upp en PDF som går att markera text i. (utan filnamn i texten) |
| `INGEN_PLAN` | Kunde inte läsa underhållsplanen. Ladda upp minst en PDF med text. |
| `BEARBETNING_MISSLYCKADES` | Bearbetningen misslyckades. |
| `INTERNT_FEL` | Något gick fel. Försök igen. |

**Koppla inte in** `forklara` i Application.kt i denna uppgift.

## Exempel
- `forklara(Felkoder.INGA_PDF)` → `"Ladda upp minst en PDF."`
- `forklara("FINNS_EJ")` → `"Något gick fel. Försök igen."`

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*FelmeddelandenTest.forklara"

Testmetoden `forklara` ska med `org.junit.Assert.assertEquals` verifiera **minst tre** kända koder med **exakt** förväntad sträng (välj själv vilka, t.ex. `INGA_PDF`, `INTE_PDF`, `JOBB_SAKNAS`) plus en okänd kod. Använd `import org.junit.Test`, inte `kotlin.test` eller JUnit Jupiter.

## Gör inte
- Koppla in `forklara` i Application.kt (senare uppdrag).
- Ändra `uppgifter/`, `granskning/`, `BESLUTSLOGG.md` eller andra filer utanför listan.
- Lägga till beroenden.
