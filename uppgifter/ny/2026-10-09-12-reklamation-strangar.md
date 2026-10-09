# Uppgift: strängar för reklamationsrätt

uppdrag: uppdrag/2026-10-09-reklamationsratt.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- garantivalvet/src/main/res/values/strings.xml
- garantivalvet/src/main/res/values-sv/strings.xml

## Läs som förebild (ändra inte)
- garantivalvet/src/test/kotlin/se/tmconnect/garantivalvet/StringsNycklarTest.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/Garanti.kt

## Uppgift
Lägg till samma nycklar på engelska och svenska för reklamationsrätt i listan och detaljvyn:

| Nyckel | Parametrar | Syfte |
|--------|------------|--------|
| `status_reklamation_galler` | — | Samlad liststatus när reklamationsrätten gäller (efter utgången garanti) |
| `detalj_reklamation_till` | `%1$s` formaterat slutdatum | Rad: reklamationsfristens sista dag |
| `detalj_reklamation_rubrik` | — | Rubrik/fetstil ovanför förklaringen |
| `detalj_reklamation_forklaring` | — | Kort förklaring enligt uppdraget: tre år att reklamera, två års presumtion, räknas från köpdatum, näringsidkare |
| `detalj_reklamation_vagledning` | — | Tydligt att innehållet är vägledning, inte juridisk rådgivning |
| `detalj_reklamation_aldre_lag` | — | Köp före 1 maj 2022 omfattas av äldre regler (visas när det finns ingen reklamationsfrist i appen men köpet är gammalt nog att nämna det — se uppgift 16) |

Använd tydlig svenska och naturlig engelska. Datum i `detalj_reklamation_till` kommer från `formateraDatum` i kod; strängen ska bara innehålla `%1$s`.

## Exempel
- `StringsNycklarTest` passerar.
- Svenska `status_reklamation_galler` kan läsa ungefär: «Reklamationsrätt gäller».

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
