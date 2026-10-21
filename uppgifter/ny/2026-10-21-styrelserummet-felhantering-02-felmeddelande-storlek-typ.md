# Uppgift: Felmeddelande för för stor fil och fel filtyp

uppdrag: uppdrag/2026-10-21-styrelserummet-felhantering.md
försök: 1
beror-på: 2026-10-21-styrelserummet-felhantering-01-felmeddelande-pdf-text-krypterad.md

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/FelHantering.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/FelHanteringTest.kt

## Läs som förebild (ändra inte)
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Granser.kt
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Application.kt

## Uppgift
Utöka `ServerFel` med:
- `ForStorFil` — total uppladdning över `MAX_BYTES_PER_JOBB`
- `IntePdf` — filen börjar inte med `%PDF-`

Utöka `felTillMeddelande` för dessa fall. Nämn gärna gränsen 25 MB i klartext för `ForStorFil` (samma som `MAX_BYTES_PER_JOBB`).

## Exempel
- `felTillMeddelande(ServerFel.ForStorFil)` → svenskt meddelande om att filerna är för stora tillsammans, med ungefärlig gräns.
- `felTillMeddelande(ServerFel.IntePdf)` → svenskt meddelande om att bara PDF-filer accepteras (ersätter inte hårdkodad sträng i `Application.kt` förrän senare uppgift kopplar in).

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*FelHanteringTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
