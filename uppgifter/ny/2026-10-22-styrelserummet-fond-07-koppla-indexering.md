# Uppgift: Koppla in prisuppräkning i fondplan (API)

uppdrag: uppdrag/2026-10-22-styrelserummet-verktyg-indexering-forandring.md
försök: 1
beror-på: 2026-10-22-styrelserummet-fond-06-indexering.md

cursor:2026-10-22-styrelserummet-verktyg-indexering-forandring

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/VerktygApi.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/VerktygApiTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/FondIndexering.kt
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/VerktygApi.kt (befintlig fondplan)

## Uppgift
Lägg till valfält `arligPrisupprakningProcent: Double?` i `FondplanRequest` (tomt/null = 0). Validera 0–10, annars `VerktygFel` med svenskt meddelande. Anropa `indexeradeAtgarder(planerade, req.franAr, tiondelar)` innan `jamnAvsattning`, `minstaAvsattningUtanUnderskott` och `fondUtveckling` när tiondelar > 0. Uppdatera `anmarkning` när uppräkning används.

## Exempel
- Utan fält: samma uttag som idag för `AtgardDto(2028, 900_000)` från 2026.
- Med `2.0`: uttag 2028 = `kronor(indexeratBelopp(900_000, 2028, 2026, 20))`.
- `10.1` → fel.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*VerktygApiTest.fondplan*"

## Gör inte
- Ändra FondIndexering.kt eller fondplan.html/js (egen uppgift).
- Lägga till beroenden.
