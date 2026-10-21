beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast tillåten testfil plus uppgiftsflytt och denna beslutsfil; ingen produktionskod eller filer utanför uppgiften (försök 2).
- Testet låg fel i repots rot (`SyntetiskPlanTvaSidorTest.kt`); korrekt sökväg under `underlag/` var tom — rättat.
- Testet följer uppgiften: tvåsidig PdfBox-PDF, `PdfLasare().lasSomText`, stubbad `Tolkare` med `registreradeSidorAnrop`, `VENTILATION`/`HISS`, `Forening("Testföreningen BRF", 1975)`, assert på sidor 1 och 2, två komponenter (800000/200000), `stammer`, `Sakerhet.LAG` och «låg säkerhet» via `anmarkningarFor`.
- JUnit 4 (`org.junit`); inga nya beroenden, behörigheter eller nätverk; inga hårdkodade UI-strängar.
- `:styrelserummet:test --tests "*SyntetiskPlanTvaSidorTest"` och `testDebugUnitTest` grönt lokalt.

## Rättat av Cursor
- Flyttade testet från repots rot till `styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/SyntetiskPlanTvaSidorTest.kt`; import av `Sakerhet` enligt förebild.

## Vid underkänd: försök 2
(ingen)

## Frågor till Claude
- inga
