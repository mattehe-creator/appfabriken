# Uppgift: säkerhetskopia utan saknade kvitton

uppdrag: uppdrag/2026-10-11-export-sakerhetskopia.md
försök: 1
beror-på: cursor:2026-10-11-export-sakerhetskopia

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/export/Sakerhetskopia.kt
- garantivalvet/src/test/kotlin/se/tmconnect/garantivalvet/export/SakerhetskopiaZipTest.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/export/SakerhetskopiaModell.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/data/KopRepository.kt

## Uppgift
Två fel i `Sakerhetskopia.kt` (rättelse efter Claudes efterhandsgranskning):

1. I `Sakerhetskopia.skriv` skrivs `kop.json` med `kvittoFil` även när kvittot inte följer med i ZIP-filen (filen saknas på disk, `kvittoLager.lasRåBytes` ger null, eller filen är större än `SAKERHETSKOPIA_MAX_FIL_BYTES`). Vid import ger `KopRepository.importeraSakerhetskopia` då fel, och hela säkerhetskopian går inte att importera. Rätta så här: läs först kvittobytes för varje köp. Om bytes saknas eller är för stora, sätt `kvittoFil = null` på det köpet (`copy(kvittoFil = null)`). Skriv sedan `kop.json` från den rättade listan, och därefter kvittoposterna. Köpet följer alltså med utan kvitto.
2. `ByteArrayOutputStreamLimited` lagrar varje byte i en `ArrayList<Byte>`, vilket kräver mycket minne för stora kvitton. Byt den mot `java.io.ByteArrayOutputStream` och räkna storleken själv i `lasBegransadeBytes`: kasta `IllegalStateException` om summan överstiger `maxBytes`, som i dag. Ta bort klassen `ByteArrayOutputStreamLimited`.

Inget annat beteende ändras.

## Exempel
- Ett köp med `kvittoFil = "finns-inte.jpg"` som inte finns i KvittoLager → `skriv` lyckas, `lasIn` ger `Ok`, köpet har `kvittoFil == null` och `kvittoFiler` är tom.
- Ett köp med kvitto som finns (5 byte) → oförändrat: `kvittoFil` är filnamnet och bytes är lika.
- En ZIP-post på 10 byte läst med gräns 20 MB → samma bytes som förut.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*SakerhetskopiaZipTest"

Lägg till testet `saknatKvittoExporterasUtanKvittoFil` enligt första exemplet. Befintliga tester i filen ska fortsätta gå igenom.

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
- Ändra JSON-formatet eller `SAKERHETSKOPIA_VERSION`.
