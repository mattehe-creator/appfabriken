# Uppgift: strängar för kvittofoto

uppdrag: uppdrag/2026-10-09-kvittofoto.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- garantivalvet/src/main/res/values/strings.xml
- garantivalvet/src/main/res/values-sv/strings.xml

## Läs som förebild (ändra inte)
- garantivalvet/src/test/kotlin/se/tmconnect/garantivalvet/StringsNycklarTest.kt

## Uppgift
Lägg till strängar (samma nycklar på engelska och svenska) för kvittofunktionen:

| Nyckel | Engelska (values) | Svenska (values-sv) |
|--------|-------------------|---------------------|
| `kvitto_valj_bild` | Choose image | Välj bild |
| `kvitto_ta_foto` | Take photo | Ta foto |
| `kvitto_ta_bort` | Remove receipt | Ta bort kvitto |
| `kvitto_fornhandsvisning` | Receipt preview | Förhandsvisning av kvitto |
| `kvitto_har_kvitto` | Has receipt | Har kvitto |
| `kvitto_oppna` | Open receipt | Öppna kvitto |
| `kvitto_dela` | Share | Dela |
| `kvitto_stang` | Close | Stäng |

## Exempel
- `StringsNycklarTest` hittar samma nycklar i båda filerna.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
