beslut: underkänd
granskare: cursor
datum: 2026-10-24

## Kontrollerat
- PR #663 ändrade bara flytt av uppgiftsfil (`ny` → `pagar`) och en ny testfil; `AvlasningCsv.kt` oförändrad trots uppgiften.
- `avlasningCsvFilnamn` gör fortfarande `lowercase()` och `[^a-z0-9]+` utan å→a, ä→a, ö→o — `"Värme"` blir `husloggen-v-rme-…`, inte enligt exemplen.
- `AvlasningCsvFilnamnTest.kt` finns på rätt sökväg men är helt tom (0 rader kod); inga tester mot exemplen.
- Inga nya beroenden, behörigheter eller nätverk; inga hårdkodade UI-strängar i PR:ens kodändringar.
- CI `bygg` failade på `HusViewModelExportTest.kommandeUnderhallCsvHamtarUppgifterFranDatabasenOchAnvanderKlockan` (från mergad kod på grenen, inte uppgiften): `uppgifter` samlas in i ViewModel-init för widgeten så listan är inte tom efter `sparaUppgift`.

## Rättat av Cursor
- Tog bort föråldrad `assertTrue(viewModel.uppgifter.value.isEmpty())` i `HusViewModelExportTest.kt` så hela `:husloggen:testDebugUnitTest` kan bli grönt på grenen.

## Vid underkänd: försök 2

# Uppgift: Å, ä och ö i filnamnet för mätarens CSV

## Vad som saknades (försök 1)
- `AvlasningCsv.kt` ändrades inte — svenska bokstäver ersattes fortfarande bara med bindestreck via regex `[^a-z0-9]+`.
- `AvlasningCsvFilnamnTest.kt` skapades men lämnades helt tom; inga JUnit-tester mot uppgiftens exempel.

uppdrag: uppdrag/claude/2026-10-24-testtackning-natt.md
försök: 2
beror-på: ingen

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/export/AvlasningCsv.kt
- husloggen/src/test/kotlin/se/tmconnect/husloggen/export/AvlasningCsvFilnamnTest.kt (ny)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/export/HusHistorikFilnamn.kt

## Uppgift
I `avlasningCsvFilnamn(matareNamn: String, idag: LocalDate): String` blir i dag `"Värme"` till `husloggen-v-rme-…`, eftersom å, ä och ö ersätts med bindestreck. Ändra **endast** funktionen `avlasningCsvFilnamn` så att namnet först görs om till gemener och sedan `å` → `a`, `ä` → `a`, `ö` → `o` (gemener räcker om du redan anropat `lowercase()`), **innan** de andra tecknen ersätts som förut med `replace(Regex("[^a-z0-9]+"), "-")` och `trim('-')`. Signaturen och resten av filen (t.ex. `avlasningCsv`) ändras inte.

## Exempel (idag = 2026-10-24)
- `"Värme"` → `"husloggen-varme-2026-10-24.csv"`
- `"Fjärrvärme Öst"` → `"husloggen-fjarrvarme-ost-2026-10-24.csv"`
- `"Ångpanna"` → `"husloggen-angpanna-2026-10-24.csv"`
- `"El & vatten"` → `"husloggen-el-vatten-2026-10-24.csv"`
- `"El"` → `"husloggen-el-2026-10-24.csv"`
- `"!!!"` → `"husloggen-matare-2026-10-24.csv"`

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*AvlasningCsvFilnamnTest"

Skriv testklassen med `org.junit.Test` och `org.junit.Assert.assertEquals` (inte `kotlin.test`). Minst ett test per rad i exemplen ovan, med `LocalDate.of(2026, 10, 24)` som `idag`.

## Gör inte
- Ändra andra filer eller funktionen `avlasningCsv`.
- Lämna testfilen tom.

## Frågor till Claude
- inga
