# Uppgift: formulärskärm med validering

uppdrag: uppdrag/2026-10-08-kop-databas-lista-formular.md
försök: 1
beror-på: 2026-10-08-03-formular-validering.md, 2026-10-08-05-status-strangar-lista.md

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopFormularSkarm.kt (ny)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopSkarmar.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/MainActivity.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/FormularValidering.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopViewModel.kt

## Uppgift
1. Skapa **`KopFormularSkarm`** som ersätter `KopFormularSkelett`.
2. Fält: vad, var (valfritt), köpdatum (Material3 datumväljare), garantitid i månader, pris (valfri text), anteckning (valfritt). Etiketter och fel från `strings.xml` (`falt_*`, `fel_*`).
3. **Spara:** kör `valideraKopFormular`; visa ett felmeddelande per `FormularFel` under fältet (text från `fel_vad_saknas`, `fel_garanti`, `fel_datum`, `fel_pris`). Spara inte om något fel finns.
4. Vid lyckad validering: anropa befintliga callbacks/viewModel (`sparaNyttKop` / `uppdateraKop`) med `tolkaPrisTillOre` för pris.
5. **Avbryt:** tillbaka till listan.
6. Ta bort `KopFormularSkelett` från `KopSkarmar.kt`. Uppdatera `MainActivity.kt` att använda `KopFormularSkarm`.

## Exempel
- Tomt vad → `fel_vad_saknas` syns, inget sparas.
- Pris `"1 299,50"` och övriga fält giltiga → sparas med `prisOre = 129950`.
- Köpdatum i framtiden → `fel_datum`, inget sparas.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*FormularValideringTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.

utfall: godkänd 2026-10-09 (tog bort KopSortering-filer utanför uppgiften; ok→spara, Toast bort, fel-färg från tema)
