# Uppgift: Å, ä och ö i filnamnet för mätarens CSV

uppdrag: uppdrag/claude/2026-10-24-testtackning-natt.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/export/AvlasningCsv.kt
- husloggen/src/test/kotlin/se/tmconnect/husloggen/export/AvlasningCsvFilnamnTest.kt (ny)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/export/HusHistorikFilnamn.kt

## Uppgift
I `avlasningCsvFilnamn(matareNamn: String, idag: LocalDate): String` blir i dag "Värme" till `husloggen-v-rme-…`, eftersom å, ä och ö ersätts med bindestreck. Ändra funktionen så att namnet först görs om till gemener och sedan `å` → `a`, `ä` → `a`, `ö` → `o`, innan de andra tecknen ersätts som förut. Signaturen ändras inte. Ändra inget annat i filen.

## Exempel (idag = 2026-10-24)
- `"Värme"` → `"husloggen-varme-2026-10-24.csv"`
- `"Fjärrvärme Öst"` → `"husloggen-fjarrvarme-ost-2026-10-24.csv"`
- `"Ångpanna"` → `"husloggen-angpanna-2026-10-24.csv"`
- `"El & vatten"` → `"husloggen-el-vatten-2026-10-24.csv"`
- `"El"` → `"husloggen-el-2026-10-24.csv"`
- `"!!!"` → `"husloggen-matare-2026-10-24.csv"`

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*AvlasningCsvFilnamnTest"

## Gör inte
- Ändra andra filer eller funktionen `avlasningCsv`.
