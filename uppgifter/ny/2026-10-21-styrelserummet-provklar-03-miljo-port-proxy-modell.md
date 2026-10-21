# Uppgift: Miljökontroll port, proxy och modell

uppdrag: uppdrag/2026-10-21-styrelserummet-provklar.md
försök: 1
beror-på: 2026-10-21-styrelserummet-provklar-02-miljo-api-nyckel.md

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Miljo.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/MiljoTest.kt

## Uppgift
Utöka `kontrolleraMiljo` med:
1. **`PORT`**: om variabeln är satt och inte tom — måste vara heltal **1–65535**. Annars **fel**. Om `PORT` saknas eller är tom ska standard 8080 accepteras utan fel från denna kontroll.
2. **`STYRELSERUMMET_PROXY`**: om satt till något annat än tom sträng eller `"1"` → **varning** (proxy bör vara `1` bakom omvänd proxy enligt DRIFT.md).
3. **`STYRELSERUMMET_MODELL`**: tom sträng → **fel**. Värde som inte finns i listan `GILTIGA_MODELLER` (minst `claude-sonnet-5-5`, samma standard som `anthropicTolkareFranMiljo`) → **varning**. Saknad variabel eller standardmodell → ingen varning.

Exportera `GILTIGA_MODELLER` som `Set<String>` i samma fil om test behöver den.

## Exempel
- `PORT` = `0` → fel.
- `PORT` = `99999` → fel.
- `PORT` = `8080` → inget portfel.
- `STYRELSERUMMET_PROXY` = `true` → varning.
- `STYRELSERUMMET_MODELL` = `` → fel.
- `STYRELSERUMMET_MODELL` = `claude-opus-99` → varning.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*MiljoTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
