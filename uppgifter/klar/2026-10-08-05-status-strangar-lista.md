# Uppgift: strängar för lista och formulär

uppdrag: uppdrag/2026-10-08-kop-databas-lista-formular.md
försök: 1
beror-på: 2026-10-08-01-garantitid.md

## Ändra bara dessa filer
- garantivalvet/src/main/res/values/strings.xml
- garantivalvet/src/main/res/values-sv/strings.xml

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/Garanti.kt

## Uppgift
Lägg till samma nycklar på engelska och svenska:

**Lista / status**
- `status_galler`, `status_gar_ut_snart`, `status_utgangen`
- `lista_garanti_slut` (format `%1$s`)

**Formulär**
- `falt_datum`, `falt_pris`, `falt_anteckning`
- `fel_vad_saknas`, `fel_garanti`, `fel_datum`, `fel_pris`

**Borttagning**
- `bekrafta_ta_bort_titel`, `bekrafta_ta_bort_text`, `ja_ta_bort`, `nej`

Använd tydlig svensk och engelska motsvarighet (inte ordagranna enum-namn).

## Exempel
- `StringsNycklarTest` passerar.
- `fel_vad_saknas` på svenska nämner att fältet vad krävs.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.

utfall: godkänd 2026-10-09 (Claude rättade fem feltexter så att de stämmer med reglerna)
