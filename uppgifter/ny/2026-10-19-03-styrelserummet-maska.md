# Uppgift: Maskera personuppgifter

uppdrag: uppdrag/2026-10-19-styrelserummet-k3-karna.md
försök: 1
beror-på: cursor:styrelserummet-webbtj-nst-k3-7da6

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/Maska.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/MaskaTest.kt (ny)

## Läs som förebild (ändra inte)
- uppdrag/vantar/2026-10-19-styrelserummet-underlag.md (avsnitt Del B, maska)

## Uppgift
Implementera `maskaPersonuppgifter(text: String): String` enligt uppdraget underlag.

## Exempel
- `800101-1234` → `[personnummer]`
- `anna@exempel.se` → `[e-post]`
- `070-123 45 67` → `[telefon]`
- Org.nr `769612-3456` ska **inte** maskeras.
- Belopp `1 250 000` och årtal `2025` ska inte maskeras.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*MaskaTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
