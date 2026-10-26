# Uppgift: Adressbedömning

uppdrag: uppdrag/vantar/2026-10-25-rostfangst-lokal-ai.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/lokal/Adressbedomning.kt (ny)
- rostfangst/src/test/kotlin/se/tmconnect/rostfangst/lokal/AdressbedomningTest.kt (ny)

## Läs som förebild (ändra inte)
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/wear/UtforMeddelande.kt
- rostfangst/src/test/kotlin/se/tmconnect/rostfangst/wear/UtforMeddelandeTest.kt

## Uppgift
Paket `se.tmconnect.rostfangst.lokal`. Ren Kotlin, ingen `android.*` och ingen `org.json`. Testet är ett vanligt JUnit 4-test, utan Robolectric.

```kotlin
sealed interface AdressBedomning {
    data class Ogiltig(val skal: String) : AdressBedomning
    object Krypterad : AdressBedomning
    object OkrypteradLokal : AdressBedomning
    data class Avvisad(val skal: String) : AdressBedomning
}
fun bedomAdress(text: String): AdressBedomning
```

Regler, i den här ordningen:
1. Trimma `text`. Tom ger `Ogiltig("tom")`.
2. Schemat är `https://` eller `http://` (stora och små bokstäver spelar ingen roll). Annars `Ogiltig("saknar_schema")`.
3. Värden är texten efter schemat fram till första `/`, `:` eller slutet. Tom värd ger `Ogiltig("saknar_vard")`.
4. `https://` ger `Krypterad`.
5. `http://` ger `OkrypteradLokal` om värden är `localhost`, slutar på `.local`, eller är en IPv4-adress (exakt fyra delar, varje del ett heltal 0–255) som börjar på `10.`, `192.168.` eller `172.` följt av ett andra tal 16–31. Annars `Avvisad("okrypterad_utanfor_lokalt_nat")`. Siffereglerna gäller bara riktiga IPv4-adresser: `10.example.com` är `Avvisad`.

## Exempel
- `https://dator.tail1234.ts.net` → `Krypterad`
- `HTTPS://dator.ts.net:11434/api` → `Krypterad`
- `http://192.168.1.10:11434` → `OkrypteradLokal`
- `http://10.0.0.5` → `OkrypteradLokal`
- `http://172.16.0.1` och `http://172.31.255.1` → `OkrypteradLokal`
- `http://172.32.0.1` → `Avvisad("okrypterad_utanfor_lokalt_nat")`
- `http://localhost:11434` och `http://minpc.local` → `OkrypteradLokal`
- `http://example.com` → `Avvisad("okrypterad_utanfor_lokalt_nat")`
- `http://10.example.com` → `Avvisad("okrypterad_utanfor_lokalt_nat")`
- `192.168.1.10` → `Ogiltig("saknar_schema")`
- `   ` → `Ogiltig("tom")`
- `https://` → `Ogiltig("saknar_vard")`

## Test som ska gå igenom
gradle :rostfangst:testDebugUnitTest --tests "*AdressbedomningTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
