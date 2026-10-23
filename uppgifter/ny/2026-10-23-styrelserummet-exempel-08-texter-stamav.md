# Uppgift: Avstämning mellan plan och årsredovisning i exempeldata

uppdrag: uppdrag/2026-10-23-styrelserummet-exempelhandlingar.md
försök: 1
beror-på: 2026-10-23-styrelserummet-exempel-07-texter-arsredovisning.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelTexter.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelTexterStamAvTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avstamning.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/ByggUtkastTest.kt

## Uppgift
Lägg till `fun exempelKomponenterFranPlan(): List<Komponent>` i `ExempelTexter.kt` (eller `ExempelData.kt` om det passar bättre — **välj ett ställe och håll dig till det**): en komponent per åtgärd **med kostnad**, med grupp enligt namn (använd befintliga `Komponentgrupp`-värden), `anskaffningsvarde = kostnadKronor`, `anskaffningsar = ar`, rimlig `nyttjandeperiodAr` från `nyttjandeperioder.csv`-intervall, `sakerhet = HOG`, `kalla = "exempel-underhallsplan"`. **Inkludera inte** Balkonger (saknar kostnad).

Test `planSummaStammerMedByggnadEnligtStamAv`:

- `stamAv(exempelKomponenterFranPlan(), BYGGNADENS_VARde).stammer == true`
- `stamAv(...).summa == summaAtgarderMedKostnad()`

## Exempel
- 13 komponenter, summa 27 000 000, `stammer == true`.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*ExempelTexterStamAvTest"

## Gör inte
- Ändra `stamAv` eller modeller.
- Lägga till balkongkomponent med uppskattat värde här (det kommer i flödestestet).
