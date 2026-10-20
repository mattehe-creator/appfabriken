# Uppgift: Anmärkning anskaffningsår efter räkenskapsåret

uppdrag: uppdrag/2026-10-20-styrelserummet-robusthet-handlingar.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/Anmarkningar.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/AnmarkningarTest.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/Utkast.kt

## Uppgift
Lägg till parameter `arsslut: Int` i `anmarkningarFor` (sista parametern). Uppdatera anropet i `ByggUtkast.byggUtkast` så att `nuAr` skickas in som `arsslut`.

Ny regel: anmärkning när komponentens **anskaffningsår är efter räkenskapsårets slut** (`arsslut`).

## Exempel
- `arsslut = 2025`, komponent `anskaffningsar = 2026` → svensk anmärkning.
- `anskaffningsar = 2025` → ingen anmärkning för den regeln.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*AnmarkningarTest.anmarkningAnskaffningsarEfterArsslut"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra `Komponent` eller `Forening`.

utfall: underkänd 2026-10-20 av Cursor (PR #305). Ersatt av 2026-10-20-styrelserummet-robusthet-16-anmarkning-efter-arsslut-f2.md
