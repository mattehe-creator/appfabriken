# Uppgift: Anmärkning anskaffningsår före byggnadsår

uppdrag: uppdrag/2026-10-20-styrelserummet-robusthet-handlingar.md
försök: 2
beror-på: ingen

## Vad som saknades
- Grenen innehöll ingen implementation: varken ändring i `Anmarkningar.kt` eller ny fil `AnmarkningarTest.kt`.
- Du ska leverera båda filerna enligt uppgiften nedan och pusha till samma gren.

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/Anmarkningar.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/AnmarkningarTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/BokfortVarde.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/ByggUtkastTest.kt

## Uppgift
Utöka `fun anmarkningarFor(utkast: Utkast, tabell: List<NyttjandeperiodRad>): List<String>` med en svensk anmärkning när en komponent har **anskaffningsår före föreningens byggnadsår** (`utkast.forening.byggnadsar`). Gäller bara när `byggnadsar` inte är null. Befintliga anmärkningar ska finnas kvar.

Lägg kontrollen i samma `forEach` över `utkast.komponenter` som övriga komponentregler. Texten ska nämna komponentens namn och att anskaffningsåret ligger före byggnadsåret (inkludera båda årtalen), i samma ton som befintliga komponentanmärkningar.

## Exempel
- Utkast med `Forening(namn = "Test", byggnadsar = 1975)` och komponent med `anskaffningsar = 1970` → minst en anmärkning som nämner komponentnamn och att anskaffningsår 1970 ligger före byggnadsår 1975.
- Samma byggnadsår som komponenten (`anskaffningsar = 1975`) → ingen ny anmärkning för den regeln.

## Test som ska gå igenom
gradle :styrelserummet:testDebugUnitTest --tests "*AnmarkningarTest.anmarkningAnskaffningsarForeByggnadsar"

Skapa `AnmarkningarTest.kt` med JUnit 4 (`org.junit.Test`, `org.junit.Assert`), inte `kotlin.test`. Bygg ett minimalt `Utkast` med `ArsredovisningUtdrag(null, null, null, null)`, `avstamning = null`, tom `anmarkningar`-lista och `tabell = emptyList()` så att bara den nya regeln testas. Testmetoden ska:
1. Verifiera att anskaffningsår 1970 ger en anmärkning med komponentnamn, 1970 och 1975.
2. Verifiera att anskaffningsår 1975 inte ger anmärkning för den regeln (samma utkast utom komponentens `anskaffningsar`).

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra `Komponent` eller `Forening`.
