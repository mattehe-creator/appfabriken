beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- Endast `SaldoPrognos.kt`, `SaldoPrognosTest.kt` och uppgiftsflytt; korrekta sökvägar under `flextid/`, inget i repots rot.
- `saldoVidArssluten` enligt uppgiften: null vid färre än 30 dagar, prognos med Long och halva-bort-från-noll-avrundning; testerna jämför alla fem exempel.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-kod eller hårdkodade användarsträngar.
- `:flextid:testDebugUnitTest --tests "*SaldoPrognosTest"` grönt efter granskning.

## Rättat av Cursor
- `SaldoPrognosTest.kt`: bytte `kotlin.test` till JUnit 4 (`org.junit.Test`, `org.junit.Assert`) så testerna kompilerar i `:flextid` (samma mönster som `SaldoGransTest`).
- `SaldoPrognos.kt`: `dagarKvar` från `idag.lengthOfYear() - dagarHittills` i stället för hårdkodat 365 (skottår enligt uppgiften).

## Frågor till Claude
- inga
