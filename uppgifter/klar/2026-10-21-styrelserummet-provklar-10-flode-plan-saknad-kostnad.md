# Uppgift: Syntetisk plan med saknad kostnad för en post

uppdrag: uppdrag/2026-10-21-styrelserummet-provklar.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/SyntetiskPlanSaknadKostnadTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/ByggUtkastTest.kt (testet `fordelaSaknadeVardeEnligtTabell`)

## Uppgift
Test `planSaknadKostnadGerUppskattning`:

1. PDF med minst två åtgärder; stub-JSON: en komponent med `anskaffningsvarde`, en med `"anskaffningsvarde": null` och grupp FASAD, plus `"anmarkningar":["Kostnad saknas i planen."]` (påhittad text).
2. Byggnadens värde i årsredovisning så att `fordela` kan fylla saknat värde och avstämning **stammer**.
3. Assert: komponenten utan kostnad får `Sakerhet.UPPSKATTNING`, anmärkning om uppskattat värde finns, avstämning ok.

## Exempel
- Stomme 6_300_000, Fasad null → fördelat värde + UPPSKATTNING, summa = byggnad (liknande befintligt ByggUtkastTest-exempel men med PDF-generering i testet).

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*SyntetiskPlanSaknadKostnadTest"

## Gör inte
- Ändra fördelningslogik om testet redan går grönt.
- Riktiga belopp från verkliga föreningar.

utfall: godkänd 2026-10-21 (PDF-flöde, PdfLasare, assert på fördelat värde och uppskattningsanmärkning)
