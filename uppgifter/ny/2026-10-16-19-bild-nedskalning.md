# Uppgift: beräkna nedskalad bildstorlek

uppdrag: uppdrag/2026-10-16-husloggen-bilder.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/BildSkalning.kt (ny)
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/BildSkalningTest.kt (ny)

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/KvittoSkalning.kt

## Uppgift
Ren funktion (standard max längsta sida 2000 px):

```kotlin
fun beraknaNedskaladStorlek(
    bredd: Int,
    hojd: Int,
    maxLangstaSida: Int = 2000,
): Pair<Int, Int>
```

Längsta sidan ska högst bli `maxLangstaSida`, proportioner bibehålls (avrunda till heltal). Mindre bilder ändras inte. Ogiltiga mått (≤ 0) returneras oförändrade (samma som förebilden).

## Exempel
- Indata: 4000×3000 → Förväntat: 2000×1500
- Indata: 1000×800 → Förväntat: 1000×800
- Indata: 3000×3000 → Förväntat: 2000×2000

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*BildSkalningTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
