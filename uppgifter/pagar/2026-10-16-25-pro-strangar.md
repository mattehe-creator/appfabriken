# Uppgift: strängar för Pro och gratisgräns

uppdrag: uppdrag/2026-10-16-husloggen-pro-gratisgrans.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- husloggen/src/main/res/values/strings.xml
- husloggen/src/main/res/values-sv/strings.xml

## Läs som förebild (ändra inte)
- husloggen/src/test/kotlin/se/tmconnect/husloggen/StringsNycklarTest.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/GratisGrans.kt

## Uppgift
Lägg till samma nycklar på engelska och svenska:

| Nyckel | Syfte |
|--------|--------|
| `pro` | Kort etikett «Pro» |
| `pro_las_upp` | Knapp «Lås upp Pro» |
| `pro_forklaring` | Förklaring: gratisversionen sparar upp till 15 poster i loggen; Pro är engångsköp utan prenumeration och tar bort gränsen; uppgifter och påminnelser är alltid obegränsade |
| `pro_du_har` | «Du har Pro» när köpet är aktivt |
| `pro_kop_misslyckades` | «Köpet kunde inte genomföras. Försök igen.» |
| `pro_grans_nadd` | «Gränsen är nådd: 15 poster» (samma siffra som `GRATIS_MAX_POSTER`) |
| `pro_inte_nu` | «Inte nu» — stäng Pro-dialogen utan köp |
| `uppgift_gjord_post_ej_sparad` | Kort meddelande när «Markera som gjord» flyttade uppgiften men ingen loggpost sparades utan Pro |

Naturlig engelska i `values/strings.xml`, tydlig svenska i `values-sv/strings.xml`. Ingen prisinformation.

## Exempel
- `StringsNycklarTest` passerar.
- Svenska `pro_forklaring` nämner 15 poster, engångsköp utan prenumeration, och att uppgifter/påminnelser är obegränsade.

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
