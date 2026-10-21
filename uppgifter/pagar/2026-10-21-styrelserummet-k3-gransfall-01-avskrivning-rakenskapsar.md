# Uppgift: Avskrivning när anskaffningsår är räkenskapsår

uppdrag: uppdrag/2026-10-21-styrelserummet-k3-gransfall.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvskrivningGransfallRakenskapsarTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivning.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvskrivningTest.kt
- styrelserummet/regler/K3.md

## Uppgift
Lägg till tester för `avskrivningFor(k: Komponent, ar: Int)` när komponenten **anskaffas samma kalenderår som räkenskapsåret** (köp mitt i året modelleras som helt räkenskapsår enligt K3.md i modulen).

Kontrollera att första avskrivningsåret ger **samma** helårsbelopp som övriga år (inte pro rata dagar), och att avskrivning fortfarande sker det året.

Om ett test visar fel beteende får du **endast** ändra `Avskrivning.kt` (lägg till filen under "Ändra bara dessa filer" i PR:en och håll dig inom cirka 150 rader totalt).

## Exempel
- Indata: anskaffningsvärde 90_000 kr, anskaffningsår 2025, nyttjandeperiod 3 år, restvärde 0, `ar` = 2025. Förväntat: 30_000 kr (samma som 2026 och 2027 före sista året).
- Indata: samma komponent, `ar` = 2024. Förväntat: 0 kr.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*AvskrivningGransfallRakenskapsarTest"

## Gör inte
- Ändra `AvskrivningTest.kt`, `Komponent.kt`, `Forening.kt` eller andra filer.
- Lägga till beroenden.
- Använda flyttal för pengar i tester eller implementation.
