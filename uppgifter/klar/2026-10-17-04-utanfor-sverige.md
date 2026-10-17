# Uppgift: utanför Sverige — bara summor

uppdrag: uppdrag/2026-10-17-husloggen-forsaljning.md
försök: 1
beror-på: 2026-10-17-02-sammanstallning-flik-f2.md, cursor:2026-10-17-husloggen-forsaljning

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/HusSkarmar.kt
- husloggen/src/test/kotlin/se/tmconnect/husloggen/ui/SammanstallningSkarmTest.kt

## Läs som förebild (ändra inte)
- husloggen/APP.md (skatteregler bara för Sverige)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/SammanstallningSkarm.kt

## Uppgift
1. Sätt `visaSkatteInfo = locale.country.equals("SE", ignoreCase = true)` i `HusStartSkarm` när sammanställningen visas (samma `locale` som redan används i filen).
2. När landet inte är SE: ingen försäljningsårsväljare som påverkar skattetext, ingen bedömning Ja/Kräver bedömning/Nej, ingen gränstext 5 000 kr, ingen skatteinformationsruta. Summor per år och typ ska fortfarande visas.
3. Test: med `Locale.US` ska skatteinformation inte finnas i trädet; med `Locale("sv", "SE")` ska den finnas.

## Exempel
- Locale US → lista med belopp per typ, inget avsnitt «Information om förbättringsutgifter».
- Locale sv-SE → full vy enligt uppgift 02.

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*SammanstallningSkarmTest"

## Gör inte
- Ändra `regler/` eller export-PDF (Cursor sätter redan `inkluderaSkatteAvsnitt` från ViewModel).
- Ändra databasschemat.

utfall: godkänd 2026-10-17 (oanvänd import borttagen)
