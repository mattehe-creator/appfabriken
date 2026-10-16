# Uppgift: strängar för påminnelser (Husloggen)

uppdrag: uppdrag/2026-10-16-husloggen-paminnelser.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- husloggen/src/main/res/values/strings.xml
- husloggen/src/main/res/values-sv/strings.xml

## Läs som förebild (ändra inte)
- husloggen/src/test/kotlin/se/tmconnect/husloggen/StringsNycklarTest.kt
- garantivalvet/src/main/res/values/strings.xml (sök `paminnelse_`)

## Uppgift
Lägg till samma nycklar på engelska och svenska för **globala** uppgiftspåminnelser (reglaget i Att göra-fliken och notiser från `PaminnelseWorker`). Nycklarna ska matcha det som worker och reglaget använder (sök `R.string.paminnelse_` i `:husloggen` efter Cursors del A).

| Nyckel | Parametrar | Syfte |
|--------|------------|--------|
| `paminnelse_kanal_namn` | — | Notiskanalens namn |
| `paminnelse_notis_titel` | — | Enskild notis: titel |
| `paminnelse_notis_text` | `%1$s` uppgiftens namn, `%2$s` formaterat nästa datum | Enskild notis: brödtext |
| `paminnelse_notis_samlad_titel` | — | Flera uppgifter: titel |
| `paminnelse_notis_samlad_text` | `%1$d` antal | Flera uppgifter: brödtext |
| `paminnelse_installning_rubrik` | — | Reglage: rubrik |
| `paminnelse_installning_beskrivning` | — | Reglage: förklaring (7 dagar före nästa datum) |
| `paminnelse_behorighet_nekad` | — | Text när användaren nekat notisbehörighet |

Använd tydlig svenska och naturlig engelska. Nästa datum formateras i kod med `formateraDatum`; strängen ska bara innehålla platshållaren `%2$s`.

## Exempel
- Engelska `paminnelse_notis_text`: `"%1$s is due on %2$s"` (eller liknande naturlig formulering).
- `StringsNycklarTest` passerar.

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
