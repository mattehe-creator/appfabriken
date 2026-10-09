# Uppgift: listrad och sorterad lista

uppdrag: uppdrag/2026-10-08-kop-databas-lista-formular.md
försök: 1
beror-på: 2026-10-08-02-sortera-efter-garanti-f2.md, 2026-10-08-04-formatering-visning.md, 2026-10-08-05-status-strangar-lista.md

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopListRad.kt (ny)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopSkarmar.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopViewModel.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/Garanti.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/KopSortering.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/Formatering.kt

## Uppgift
1. **`KopListRad`** (Composable): visar `vad`, valfritt `var_kopt`, formaterat garantislut (`formateraDatum(garantiSlut(...))` + `lista_garanti_slut`), och status (`garantiStatus` → text från `status_*`-strängar). Statusfärg: `MaterialTheme.colorScheme.primary` (gäller), `tertiary` (snart), `error` (utgången). Minsta tryckyta 48 dp.
2. **`KopSkarmar.kt`**: ersätt `KopRadSkelett` med `KopListRad`.
3. **`KopViewModel`**: exponera listan sorterad med `sorteraEfterGaranti(..., LocalDate.now())` (idag injicerbar för test via default).

## Exempel
- Köp med garanti som gäller visar grönaktig (primary) statusetikett enligt `status_galler`.
- Listan i UI följer samma ordning som `sorteraEfterGaranti` för dagens datum.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*KopSorteringTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
