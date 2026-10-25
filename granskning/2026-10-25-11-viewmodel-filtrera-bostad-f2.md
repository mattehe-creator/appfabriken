beslut: godkänd
granskare: cursor
datum: 2026-10-25

## Kontrollerat
- Endast `husloggen/.../HusViewModel.kt` och `husloggen/.../HusViewModelExportTest.kt` ändrade (korrekt modulsökväg); inga filer i repots rot.
- `posterForExport` och `sparaKommandeUnderhallCsv` använder `filtreraPaBostad` på `allaPoster()` respektive `allaUppgifter()` enligt uppgiften; export beteende oförändrat för en bostad.
- Nytt test `exportHistorikCsvTarBaraMedValdBostad` verifierar exemplet A1/B1 (CSV innehåller A1, inte B1) med befintlig testsetup.
- JUnit 4 (`org.junit`, `assertTrue`/`assertFalse`); inget `kotlin.test`.
- Inga nya beroenden, behörigheter eller nätverk; inga hårdkodade UI-strängar tillagda.
- `:husloggen:testDebugUnitTest --tests "*HusViewModelExportTest*"` grönt lokalt.

## Rättat av Cursor
- Återställde hela `HusViewModel.kt` och `HusViewModelExportTest.kt` från `main` (lokal modell hade raderat ~1400 rader och lämnat ogiltiga platshållarkommentarer; bygget failade med massor av unresolved references).
- Implementerade `filtreraPaBostad` i exportvägarna och lade till enhetstestet enligt uppgiften (försök 2 misslyckades; Cursor tog över enligt CLAUDE.md punkt 3).

## Vid underkänd: försök 2
(ej tillämpligt — Cursor utförde uppgiften efter försök 2 och godkände.)

## Frågor till Claude
- inga
