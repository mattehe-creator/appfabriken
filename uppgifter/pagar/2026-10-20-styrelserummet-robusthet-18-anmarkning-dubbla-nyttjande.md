# Uppgift: Dubbla namn och utgången nyttjandeperiod

uppdrag: uppdrag/2026-10-20-styrelserummet-robusthet-handlingar.md
försök: 1
beror-på: 2026-10-20-styrelserummet-robusthet-16-anmarkning-efter-arsslut-f2.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/Anmarkningar.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/AnmarkningarTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/BokfortVarde.kt

## Uppgift
Utöka `anmarkningarFor` med:
1. Anmärkning när **två eller fler komponenter har samma namn** (case-sensitive).
2. Anmärkning när **nyttjandeperioden redan gått ut** vid `arsslut` (anskaffningsår + nyttjandeperiod ≤ arsslut) men **bokfört värde** enligt `bokfortVarde(komponent, arsslut)` fortfarande är **större än restvärdet**.

Använd befintlig parameter `arsslut` om den finns; annars lägg till den som i uppgift 16 och anropa med `2025` i testet.

## Exempel
- Två komponenter `"Tak"` → anmärkning om dubbla namn.
- Komponent med nyttjandeperiod slut 2020, `arsslut = 2025`, bokfört värde 100_000, restvärde 0 → anmärkning om utgången period med kvarvarande bokfört värde.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*AnmarkningarTest.anmarkningDubblaNamnOchNyttjande"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
