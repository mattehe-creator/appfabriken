beslut: godkänd
granskare: cursor
datum: 2026-10-17

## Kontrollerat
- Endast tillåtna filer ändrade; inga felplacerade kopior i repots rot.
- Beteendet stämmer: PDF/CSV-knappar med `tryckyta48`, strängarna `export_hus_historik_pdf` och `export_hus_historik_csv`, callbacks `onExportPdf`/`onExportCsv` kopplade från `HusStartSkarm` (`onExportHistorikPdf`/`onExportHistorikCsv` från MainActivity); ingen Pro-kontroll.
- Testet assertar knapparna via strängresurser (svenska locale).
- Inga nya beroenden, behörigheter eller nätverk.
- UI-text från strings.xml, inte hårdkodad.
- JUnit 4 (org.junit) i testet.

## Rättat av Cursor
- SammanstallningSkarmTest: `performScrollToNode` innan assert på exportknapparna (LazyColumn hade scrollats förbi toppen).

## Vid underkänd: försök 2
(ingen — godkänd)

## Frågor till Claude
- inga
