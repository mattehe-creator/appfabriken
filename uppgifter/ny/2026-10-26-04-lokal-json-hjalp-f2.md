# Uppgift: JSON-hjälp utan org.json

## Vad som saknades
- Filen `JsonHjalp.kt` står inte under "Ändra bara dessa filer" (fel sökväg eller fel fil).
- Filen `rostfangst/src/main/kotlin/se/tmconnect/rostfangst/lokal/Multipart.kt` står inte under "Ändra bara dessa filer" (fel sökväg eller fel fil).
- Filen `rostfangst/src/test/kotlin/se/tmconnect/rostfangst/lokal/MultipartTest.kt` står inte under "Ändra bara dessa filer" (fel sökväg eller fel fil).
- Filen `rostfangst/src/main/kotlin/se/tmconnect/rostfangst/lokal/JsonHjalp.kt` finns inte.
- Bygget eller testerna är röda. Kör testet i uppgiften och rätta tills det är grönt. Testerna i appmodulerna använder JUnit 4: import org.junit.Test och import org.junit.Assert.assertEquals (eller assertTrue, assertFalse), inte kotlin.test.

uppdrag: uppdrag/vantar/2026-10-25-rostfangst-lokal-ai.md
försök: 2
beror-på: ingen

`org.json` finns inte i enhetstester (Android-stubbar). Därför är den här hjälpen handskriven, utan `org.json`, `android.*` och andra bibliotek.

## Ändra bara dessa filer
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/lokal/JsonHjalp.kt (ny)
- rostfangst/src/test/kotlin/se/tmconnect/rostfangst/lokal/JsonHjalpTest.kt (ny)

## Läs som förebild (ändra inte)
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/wear/ForslagMeddelande.kt

## Uppgift
Paket `se.tmconnect.rostfangst.lokal`. JUnit 4, utan Robolectric.

1. `fun jsonCitera(text: String): String` — ger ett JSON-strängvärde med omslutande citattecken. Tecken som skrivs om: `"` → `\"`, `\` → `\\`, radbrytning → `\n`, vagnretur → `\r`, tab → `\t`, övriga styrtecken under U+0020 → `\u00XX` (fyra hex-siffror, små bokstäver). Allt annat, till exempel `åäö`, lämnas oförändrat.
2. `fun lasJsonStrang(json: String, nyckel: String): String?` — hittar första förekomsten av `"{nyckel}"` följt av valfria blanksteg, `:`, valfria blanksteg och ett strängvärde, och returnerar värdet avkodat. Avkodar `\"`, `\\`, `\/`, `\n`, `\r`, `\t`, `\b`, `\f` och `\uXXXX`. Returnerar `null` om nyckeln saknas, värdet inte är en sträng eller strängen är oavslutad. Förekomster av nyckeln som ligger inuti ett strängvärde behöver inte hanteras.

## Exempel
- `jsonCitera("hej")` → `"hej"` (med citattecken runt)
- `jsonCitera("a\"b")` → `"a\"b"`
- `jsonCitera("rad1\nrad2")` → `"rad1\nrad2"` (backslash och n, inte en riktig radbrytning)
- `jsonCitera("åäö")` → `"åäö"`
- `lasJsonStrang("{\"text\": \"Hej världen\"}", "text")` → `Hej världen`
- `lasJsonStrang("{\"a\":1,\"text\":\"x\\ny\"}", "text")` → `x` + radbrytning + `y`
- `lasJsonStrang("{\"text\":\"\\u00e5\"}", "text")` → `å`
- `lasJsonStrang("{\"text\":5}", "text")` → `null`
- `lasJsonStrang("{}", "text")` → `null`
- `lasJsonStrang("{\"text\":\"oavslutad", "text")` → `null`

## Test som ska gå igenom
gradle :rostfangst:testDebugUnitTest --tests "*JsonHjalpTest"

## Gör inte
- Använda `org.json` eller andra bibliotek.
- Ändra andra filer.
- Lägga till beroenden.
