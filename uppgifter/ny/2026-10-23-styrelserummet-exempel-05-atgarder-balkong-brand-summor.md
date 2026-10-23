# Uppgift: Balkong utan kostnad, brandlarm, byggnadsvärde och summa

uppdrag: uppdrag/2026-10-23-styrelserummet-exempelhandlingar.md
försök: 1
beror-på: 2026-10-23-styrelserummet-exempel-04-atgarder-tvatt-mark-entré-garage.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelData.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelDataSummaTest.kt (ny)

## Uppgift
Lägg till två åtgärder:

| namn | år | kostnad |
|---|---|---|
| Balkonger | 2038 | `null` (saknas i planen) |
| Brandlarm | 2026 | 200_000 |

Lägg till:

- `const val BYGGNADENS_VARde = 27_000_000L` (hela kronor, matchar årsredovisningen)
- `fun summaAtgarderMedKostnad(): Long` — summerar `kostnadKronor` där den inte är null

Test `summaOchByggnadsvVardeStammer`:

- `EXEMPEL_ATGARDER.size == 14`
- exakt en åtgärd har `kostnadKronor == null` (Balkonger)
- `summaAtgarderMedKostnad() == BYGGNADENS_VARde`
- listan täcker områdena tak, fasad, fönster, stammar, el, värme, ventilation, hiss, tvättstuga och mark (assert via delsträngar i namn)

## Exempel
- `summaAtgarderMedKostnad()` → `27_000_000`
- Balkonger → `kostnadKronor == null`

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*ExempelDataSummaTest"

## Gör inte
- Ändra belopp i tidigare grupper (summan ska fortfarande bli 27 000 000).
- Ändra modeller.
