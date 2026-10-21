# Uppgift: Koppla felhantering i jobbkön

uppdrag: uppdrag/2026-10-21-styrelserummet-felhantering.md
försök: 1
beror-på: 2026-10-21-styrelserummet-felhantering-08-pdf-krypterad.md

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/JobbKo.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/JobbKoTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/FelHantering.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/pdf/PdfLasare.kt

## Uppgift
Ersätt hårdkodade svenska strängar i `JobbKo.fel(...)` med `felTillMeddelande(ServerFel.…)`:

- PDF utan läsbar text → `IngenLasbarText` (inga filnamn i meddelandet)
- Krypterad PDF → `KrypteradPdf` (via `PdfLasare` från uppgift 08)
- AI-timeout / ogiltigt svar → `AiTimeout` / `AiOgiltigtSvar` när **ingen** komponent kunde tas fram (alla `Result.failure` → timeout respektive ogiltigt svar; tomt/ogiltigt JSON som ger `utkast.komponenter.isEmpty()` efter `byggUtkast`)
- Övrigt undantag → `Okant`

Skriv enhetstest med temporär katalog, sparad PDF utan text (PDFBox i test), och `FalskTolkare` i timeout-läge — kontrollera att `JobbMeta.status` blir `FEL` och `felmeddelande` matchar rätt användartext.

## Exempel
- Jobb med skannad tom PDF → meta `FEL`, meddelande som vid `felTillMeddelande(IngenLasbarText)`.
- Jobb där tolkaren returnerar `"inte json"` → meta `FEL`, meddelande som vid `AiOgiltigtSvar`.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*JobbKoTest"

## Gör inte
- Ändra `Komponent`, `Forening` eller `byggUtkast` om det inte går att klara inom tre filer — i så fall dokumentera i PR och lämna till Cursor.
- Lägga till beroenden.
