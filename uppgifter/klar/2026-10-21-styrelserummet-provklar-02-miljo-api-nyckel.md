# Uppgift: Miljökontroll API-nyckel

uppdrag: uppdrag/2026-10-21-styrelserummet-provklar.md
försök: 1
beror-på: 2026-10-21-styrelserummet-provklar-01-miljo-data-skrivbar-f2.md

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Miljo.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/MiljoTest.kt

## Uppgift
Utöka `kontrolleraMiljo` med:
1. Saknad eller tom `ANTHROPIC_API_KEY` → **fel** (produktion behöver nyckel; nämn variabelnamnet).
2. Nyckel som inte börjar med `sk-ant-` → **fel** om ogiltigt format.

**Nyckelvärdet får aldrig** förekomma i någon returnerad sträng (testa med nyckeln `sk-ant-hemlig-xyz` och `fel-nyckel` — assert att varken `hemlig` eller hela nyckeln finns i listan).

Behåll kontroller från uppgift 01.

## Exempel
- Indata: `ANTHROPIC_API_KEY` saknas → minst ett fel, utan nyckeltext.
- Indata: `ANTHROPIC_API_KEY` = `fel-prefix-123` → fel om format; listan innehåller inte `fel-prefix-123`.
- Indata: `ANTHROPIC_API_KEY` = `sk-ant-test` → inget fel från nyckelkontrollen (datakatalog fortfarande ok enligt exempel i 01).

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*MiljoTest"

## Gör inte
- Ändra andra filer.
- Logga eller returnera nyckelvärden.
- Lägga till beroenden.

utfall: godkänd 2026-10-21 (MiljoTest: datakatalog + nyckel i env, test för att nyckelvärde inte läcker)
