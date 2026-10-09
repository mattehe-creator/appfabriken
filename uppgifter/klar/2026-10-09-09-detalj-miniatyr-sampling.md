# Uppgift: nedskalad kvittominiatyr i detaljvyn

uppdrag: uppdrag/2026-10-09-kvittofoto.md
försök: 1
beror-på: 2026-10-09-08-formular-forhandsvisning-sampling.md

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopDetaljSkarm.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopFormularSkarm.kt (förhandsvisningen efter uppgift 08)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/KvittoSkalning.kt

## Uppgift
Miniatyren i `KopDetaljSkarm` avkodar i dag den sparade bilden i full storlek med `BitmapFactory.decodeStream(stream)`, utan felhantering. En sparad bild på 2000 x 1500 blir ca 12 MB för en ruta som är 160 dp hög, och saknas filen kraschar vyn.

1. Inuti `remember(uri) { ... }`: läs först bara måtten (`BitmapFactory.Options().apply { inJustDecodeBounds = true }`) via `context.contentResolver.openInputStream(uri)`.
2. Beräkna `val sampleSize = beraknaInSampleSize(options.outWidth, options.outHeight, maxSida = 480)`.
3. Öppna strömmen igen och avkoda med `BitmapFactory.Options().apply { inSampleSize = sampleSize }`.
4. Omge avkodningen med `try/catch (e: Exception)` som ger `null`. Då visas ingen miniatyr.
5. Resten av vyn (storlek, klick, innehållsbeskrivning, placering) lämnas oförändrad.

## Exempel
- Sparad bild 2000 x 1500 → `beraknaInSampleSize(2000, 1500, maxSida = 480)` = 4, bitmap 500 x 375.
- Sparad bild 400 x 300 → sampleSize 1.
- Filen borttagen → ingen miniatyr, ingen krasch.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*KvittoSkalningTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.

utfall: godkänd 2026-10-09 (inget)
