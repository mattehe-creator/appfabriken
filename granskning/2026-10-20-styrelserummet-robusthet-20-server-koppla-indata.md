beslut: underkänd
granskare: cursor
datum: 2026-10-20

## Kontrollerat
- Diff mot main: endast flytt `uppgifter/ny/` → `uppgifter/pagar/`; `Application.kt` och `ApiTest.kt` är oförändrade.
- Uppgiften kräver koppling av `sakertFilnamn`, `arPdf`, `valideraForeningsnamn` och `valideraArsslut` i `POST /api/jobb` — kvarvarande ad hoc-kontroller (`forening.isBlank()`, `arsslut !in 2000..2100`, `.endsWith(".pdf")`, rått filnamn till `sparaFil`).
- Inga nya tester i `ApiTest` för ogiltigt föreningsnamn eller icke-PDF enligt exemplen.
- Inga filer utanför uppgiftens lista (ingen kod levererad); inga filer i repots rot.
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Befintliga `ApiTest` använder JUnit 4 (`org.junit`).
- Bygget på grenen grönt (ingen kod som kunde bryta kompilering).

## Rättat av Cursor
- inget (implementation saknas helt)

## Vid underkänd: försök 2

```
# Uppgift: Koppla in indatavalidering i API:t

uppdrag: uppdrag/2026-10-20-styrelserummet-robusthet-handlingar.md
försök: 2
beror-på: 2026-10-20-styrelserummet-robusthet-19-server-indata-hjalpare-f2.md

## Vad som saknades
- Grenen innehöll bara commit «Uppgift startad» (flytt till `pagar/`). `Application.kt` och `ApiTest.kt` ändrades inte.
- `POST /api/jobb` använder fortfarande egna kontroller i stället för hjälparna i `Indata.kt` (mergade i uppgift 19).

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Application.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/ApiTest.kt

## Läs som förebild (ändra inte)
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Indata.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/IndataTest.kt

## Uppgift
I `POST /api/jobb` (samma gren som idag):

1. **Förening:** Efter multipart-loopen, anropa `valideraForeningsnamn(forening)`. Om resultatet inte är `null`, anropa `lagring.raderaJobb(id)` och kasta `FelBegaran` med **exakt** det felmeddelande som funktionen returnerar. Ta bort `forening.isBlank()`-grenen.
2. **Räkenskapsår:** Spara formulärfältet `arsslut` som trimmad sträng (inte bara `toIntOrNull` i loopen). Efter loopen: `val ar = valideraArsslut(arsslutText, java.time.Year.now().value)` (eller motsvarande nuår). Om `ar == null`, radera jobbet och kasta `FelBegaran("Ange räkenskapsårets slut (år).")`. Använd `ar` i `meta.copy(arsslut = ar, ...)`. Ta bort `arsslut !in 2000..2100`.
3. **PDF:** För varje uppladdad fil, efter `readBytes()`, om `!arPdf(bytes)`: dispose, `lagring.raderaJobb(id)`, kasta `FelBegaran("Bara PDF-filer kan laddas upp.")`. Ta bort kontrollen som bara tittar på filändelse `.pdf`.
4. **Filnamn:** Anropa `lagring.sparaFil(id, sakertFilnamn(namn), bytes)` där `namn` är `part.originalFileName ?: "fil.pdf"`.

Övrigt beteende (max filer, max bytes, minst en fil, ip-gräns) ska vara som idag.

## Exempel
- Multipart med `forening = ""` → HTTP `400`, JSON med `"fel"` som matchar `valideraForeningsnamn("")`, jobbmappen borttagen (samma som idag vid fel).
- Fil med disposition `filename=rapport.pdf` men bytes som **inte** börjar med `%PDF-` → HTTP `400`, `"fel"` begripligt (t.ex. «Bara PDF-filer…»), jobb raderat.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*ApiTest*"

Lägg till minst **två** nya `@Test` i `ApiTest` (JUnit 4, `org.junit`):

1. `tomForeningGer400` — posta multipart med `forening = ""`, en giltig PDF (t.ex. `pdfMedText("x")`), assert `BadRequest` och att svarstexten innehåller samma fel som `valideraForeningsnamn("")`.
2. `pdfFilnamnMenIntePdfBytesGer400` — posta med giltig förening och `arsslut` `"2025"`, en fil med `.pdf` i filnamn men body `NOTPDF`.encodeToByteArray(), assert `BadRequest` och att `"fel"` i JSON är begripligt.

Befintliga tester ska fortsatt vara gröna.

## Gör inte
- Ändra andra filer (särskilt inte `Indata.kt`).
- Lägga till beroenden.
```

## Frågor till Claude
- inga
