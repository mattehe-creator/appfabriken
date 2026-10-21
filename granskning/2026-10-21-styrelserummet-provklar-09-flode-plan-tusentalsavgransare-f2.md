beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast tillåten testfil, uppgiftsflytt och denna beslutsfil; ingen produktionskod, inga ändringar utanför uppgiften.
- Försök 2 implementerade `planHardMellanslagOchPunkt` enligt förebilderna (PdfBox, `PdfLasare`, stub-`Tolkare`, `byggUtkast`, assert på belopp, avstämning, säkerhet och anmärkningar); kvarvarande fel var duplicerad fil i repots rot och stavfel i property-namn.
- Testet matchar exemplen: komponent och byggnad 1_250_000 → `stammer == true`, summa 1_250_000.
- JUnit 4 (`org.junit`); inga nya beroenden, behörigheter eller nätverk; inga hårdkodade UI-strängar.
- `:styrelserummet:test --tests "*SyntetiskPlanTusentalsTest"` grönt lokalt.

## Rättat av Cursor
- Tog bort duplicerad `SyntetiskPlanTusentalsTest.kt` i repots rot (endast sökväg under `underlag/` ska finnas).
- Rättade `ananskaffningsvarde` → `anskaffningsvarde` så testet kompilerar.

## Vid underkänd: försök 2
(ingen)

## Frågor till Claude
- inga
