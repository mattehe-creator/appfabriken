# Uppgift: Anmärkning anskaffningsår före byggnadsår

uppdrag: uppdrag/2026-10-20-styrelserummet-robusthet-handlingar.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/Anmarkningar.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/AnmarkningarTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/BokfortVarde.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/ByggUtkastTest.kt

## Uppgift
Utöka `fun anmarkningarFor(utkast: Utkast, tabell: List<NyttjandeperiodRad>): List<String>` med en svensk anmärkning när en komponent har **anskaffningsår före föreningens byggnadsår** (`utkast.forening.byggnadsar`). Befintliga anmärkningar ska finnas kvar.

## Exempel
- Utkast med `Forening(namn = "Test", byggnadsar = 1975)` och komponent med `anskaffningsar = 1970` → anmärkning som nämner komponentnamn och att anskaffningsår ligger före byggnadsår 1975.
- Samma byggnadsår som komponenten → ingen ny anmärkning för den regeln.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*AnmarkningarTest.anmarkningAnskaffningsarForeByggnadsar"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra `Komponent` eller `Forening`.
