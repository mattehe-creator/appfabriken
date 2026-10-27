# Uppgift: HA-inställningar i InstallningarLager (Röstfångst)

uppdrag: uppdrag/claude/2026-10-27-rostfangst-ha-reserv.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/data/InstallningarLager.kt
- rostfangst/src/test/kotlin/se/tmconnect/rostfangst/data/InstallningarLagerHaTest.kt (ny)

## Läs som förebild (ändra inte)
- rostfangst/src/test/kotlin/se/tmconnect/rostfangst/NatverkBehorighetTest.kt (Robolectric-uppsättning: `@RunWith(RobolectricTestRunner::class)`, `@Config(sdk = [28])`, `ApplicationProvider`)

## Uppgift
Lägg till i `InstallningarLager`, på samma sätt som `apiNyckel`:

```kotlin
val haAdress: Flow<String>      // nyckel "ha_adress", tom om saknas
val haToken: Flow<String>       // nyckel "ha_token", tom om saknas
val haTodo: Flow<String>        // nyckel "ha_todo", tom om saknas

suspend fun sparaHa(adress: String, token: String, todo: String)  // sparar alla tre trimmade
```

Befintliga funktioner och nycklar ändras inte.

Testet (Robolectric, `runBlocking` från `kotlinx.coroutines`, `first()` från `kotlinx.coroutines.flow`):
- Innan något sparats är alla tre tomma.
- `sparaHa(" http://ha.local:8123 ", " abc ", " todo.x ")` och sedan `first()` ger `"http://ha.local:8123"`, `"abc"`, `"todo.x"`.
- `sparaHa` ändrar inte `apiNyckel`: spara först `sparaApiNyckel("k")`, sedan `sparaHa(...)`, och `apiNyckel.first()` är fortfarande `"k"`.

DataStore delas mellan testfallen i samma process. Skriv därför ett testfall som gör stegen i ordning, eller börja varje fall med `sparaHa("", "", "")`.

## Test som ska gå igenom
gradle :rostfangst:testDebugUnitTest --tests "*InstallningarLagerHaTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Skriva token i loggar.
