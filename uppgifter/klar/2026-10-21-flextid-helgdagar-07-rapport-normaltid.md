# Uppgift: Månadens normaltid i månadsrapporten

uppdrag: uppdrag/claude/2026-10-21-flextid-helgdagar.md
försök: 1
beror-på: 2026-10-21-flextid-helgdagar-06-manad.md

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/ManadsRapport.kt
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/ManadsRapportNormaltidTest.kt (ny)

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/ManadsNormaltid.kt
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/ManadsRapportTest.kt

## Uppgift
Månadsrapporten ska visa hur mycket normaltid månaden har, med röda dagar borträknade.

1. Lägg till ett fält sist i `ManadsRapportTexter`: `val normaltidManadEtikett: String = ""`.
2. Lägg till två fält sist i `ManadsRapportData`: `val arbetsdagarManad: Int = 0` och `val normaltidManad: String = ""`.
3. Lägg till en parameter sist i `byggManadsRapport`: `aftnarLediga: Boolean = true`.
4. I `byggManadsRapport`: räkna
   `val arbetsdagar = arbetsdagarIManad(arManad.year, arManad.monthValue, installning.arbetsdagarMask, uteslutRodaDagar = true, uteslutAftnar = aftnarLediga)`
   och sätt `arbetsdagarManad = arbetsdagar` och `normaltidManad = formateraMinuterSomTid(arbetsdagar * installning.normaltidMinPerDag, locale)` i det returnerade objektet.

Alla befintliga tester ska fortsätta gå igenom (de nya fälten har standardvärden).

## Exempel
Installning med normaltid 480 och mask måndag–fredag, inga dagar:
- April 2026 → `arbetsdagarManad` 20, `normaltidManad` = `formateraMinuterSomTid(9600, locale)`
- December 2026, `aftnarLediga = true` → 20
- December 2026, `aftnarLediga = false` → 22

Använd i testet samma sätt att skapa `ManadsRapportTexter` som i `ManadsRapportTest`.

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*ManadsRapport*"

## Gör inte
- Ändra andra filer.
- Ändra hur utfall och saldo räknas.
- Lägga till beroenden.

utfall: utförd av Claude (gren claude/flextid-helgdagar-gahem), 2026-10-21
