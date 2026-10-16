# Uppgift: test för nedskalad bildstorlek

uppdrag: uppdrag/2026-10-16-husloggen-bilder.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- karna/src/test/kotlin/se/tmconnect/karna/bild/BildSkalningTest.kt (ny)

## Läs som förebild (ändra inte)
- karna/src/main/kotlin/se/tmconnect/karna/bild/BildSkalning.kt (finns redan från del A, ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/KvittoSkalning.kt

## Uppgift
`beraknaNedskaladStorlek(bredd, hojd, maxLangstaSida = 2000): Pair<Int, Int>` och `beraknaInSampleSize(bredd, hojd, maxSida = 2000): Int` finns redan (internal) i `BildSkalning.kt` i `:karna`. Skriv enhetstester för båda i samma modul. Hittar du ett fel: rätta det inte, skriv det i PR-beskrivningen.

## Exempel
- 4000×3000 → 2000×1500
- 1000×800 → 1000×800
- 3000×3000 → 2000×2000
- 0×500 → 0×500 (ogiltiga mått oförändrade)
- `beraknaInSampleSize(1000, 800)` → 1
- `beraknaInSampleSize(4000, 3000)` → 2
- `beraknaInSampleSize(8000, 6000)` → 4

## Test som ska gå igenom
gradle :karna:testDebugUnitTest --tests "*BildSkalningTest"

## Gör inte
- Ändra andra filer än testfilen.
- Lägga till beroenden.
- Ändra databasschemat.

utfall: godkänd 2026-10-16 (bytte kotlin.test till org.junit.Assert.assertEquals)
