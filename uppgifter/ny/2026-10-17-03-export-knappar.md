# Uppgift: exportknappar PDF och CSV

uppdrag: uppdrag/2026-10-17-husloggen-forsaljning.md
försök: 1
beror-på: 2026-10-17-02-sammanstallning-flik-f2.md, cursor:2026-10-17-husloggen-forsaljning

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/SammanstallningSkarm.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/HusSkarmar.kt
- husloggen/src/test/kotlin/se/tmconnect/husloggen/ui/SammanstallningSkarmTest.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopDetaljSkarm.kt (exportknapp)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/MainActivity.kt (efter Cursor: PDF/CSV-kontrakt och callbacks)

## Uppgift
1. På sammanställningsfliken: två knappar med minst 48 dp tryckyta — `export_hus_historik_pdf` och `export_hus_historik_csv`.
2. Propagera `onExportPdf` och `onExportCsv` från `MainActivity` via `HusStartSkarm` till `SammanstallningSkarm`.
3. Utöka `SammanstallningSkarmTest` så att knapparna finns (contentDescription eller text).

Export ska alltid vara gratis (ingen Pro-kontroll).

## Exempel
- Tryck på PDF-knappen → `onExportPdf` anropas.
- Tryck på CSV-knappen → `onExportCsv` anropas.

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*SammanstallningSkarmTest"

## Gör inte
- Implementera själva filskrivningen (finns i Cursor-delens `HusViewModel` och `export/`).
- Nya behörigheter.
