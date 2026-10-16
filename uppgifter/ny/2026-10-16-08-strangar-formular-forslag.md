# Uppgift: strängar för formulär och förslag

uppdrag: uppdrag/2026-10-16-husloggen-uppgifter.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- husloggen/src/main/res/values/strings.xml
- husloggen/src/main/res/values-sv/strings.xml
- husloggen/src/test/kotlin/se/tmconnect/husloggen/StringsNycklarTest.kt

## Läs som förebild (ändra inte)
- husloggen/src/main/res/values/strings.xml (befintliga nycklar — lägg till, ta inte bort)
- uppgifter/klar/2026-10-16-05-strangar.md

## Uppgift
Lägg till strängar på **engelska** i `values/strings.xml` och **svenska** i `values-sv/strings.xml`. Behåll alla befintliga nycklar.

### Formulär och åtgärder

| Nyckel | Svenska | Engelska |
|--------|---------|----------|
| `fab_ny_uppgift` | Ny uppgift | New task |
| `ny_uppgift_rubrik` | Ny uppgift | New task |
| `andra_uppgift_rubrik` | Ändra uppgift | Edit task |
| `spara` | Spara | Save |
| `avbryt` | Avbryt | Cancel |
| `ta_bort` | Ta bort | Delete |
| `stang` | Stäng | Close |
| `falt_namn` | Namn | Name |
| `falt_intervall_manader` | Intervall (månader) | Interval (months) |
| `falt_startdatum` | Startdatum | Start date |
| `falt_paminnelse` | Påminnelse | Reminder |
| `paminnelse_pa` | På | On |
| `paminnelse_av` | Av | Off |
| `falt_anteckning` | Anteckning | Notes |
| `valj_datum` | Välj datum | Choose date |
| `forslag_rubrik` | Färdiga förslag | Suggestions |
| `enligt_kommunen` | Enligt kommunen | As set by your municipality |
| `enligt_tillverkaren` | Enligt tillverkaren | As set by the manufacturer |
| `fel_namn_saknas` | Ange ett namn | Enter a name |
| `fel_intervall_saknas` | Ange intervall i månader | Enter interval in months |
| `fel_intervall_ogiltigt` | Intervall ska vara 1–120 månader | Interval must be 1–120 months |
| `markera_gjord` | Markera som gjord | Mark as done |
| `uppgift_markerad_gjord` | Uppgiften är markerad som gjord | Task marked as done |
| `bekrafta_ta_bort_uppgift_titel` | Ta bort uppgift? | Delete task? |
| `bekrafta_ta_bort_uppgift_text` | Uppgiften tas bort. Poster i loggen behålls. | The task will be deleted. Log entries are kept. |
| `ja_ta_bort` | Ja, ta bort | Yes, delete |
| `nej` | Nej | No |

### Förslagsnamn

| Nyckel | Svenska | Engelska |
|--------|---------|----------|
| `forslag_rensa_hangrannor` | Rensa hängrännor | Clean gutters |
| `forslag_kontrollera_brandvarnare` | Kontrollera brandvarnare | Test smoke alarms |
| `forslag_byt_filter_ventilation` | Byt filter i ventilationen | Replace ventilation filter |
| `forslag_rensa_golvbrunn` | Rensa golvbrunn | Clean floor drain |
| `forslag_kontrollera_tak` | Kontrollera takets skick | Inspect the roof |
| `forslag_sotning` | Sotning | Chimney sweep |
| `forslag_service_varmepump` | Service av värmepump | Heat pump service |
| `forslag_service_ventilation` | Service av ventilation | Ventilation service |

### Pluralresurser för intervallhjälptext

Samma nycklar i båda språkfiler:

`intervall_manader` (plurals): `%d` månad/månader — English: "Every %d month" / "Every %d months" (quantity one/other).

`intervall_ar` (plurals): `%d` år — English: "Every %d year" / "Every %d years".

Utöka `StringsNycklarTest` med ett test `forslagsNamnFinnsPaBadaSprak()` som kontrollerar att varje nyckel som börjar med `forslag_` finns i båda `strings.xml` (samma logik som nyckeljämförelsen, inklusive plurals om du lägger förslag där — här bara `<string name="forslag_...">`).

## Exempel
- `forslag_sotning` är "Sotning" / "Chimney sweep"
- `enligt_kommunen` är inte samma som ett lagkrav — neutral formulering enligt tabellen

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Ändra Kotlin-filer utom testet ovan.
- Ta bort befintliga nycklar.
- Lägga till beroenden.
