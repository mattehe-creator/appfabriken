# Uppgift: gissa varutyp utifrån varunamnet

uppdrag: ingen (Mattias önskemål 2026-10-12: appen ska hjälpa till att välja varutyp, eftersom typen styr frister)
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/VarutypGissning.kt (ny)
- garantivalvet/src/test/kotlin/se/tmconnect/garantivalvet/regler/VarutypGissningTest.kt (ny)

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/OppetKop.kt (enum `Varutyp`)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/KvittoVaror.kt (stil: ren Kotlin, ingen Android)

## Uppgift
Skapa en ren funktion (ingen Android, inga beroenden) i paketet `se.tmconnect.garantivalvet.regler`:

```kotlin
fun gissaVarutyp(vad: String): Varutyp?
```

Den gissar en [Varutyp] ur namnet på varan, eller `null` när den inte är säker (`OVRIGT` gissas aldrig).

Regler:
1. Gör namnet till gemener, dela upp i ord på allt som inte är en bokstav (`Char.isLetter()`), och ignorera tomma ord. Siffror och skiljetecken ger inga ord.
2. Ett ord matchar ett nyckelord om ordet är **lika med** nyckelordet, eller om nyckelordet har minst 4 bokstäver och ordet **slutar på** nyckelordet. (På svenska står sammansättningens huvudord sist: "kaffebryggare" slutar på "bryggare". Därför matchar inte "kaffebryggare" nyckelordet "kaffe".)
3. Kategorierna kontrolleras i denna ordning. Den första kategori där något ord matchar något av dess nyckelord avgör. Ingen matchning ger `null`.
   1. `ELEKTRONIK`: tv, dator, laptop, telefon, mobil, surfplatta, hörlurar, kamera, skärm, laddare, högtalare, bryggare, vattenkokare, brödrost, mikrovågsugn, dammsugare, diskmaskin, tvättmaskin, kyl, frys, kylskåp, hårtork, rakapparat, dishwasher, kettle, headphones, speaker, charger, television, phone
   2. `KLADER_SKOR`: jacka, jeans, byxor, byxa, skor, sko, kängor, stövlar, tröja, klänning, kjol, strumpor, mössa, handskar, skjorta, kavaj, kappa, jacket, shoes, boots, trousers, sweater, dress, coat
   3. `HEM_BYGG`: borrmaskin, skruvdragare, hammare, färg, spik, skruv, kran, toalettstol, golv, laminat, tapet, lampa, drill, hammer, paint, screws
   4. `VAXTER`: planta, växt, tuja, thuja, buske, träd, ros, palm, kaktus, orkidé, plant, shrub, tree
   5. `MAT_DRYCK`: mjölk, bröd, kaffe, te, kött, fisk, ost, frukt, grönsaker, ägg, juice, läsk, vin, öl, choklad, kaka, yoghurt, snittblommor, bukett, milk, bread, coffee, cheese, fruit
4. Funktionen har en tydlig KDoc som förklarar reglerna (kort, på svenska).

## Exempel (alla ska bli testfall i `VarutypGissningTest`)
- "Kaffebryggare" → ELEKTRONIK
- "Kaffe 500 g" → MAT_DRYCK
- "Mobiltelefon" → ELEKTRONIK
- "TV 55\"" → ELEKTRONIK
- "tv" → ELEKTRONIK
- "Diskmaskin" → ELEKTRONIK
- "Löparskor" → KLADER_SKOR
- "Vinterjacka" → KLADER_SKOR
- "Borrmaskin" → HEM_BYGG
- "Väggfärg" → HEM_BYGG
- "Thuja" → VAXTER
- "Rosbuske" → VAXTER
- "Ros" → VAXTER
- "Rost" → null (för kort nyckelord "ros" matchar bara ett helt ord)
- "Havremjölk" → MAT_DRYCK
- "Snittblommor" → MAT_DRYCK
- "Dishwasher" → ELEKTRONIK
- "Present" → null
- "" → null
- "   " → null
- "Tvättmedel" → null

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*VarutypGissningTest"
gradle testDebugUnitTest

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
- Lägga till fler nyckelord än de som står ovan.
