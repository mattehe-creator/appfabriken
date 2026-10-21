# Uppgift: Koppla felhantering i jobbkön

uppdrag: uppdrag/2026-10-21-styrelserummet-felhantering.md
försök: 2
beror-på: 2026-10-21-styrelserummet-felhantering-08-pdf-krypterad-f2.md

## Vad som saknades
- `JobbKo.kt` ändrades inte. Alla anrop till `fel(id, "…")` med hårdkodad text (inklusive filnamn) kvarstår.
- `JobbKoTest.kt` skapades men är **tom** (0 rader kod). Uppgiftens testkommando (`*JobbKoTest`) kör inget meningsfullt.
- Ingen koppling till `felTillMeddelande` / `ServerFel` enligt uppdraget.

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/JobbKo.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/JobbKoTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/FelHantering.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/pdf/PdfLasare.kt

## Uppgift

1. **`JobbKo.kt`** — byt `fel(id, meddelande: String)` till `fel(id, serverFel: ServerFel)` som sätter `felmeddelande = felTillMeddelande(serverFel)` (importera från samma paket som `FelHantering.kt`).

2. **PDF per fil** (i loopen över `listaFiler`):
   - Om `pdfLasare.arKrypterad(bytes)` → `fel(id, ServerFel.KrypteradPdf)` och returnera.
   - Om `!pdfLasare.harLasbarText(bytes)` → `fel(id, ServerFel.IngenLasbarText)` (inga filnamn i meddelandet).
   - Vid `pdfLasare.lasSomText(bytes).getOrElse { … }`: mappa undantag — `KrypteradPdfException` → `KrypteradPdf`, `IngenLasbarTextException` → `IngenLasbarText`, övrigt → `Okant`.

3. **Efter `byggUtkast(...)`** — om `utkast.komponenter.isEmpty()`:
   - Om **alla** anmärkningar börjar med `"Tolkning misslyckades"` (typiskt när varje AI-anrop gav `Result.failure`, t.ex. timeout) → `fel(id, ServerFel.AiTimeout)`.
   - Annars (ogiltig/tom JSON som inte gav komponenter) → `fel(id, ServerFel.AiOgiltigtSvar)`.
   - Returnera utan att spara utkast som KLAR.

4. **`catch (_: Exception)`** i `bearbeta` → `fel(id, ServerFel.Okant)` i stället för hårdkodad text.

5. Övriga tidigare hårdkodade fel (t.ex. inga filer, tom underhållsplan) — använd `ServerFel.Okant` eller `IngenLasbarText` där det matchar beteendet; **aldrig** filnamn eller egen fri text i `felmeddelande`.

6. **`JobbKoTest.kt`** (JUnit 4, `org.junit` — **inte** `kotlin.test`):
   - Temporär katalog med `createTempDirectory`, `JobbLagring`, `JobbMeta` med status `VANTAR`.
   - **`JobbKo(..., synkron = true)`** så `lagTill` kör `bearbeta` direkt; `CoroutineScope(Dispatchers.Unconfined)` räcker som scope.
   - Test **tom PDF**: skapa bytes med PDFBox (tom sida utan text, som i `PdfLasareTest.skannadPdfUtanTextGerIngenLasbarText`). Spara som fil, kör jobbet, assert `lasMeta(id)!!.status == JobbStatus.FEL.name` och `felmeddelande == felTillMeddelande(ServerFel.IngenLasbarText)`.
   - Test **ogiltigt AI-svar**: PDF med läsbar text (kopiera hjälpfunktion från `ApiTest.pdfMedText`), `FalskTolkare(lage = SvarLage.OGILTIGT_JSON)`, assert `FEL` och `felmeddelande == felTillMeddelande(ServerFel.AiOgiltigtSvar)`.
   - Test **AI-timeout** (enligt uppgiftens exempel): `FalskTolkare(lage = SvarLage.TIMEOUT)`, assert `FEL` och `felmeddelande == felTillMeddelande(ServerFel.AiTimeout)`.

## Exempel
- Jobb med skannad tom PDF → meta `FEL`, meddelande som vid `felTillMeddelande(IngenLasbarText)`.
- Jobb där tolkaren returnerar `"inte json"` → meta `FEL`, meddelande som vid `felTillMeddelande(AiOgiltigtSvar)`.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*JobbKoTest"

## Gör inte
- Ändra `Komponent`, `Forening` eller `byggUtkast` om det inte går att klara inom tre filer — i så fall dokumentera i PR och lämna till Cursor.
- Lägga till beroenden.
