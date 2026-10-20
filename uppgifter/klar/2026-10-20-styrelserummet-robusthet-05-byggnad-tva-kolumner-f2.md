# Uppgift: Två kolumner och årtalsrubrik

uppdrag: uppdrag/2026-10-20-styrelserummet-robusthet-handlingar.md
försök: 2
beror-på: ingen

## Vad som saknades
- Fallback-regexen `$rubrik[^\d\n]*([\d]...)` hittar **inte** belopp på en senare rad. För flerradig tabell (`rubrik` → årtalsrad → beloppsrad) blev `regex.find` null och funktionen returnerade null.
- När nästa rad matchade årtalsmönster togs **första årtalet** (t.ex. 2024) med `parseBelopp`, i stället för **första beloppet på raden under** (t.ex. `12 500 000`).
- På en rad som `Byggnader och mark 2024 2023` matchade regex årtalet 2024 som belopp; uppgiften kräver `null` eftersom det bara är kolumnrubriker, inga belopp.
- Byt inte `continue` till `return null` i befintliga tkr/mnkr-grenar om det inte behövs för exemplen — det ändrar beteende när flera rubriker testas i loopen.

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/HittaByggnadensVarde.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/HittaByggnadensVardeTest.kt

## Uppgift
Utöka `hittaByggnadensVarde` för tabellrader med **två belopp** (innevarande år och föregående år):

1. Hitta raden med rubriken `Byggnader och mark` eller `Byggnader` (befintlig ordning i `rubriker`).
2. Om **nästa rad** bara är två fyrsiffriga årtal separerade med mellanslag (t.ex. `2024 2023`), är det en **årtalsrad** — inte belopp.
3. Då ska **första beloppet** läsas från **raden efter årtalsraden**: texten före andra beloppet på samma rad (t.ex. `12 500 000` ur `12 500 000 11 800 000`). Använd `parseBelopp(..., iTusentals = false)` om inget tkr/mnkr/tusen finns i kontexten på den raden; befintlig tkr/mnkr-logik ska fortfarande gälla om enheten står på beloppsraden.
4. Om rubrikraden själv innehåller bara årtal efter rubriken (t.ex. `Byggnader och mark 2024 2023`) och det **inte** finns en separat beloppsrad under, returnera `null` — årtalen är inte belopp.
5. Enkolumnfall (t.ex. `Byggnader 10 000 000` på samma rad) ska fungera som idag.

## Exempel
- Indata:
  ```
  Byggnader och mark
  2024 2023
  12 500 000 11 800 000
  ```
  Förväntat: `Belopp(12500000, iTusentals = false)`
- Indata: `Byggnader och mark 2024 2023` (inga belopp) → `null`
- Indata: `Byggnader 10 000 000` (en kolumn) → `Belopp(10000000, iTusentals = false)`

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*HittaByggnadensVardeTest.hittaByggnadensVardeTvaKolumner"

Testmetoden ska asserta exakt de tre exemplen ovan (inga extra fall som inte står i uppgiften).

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra datamodellen.

utfall: godkänd 2026-10-20 (hela implementationen — lokal modell levererade ingen kod)
