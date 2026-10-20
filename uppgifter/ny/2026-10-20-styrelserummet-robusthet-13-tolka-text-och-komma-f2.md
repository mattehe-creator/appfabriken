# Uppgift: Text runt JSON, kommatecken, null och taltext

uppdrag: uppdrag/2026-10-20-styrelserummet-robusthet-handlingar.md
försök: 2
beror-på: ingen

## Vad som saknades
- **`TolkaForslag.kt` ändrades inte alls.** All robusthetslogik ska ligga där (och ev. i befintlig `heltalsVarde`).
- **`TolkaForslagTest.kt` lämnades tom.** Uppgiften kräver klassen `TolkaForslagTest` med minst metoden `tolkaRobustJsonOchTal` som assertar mot exemplen nedan.
- Använd **JUnit 4** (`import org.junit.Test`, `import org.junit.Assert.*`), inte `kotlin.test` eller JUnit Jupiter.
- Rör **inga andra filer** (t.ex. inte `HittaByggnadensVarde.kt`).

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/TolkaForslag.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/TolkaForslagTest.kt

## Läs som förebild (ändra inte)
- Befintlig `TolkaForslag.kt` på grenen/main (parseKomponent, heltalsVarde, anmärkningar vid ogiltig JSON)

## Uppgift
Utöka `tolkaForslag` och `heltalsVarde` så att:
1. **Markdown-kodstaket** — om indata innehåller ` ```json ` … ` ``` `, extrahera innehållet däremellan före parsning. Samma JSON som rå text ska ge samma resultat.
2. **Text före/efter JSON** — kort fri text runt ett JSON-objekt ska ignoreras (t.ex. hitta första `{` och matchande avslutande `}` eller parsa efter extraktion).
3. **Avslutande kommatecken** — aktivera i JSON-parsern (t.ex. `Json { ignoreUnknownKeys = true; allowTrailingComma = true }`).
4. **JSON `null` på talfält** — behandla som saknat värde (`null` i Kotlin), inte som ogiltigt heltal. Gäller både riktigt `JsonNull` och strängen `"null"` om den förekommer.
5. **Tal som textsträngar** — `"anskaffningsvarde": "1 200 000"` och `"1200000"` ska bli `1200000L` (ta bort mellanslag i strängen innan `toLongOrNull`).
6. **Ogiltig JSON eller ogiltiga talsträngar** — anmärkning och `Sakerhet.LAG` på raden, **aldrig** okontrollerat undantag ut ur `tolkaForslag`.

## Exempel (ska finnas som assert i testet)
- Indata: samma komponent som rå JSON, men inlindad i ` ```json ` … ` ``` ` → samma `ForslagResultat` som utan staket.
- Indata: `Notera detta {"komponenter":[],"anmarkningar":[]}` → tom komponentlista, inga krascher.
- Indata: `{"komponenter":[{"namn":"Tak","grupp":"TAK","anskaffningsvarde":"1 200 000","anskaffningsar":2000,"nyttjandeperiodAr":40,"kalla":"p","sakerhet":"MEDEL"}],"anmarkningar":[]}` → en komponent med `anskaffningsvarde == 1200000L`.
- Indata: samma objekt men `"anskaffningsvarde": "1200000"` (sträng utan mellanslag) → `1200000L`.
- Indata: `"anskaffningsvarde": "abc"` → anmärkning om heltal, komponenten får `Sakerhet.LAG` (eller hoppas över enligt befintlig logik — men **LAG** ska gälla).
- Indata: `inte json alls` → anmärkning om ogiltig JSON, tom lista.
- Indata: JSON med avslutande komma efter sista fält i objekt/array → ska parsas utan fel.

## Test som ska gå igenom
gradle :styrelserummet:testDebugUnitTest --tests "*TolkaForslagTest.tolkaRobustJsonOchTal"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
