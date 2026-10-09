# Uppgift: nedskalad förhandsvisning av kvitto i formuläret

uppdrag: uppdrag/2026-10-09-kvittofoto.md
försök: 1
beror-på: 2026-10-09-04-detalj-kvitto-miniatur-f2.md

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopFormularSkarm.kt
- garantivalvet/src/test/kotlin/se/tmconnect/garantivalvet/regler/KvittoSkalningTest.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/KvittoSkalning.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/data/KvittoLager.kt (avkodning i två steg med inJustDecodeBounds)

## Uppgift
Förhandsvisningen i `KopFormularSkarm` avkodar i dag bilden i full storlek med `BitmapFactory.decodeStream(input)`. Ett nytaget kamerafoto (t.ex. 4000 x 3000) blir då en bitmap på ca 48 MB för en ruta som bara är 120 dp hög, och appen kan ta slut på minnet.

1. I `KopFormularSkarm`, inuti `remember(kvittoUri) { ... }`: läs först bara måtten (`BitmapFactory.Options().apply { inJustDecodeBounds = true }`) via `context.contentResolver.openInputStream(kvittoUri)`.
2. Beräkna `val sampleSize = beraknaInSampleSize(options.outWidth, options.outHeight, maxSida = 480)`.
3. Öppna strömmen igen och avkoda med `BitmapFactory.Options().apply { inSampleSize = sampleSize }`.
4. Behåll `try/catch` som ger `null` vid fel, och resten av UI:t oförändrat.
5. Lägg till ett test i `KvittoSkalningTest` för `maxSida = 480`.

## Exempel
- `beraknaInSampleSize(4000, 3000, maxSida = 480)` → 8 (bilden blir 500 x 375).
- `beraknaInSampleSize(400, 300, maxSida = 480)` → 1.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*KvittoSkalningTest"

## Gör inte
- Ändra andra filer, inklusive `KvittoSkalning.kt` och `KvittoLager.kt`.
- Lägga till beroenden.
- Ändra databasschemat.
