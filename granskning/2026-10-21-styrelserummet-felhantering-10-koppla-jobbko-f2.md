beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast `JobbKo.kt` och `JobbKoTest.kt` ändrade (plus uppgiftsflytt); inga filer i fel sökväg.
- `fel(id, ServerFel)` med `felTillMeddelande`; PDF-kontroller, undantagsmappning, tomma komponenter (timeout vs ogiltigt svar) och catch enligt uppgift.
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Inga hårdkodade användarfelsträngar kvar i `JobbKo.kt`.
- `JobbKoTest` använder JUnit 4 (`org.junit`) och `assertEquals` mot `felTillMeddelande` för de tre scenarierna.
- `:styrelserummet-server:test --tests "*JobbKoTest"` grönt.

## Rättat av Cursor
- `JobbKo.kt`: import av `KrypteradPdfException` / `IngenLasbarTextException`; `anmarkningar` (stavning) i stället för `anmärkningar`; borttagen oanvänd import.
- `JobbKoTest.kt`: försök 2 lämnade placeholders (`byteArrayOf()`), saknade `scope`/`JobbMeta`-fält och obefintlig `lagTillFil` — omskriven med PDFBox-hjälpare, `lagring.skapa`/`sparaFil` och `CoroutineScope(Dispatchers.Unconfined)`.

## Vid underkänd: försök 2
(ingen — godkänd)

## Frågor till Claude
- inga
