# Uppgift: Saknad jobbkatalog mitt i körning

uppdrag: uppdrag/2026-10-21-styrelserummet-provklar.md
försök: 1
beror-på: 2026-10-21-styrelserummet-provklar-13-lagring-samtidig-skrivning.md

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/JobbLagring.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/JobbLagringTest.kt

## Uppgift
Test `saknadJobbKatalogGerNullInteUndantag`:

1. Skapa jobb, radera hela jobbkatalogen manuellt från filsystemet (inte via `raderaJobb` om den kräver att mappen finns).
2. Anrop `lasMeta`, `lasUtkast`, `listaFiler`, `sparaUtkast` ska ge **null/tom lista** eller motsvarande felsvar — **inte** okontrollerat undantag som bubblar ut.

Om `JobbKo` eller API redan hanterar detta via `null`, testa minst `lasMeta` och `sparaUtkast`/`lasUtkast` enligt nuvarande beteende och dokumentera i assert.

Ändra produktionskod endast om test avslöjar krasch.

## Exempel
- Katalog borta → `lasMeta(id)` → `null` utan thrown Exception.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*JobbLagringTest.saknadJobbKatalog*"

## Gör inte
- Ändra HTTP-lager i denna uppgift.

utfall: godkänd 2026-10-21 (import exists och korrekt UtkastDto i test)
