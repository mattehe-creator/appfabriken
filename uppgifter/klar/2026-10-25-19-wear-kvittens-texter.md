# Uppgift: Kvittenstexter på klockan (svenska)

uppdrag: uppdrag/vantar/2026-10-25-rostfangst-klocka.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/wear/KvittensTexter.kt (ny)
- rostfangst/src/test/kotlin/se/tmconnect/rostfangst/wear/KvittensTexterTest.kt (ny)

## Läs som förebild (ändra inte)
- rostfangst/src/main/res/values-sv/strings.xml

## Uppgift
Enum `Overforing` med värden `VANTAR`, `SKICKAD`, `INGEN_TELEFON`, `FEL`.

`fun kvittensText(tillstand: Overforing): String` returnerar kort svensk text för klockskärm:
- `VANTAR` → `"Skickar…"`
- `SKICKAD` → `"Skickat till telefonen"`
- `INGEN_TELEFON` → `"Ingen telefon i närheten"`
- `FEL` → `"Kunde inte skicka"`

Texterna ska matcha strängnycklarna som Cursor lägger i `:rostfangst-wear` (samma ordalydelse).

## Exempel
- `kvittensText(Overforing.INGEN_TELEFON)` → exakt `"Ingen telefon i närheten"`.

## Test som ska gå igenom
gradle :rostfangst:testDebugUnitTest --tests "*KvittensTexterTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.

utfall: godkänd 2026-10-25 av Claude (reservläge, inget rättat)
