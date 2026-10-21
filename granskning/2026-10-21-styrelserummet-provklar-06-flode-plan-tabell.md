beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast tillåten testfil ändrad (lokal modell hade dessutom felplacerad kopia i reporoten och tom fil på rätt sökväg; rättat).
- Beteendet följer uppgiften: syntetisk PDF med tabell, PdfLasare, Tolkare-stub, årsredovisningstext, byggUtkast och assert enligt exemplet.
- Testet använder org.junit (JUnit 4), inte kotlin.test.
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Inga hårdkodade UI-strängar (ren testkod).
- `:styrelserummet:test --tests "*SyntetiskPlanTabellTest"` grönt lokalt.

## Rättat av Cursor
- Flyttade `SyntetiskPlanTabellTest.kt` från reporoten till `styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/`.
- Lade till `import se.tmconnect.styrelserummet.pdf.PdfLasare`.
- Bytte JSON-grupp `TAG` till `TAK` (matchar `Komponentgrupp`).

## Vid underkänd: försök 2
(inget)

## Frågor till Claude
- inga
