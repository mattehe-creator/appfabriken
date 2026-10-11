# Uppgift: strängar för Pro och gratisgräns

uppdrag: uppdrag/2026-10-11-pro-gratisgrans.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- garantivalvet/src/main/res/values/strings.xml
- garantivalvet/src/main/res/values-sv/strings.xml

## Läs som förebild (ändra inte)
- garantivalvet/src/test/kotlin/se/tmconnect/garantivalvet/StringsNycklarTest.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/GratisGrans.kt

## Uppgift
Lägg till samma nycklar på engelska och svenska:

| Nyckel | Syfte |
|--------|--------|
| `pro` | Kort etikett «Pro» |
| `pro_las_upp` | Knapp «Lås upp Pro» |
| `pro_forklaring` | Förklaring i Pro-dialogen: gratisversionen sparar upp till 15 köp; Pro är engångsköp utan prenumeration och låser upp obegränsat antal köp |
| `pro_du_har` | «Du har Pro» när köpet är aktivt |
| `pro_kop_misslyckades` | «Köpet kunde inte genomföras. Försök igen.» vid fel (visas senare i UI) |
| `pro_grans_nadd` | «Gränsen är nådd: 15 köp» (valfritt i dialog/lista; samma siffra som `GRATIS_MAX_KOP`) |
| `pro_inte_nu` | «Inte nu» — stäng Pro-dialogen utan köp |

Naturlig engelska i `values/strings.xml`, tydlig svenska i `values-sv/strings.xml`. Ingen prisinformation.

## Exempel
- `StringsNycklarTest` passerar.
- Svenska `pro_forklaring` nämner 15 köp och engångsköp utan prenumeration.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
