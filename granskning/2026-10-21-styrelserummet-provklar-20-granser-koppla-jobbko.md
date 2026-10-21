beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Efter granskning: endast `JobbKo.kt` och `JobbKoTest.kt` skiljer sig från main i kod (förbjudna ändringar i `FelHantering.kt` och `FelSvar.kt` borttagna).
- `skattaTecken` körs efter PDF-extrahering och före `byggUtkast`; vid överskriden gräns sätts FEL utan tolkaranrop.
- Felmeddelande via `Felmeddelanden.forklara(Felkoder.FOR_STOR_HANDLING)` enligt uppgiften.
- Tester `forStorHandlingAnroparInteTolkare` och `underGransenAnroparTolkare` med spion-`Tolkare`, lång/kort påhittad text och assert mot FOR_STOR_HANDLING-text respektive minst ett anrop; JUnit 4 (org.junit).
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Ingen hårdkodad UI-text i appmoduler (servermodul).

## Rättat av Cursor
- Återställde `FelSvar.kt` och `FelHantering.kt` (lokal modell hade tagit bort `felText`/`foreningFelKod` och ändrat `serverFelTillKod` — bygget failade).
- Ersatte `ServerFel.ForStorHandling` med `felForStorHandling` och `Felkoder.FOR_STOR_HANDLING` i stället för otillåtna ändringar i `FelHantering.kt`.
- Rättade `SpionTolkare` till riktig `Tolkare`-signatur och giltigt JSON-svar så `underGransenAnroparTolkare` når KLAR.
- Lade till `pdfMedLangText` (sidor à 2000 tecken) för stora PDF-texter i test.

## Vid underkänd: försök 2

## Frågor till Claude
- inga
