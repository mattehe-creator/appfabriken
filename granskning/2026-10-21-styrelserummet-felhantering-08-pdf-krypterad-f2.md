beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- PR #401 ändrade ingen Kotlin-kod (endast flytt av uppgiftsfil till `pagar/`); samma sak som underkänd PR #399.
- Efter försök 2 utan implementation tog Cursor över enligt CLAUDE.md punkt 3 och uppgifter/MALL.md.
- Endast `PdfLasare.kt` och `PdfLasareTest.kt` under `styrelserummet/`; inga filer i repots rot.
- `KrypteradPdfException`, `arKrypterad`, och krypteringskontroll före textutvinning i `lasPerSida` så lösenordsskyddad PDF ger `KrypteradPdfException`, inte `IngenLasbarTextException`.
- `arKrypterad` returnerar `true` vid `isEncrypted` och vid `InvalidPasswordException` utan lösenord.
- Nytt test `olastPdfArInteKrypteradMenKrypteradPdfGerKrypteradPdfException` assertar olåst → false/success, krypterad → true och `KrypteradPdfException` på `lasPerSida`/`lasSomText`.
- Befintliga tester `lasPerSidaReturnerarTextPerSida` och `skannadPdfUtanTextGerIngenLasbarText` gröna.
- Inga nya beroenden, behörigheter eller nätverk.
- Ingen ny hårdkodad UI-text (befintlig `INGEN_LASBAR_TEXT` oförändrad).
- Enhetstest använder JUnit 4 (`org.junit`).

## Rättat av Cursor
- Hela uppgiftslösningen i `PdfLasare.kt` och `PdfLasareTest.kt` (lokal modell levererade ingen kod på försök 2).

## Vid underkänd: försök 2
(ingen — Cursor implementerade efter försök 2 enligt direktiv.)

## Frågor till Claude
- inga
