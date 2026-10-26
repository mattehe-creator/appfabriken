# Uppgift: Multipart-kropp

uppdrag: uppdrag/vantar/2026-10-25-rostfangst-lokal-ai.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/lokal/Multipart.kt (ny)
- rostfangst/src/test/kotlin/se/tmconnect/rostfangst/lokal/MultipartTest.kt (ny)

## Läs som förebild (ändra inte)
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/wear/UtforMeddelande.kt
- rostfangst/src/test/kotlin/se/tmconnect/rostfangst/wear/UtforMeddelandeTest.kt

## Uppgift
Paket `se.tmconnect.rostfangst.lokal`. Ren Kotlin, ingen `android.*`. JUnit 4, utan Robolectric.

`fun byggMultipart(grans: String, falt: List<Pair<String, String>>, filnamn: String, filtyp: String, fil: ByteArray): ByteArray`

Kroppen byggs i den här ordningen, med radbrytning `\r\n` (CR LF) överallt, och textdelarna kodas som UTF-8:
1. För varje par `(namn, värde)` i `falt`, i listans ordning:
   `--{grans}\r\nContent-Disposition: form-data; name="{namn}"\r\n\r\n{värde}\r\n`
2. Filen:
   `--{grans}\r\nContent-Disposition: form-data; name="file"; filename="{filnamn}"\r\nContent-Type: {filtyp}\r\n\r\n`, därefter bytes i `fil` oförändrade, därefter `\r\n`.
3. Slutet: `--{grans}--\r\n`

## Exempel
`byggMultipart("XX", listOf("model" to "small", "language" to "sv"), "a.wav", "audio/wav", byteArrayOf(1, 2, 3))` ger byte för byte samma som
`"--XX\r\nContent-Disposition: form-data; name=\"model\"\r\n\r\nsmall\r\n--XX\r\nContent-Disposition: form-data; name=\"language\"\r\n\r\nsv\r\n--XX\r\nContent-Disposition: form-data; name=\"file\"; filename=\"a.wav\"\r\nContent-Type: audio/wav\r\n\r\n"` + bytes `1, 2, 3` + `"\r\n--XX--\r\n"`.
- Tom `falt`-lista: kroppen börjar direkt med filens avsnitt.
- Tom `fil`: filavsnittet har tom nyttolast, resten som vanligt.
- Värdet `åäö` kodas som UTF-8 (6 byte).

## Test som ska gå igenom
gradle :rostfangst:testDebugUnitTest --tests "*MultipartTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
