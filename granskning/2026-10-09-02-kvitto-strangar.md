beslut: underkänd
granskare: cursor
datum: 2026-10-09

## Kontrollerat
- Grenen innehåller bara flytt av uppgiftsfilen till `uppgifter/pagar/`; varken `garantivalvet/src/main/res/values/strings.xml` eller `values-sv/strings.xml` är ändrade (uppgiften krävde enbart dessa två).
- Kvittosträngarna (`kvitto_*`) saknas helt i båda språkfilerna — uppgiftens tabell är inte implementerad.
- Inga filer i fel sökväg (t.ex. repots rot); inga oavsiktliga filändringar.
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Databasschemat oförändrat; ingen UI-kod tillagd.
- `StringsNycklarTest` (JUnit 4) skulle fortfarande gå grönt utan ändring eftersom testet bara jämför nyckelmängder mellan språken, inte att kvittonycklarna finns — därför räcker det inte att bara köra testet; strängarna måste läggas in enligt tabellen.
- PR #14 / lokalt körskript rapporterade röda tester och att strängfilerna saknades i leveransen.

## Rättat av Cursor
- inget (helt saknad implementation — inte småfel)

## Vid underkänd: försök 2

# Uppgift: strängar för kvittofoto

## Vad som saknades (försök 1)
- Inga ändringar i `garantivalvet/src/main/res/values/strings.xml` eller `garantivalvet/src/main/res/values-sv/strings.xml`. Endast uppgiftsfilen flyttades till `pagar/`.
- Alla åtta `kvitto_*`-nycklar från tabellen måste finnas i **båda** filerna med exakt de engelska respektive svenska texterna.

uppdrag: uppdrag/2026-10-09-kvittofoto.md
försök: 2
beror-på: ingen

## Ändra bara dessa filer
- garantivalvet/src/main/res/values/strings.xml
- garantivalvet/src/main/res/values-sv/strings.xml

## Läs som förebild (ändra inte)
- garantivalvet/src/test/kotlin/se/tmconnect/garantivalvet/StringsNycklarTest.kt

## Uppgift
Lägg till strängar (samma nycklar på engelska och svenska) för kvittofunktionen. Placera dem i modulen **garantivalvet**, inte i repots rot. Lägg till raderna före `</resources>` i varje fil.

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

Exempel på format i `values/strings.xml` (övriga strängar ska vara kvar):

```xml
    <string name="kvitto_valj_bild">Choose image</string>
    <string name="kvitto_ta_foto">Take photo</string>
```

Motsvarande nycklar med svenska texter i `values-sv/strings.xml`.

## Exempel
- Efter ändringen: `StringsNycklarTest` hittar samma nycklar i båda filerna (inklusive alla åtta `kvitto_*`).

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.

## Frågor till Claude
- inga
