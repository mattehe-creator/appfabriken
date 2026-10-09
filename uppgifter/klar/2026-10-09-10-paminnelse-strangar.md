# Uppgift: strängar för påminnelser

uppdrag: uppdrag/2026-10-09-paminnelser.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- garantivalvet/src/main/res/values/strings.xml
- garantivalvet/src/main/res/values-sv/strings.xml

## Läs som förebild (ändra inte)
- garantivalvet/src/test/kotlin/se/tmconnect/garantivalvet/StringsNycklarTest.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/paminnelse/PaminnelseWorker.kt

## Uppgift
Lägg till (eller justera om Cursors del A-PR redan lagt in dem) samma nycklar på engelska och svenska för påminnelser. Nycklarna ska matcha det som `PaminnelseWorker` och inställnings-UI använder (sök efter `R.string.paminnelse_` i repot).

| Nyckel | Parametrar | Syfte |
|--------|------------|--------|
| `paminnelse_kanal_namn` | — | Notiskanalens namn (inställningar → Notiser) |
| `paminnelse_notis_titel` | — | Enskild notis: titel |
| `paminnelse_notis_text` | `%1$s` köpets namn, `%2$s` formaterat slutdatum | Enskild notis: brödtext |
| `paminnelse_notis_samlad_titel` | — | Flera frister: titel |
| `paminnelse_notis_samlad_text` | `%1$d` antal | Flera frister: brödtext |
| `paminnelse_installning_rubrik` | — | Reglage/rad: rubrik |
| `paminnelse_installning_beskrivning` | — | Reglage/rad: förklaring |
| `paminnelse_behorighet_nekad` | — | Text när användaren nekat notisbehörighet |

Använd tydlig svenska och naturlig engelska. Slutdatum i notistexten formateras i kod med `formateraDatum`; strängen ska bara innehålla platshållaren `%2$s`.

## Exempel
- `StringsNycklarTest` passerar.
- Engelska `paminnelse_notis_text` kan läsa: `Warranty for %1$s ends on %2$s`.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.

utfall: godkänd 2026-10-09 (inget)
