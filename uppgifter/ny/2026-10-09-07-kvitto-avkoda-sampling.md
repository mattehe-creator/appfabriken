# Uppgift: avkoda stora kvittobilder utan att minnet tar slut

uppdrag: uppdrag/2026-10-09-kvittofoto.md
försök: 1
beror-på: 2026-10-09-01-kvitto-skala-enhetstest.md

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/KvittoSkalning.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/data/KvittoLager.kt
- garantivalvet/src/test/kotlin/se/tmconnect/garantivalvet/regler/KvittoSamplingTest.kt (ny)

## Läs som förebild (ändra inte)
- garantivalvet/src/test/kotlin/se/tmconnect/garantivalvet/regler/FormularValideringTest.kt

## Uppgift
I dag avkodas hela bilden i full storlek innan den skalas ned. Ett foto på 48 megapixel kräver då cirka 190 MB och kan krascha appen.

1. Lägg till i `KvittoSkalning.kt` (ren Kotlin):
   `fun beraknaInSampleSize(bredd: Int, hojd: Int, maxSida: Int = 2000): Int`
   Returnera den största tvåpotensen `n` (1, 2, 4, 8 ...) sådan att `max(bredd, hojd) / n >= maxSida`. Är bilden redan högst `maxSida`, returnera 1.
2. I `KvittoLager.kopieraFranUri`: läs först bara storleken med `BitmapFactory.Options().apply { inJustDecodeBounds = true }` (öppna strömmen en gång för detta), räkna `inSampleSize` med funktionen ovan, och öppna strömmen igen för den riktiga avkodningen med `inSampleSize` satt. Resten (nedskalning till högst 2000 px, JPEG 85) är oförändrat.

## Exempel
- `beraknaInSampleSize(8000, 6000)` → `4` (8000/4 = 2000)
- `beraknaInSampleSize(4000, 3000)` → `2`
- `beraknaInSampleSize(3999, 3000)` → `1` (3999/2 = 1999, under 2000)
- `beraknaInSampleSize(1200, 800)` → `1`
- `beraknaInSampleSize(16000, 9000)` → `8`

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*KvittoSamplingTest"

Ett testfall per exempel, JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals`).

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra hur filen sparas eller vad funktionen returnerar.
