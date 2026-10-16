# Uppgift: strängar för logg och postformulär

uppdrag: uppdrag/2026-10-16-husloggen-loggen.md
försök: 1
beror-på: cursor:2026-10-16-husloggen-loggen

## Ändra bara dessa filer
- husloggen/src/main/res/values/strings.xml
- husloggen/src/main/res/values-sv/strings.xml
- husloggen/src/test/kotlin/se/tmconnect/husloggen/StringsNycklarTest.kt

## Läs som förebild (ändra inte)
- husloggen/src/main/res/values/strings.xml (befintliga nycklar — lägg till, ta inte bort)
- uppgifter/klar/2026-10-16-05-strangar.md (tabellformat)

## Uppgift
Lägg till strängar på **engelska** i `values/strings.xml` och **svenska** i `values-sv/strings.xml`. Behåll alla befintliga nycklar.

Minst dessa nycklar (samma `name` i båda filer):

| Nyckel | Svenska | Engelska |
|--------|---------|----------|
| `fab_ny_post` | Ny post | New entry |
| `ny_post_rubrik` | Ny post | New entry |
| `andra_post_rubrik` | Ändra post | Edit entry |
| `post_detalj_rubrik` | Post | Entry |
| `falt_vad` | Vad som gjordes | What was done |
| `falt_datum` | Datum | Date |
| `falt_typ` | Typ | Type |
| `falt_belopp` | Belopp (kr) | Amount (SEK) |
| `falt_utforare` | Vem utförde arbetet | Who did the work |
| `falt_uppgift` | Kopplad uppgift | Linked task |
| `ingen_uppgift` | Ingen uppgift | No task |
| `fel_vad_saknas` | Ange vad som gjordes | Enter what was done |
| `fel_datum_framtid` | Datum får inte ligga i framtiden | Date cannot be in the future |
| `fel_belopp_ogiltigt` | Ogiltigt belopp | Invalid amount |
| `bekrafta_ta_bort_post_titel` | Ta bort post? | Delete entry? |
| `bekrafta_ta_bort_post_text` | Posten tas bort permanent. | This entry will be deleted permanently. |
| `knapp_andra` | Ändra | Edit |

Uppdatera `StringsNycklarTest` så att nyckelmängderna fortfarande matchar mellan språken.

## Exempel
- `fel_vad_saknas` är "Ange vad som gjordes" på svenska och "Enter what was done" på engelska.

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Ändra Kotlin-filer utom testet ovan.
- Ta bort befintliga nycklar.
- Lägga till beroenden.
