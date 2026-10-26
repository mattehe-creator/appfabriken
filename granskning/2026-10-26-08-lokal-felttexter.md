beslut: underkänd
granskare: spärren
datum: 2026-10-26

## Kontrollerat
- Filen `OllamaFraga.kt` står inte under "Ändra bara dessa filer" (fel sökväg eller fel fil).
- Filen `OllamaFragaTest.kt` står inte under "Ändra bara dessa filer" (fel sökväg eller fel fil).

## Rättat av Cursor
- inget

## Vid underkänd: försök 2
# Uppgift: Feltexter för datorn

## Vad som saknades
- Filen `OllamaFraga.kt` står inte under "Ändra bara dessa filer" (fel sökväg eller fel fil).
- Filen `OllamaFragaTest.kt` står inte under "Ändra bara dessa filer" (fel sökväg eller fel fil).

uppdrag: uppdrag/vantar/2026-10-25-rostfangst-lokal-ai.md
försök: 2
beror-på: 2026-10-26-07-lokal-val-av-tolkare.md

## Ändra bara dessa filer
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/lokal/LokalFelText.kt (ny)
- rostfangst/src/test/kotlin/se/tmconnect/rostfangst/lokal/LokalFelTextTest.kt (ny)

## Läs som förebild (ändra inte)
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/lokal/ValAvTolkare.kt (här finns `LokalFel`)

## Uppgift
Paket `se.tmconnect.rostfangst.lokal`. Ren Kotlin, ingen `android.*`. JUnit 4, utan Robolectric.

```kotlin
enum class Kalla { TOLKNING, TALIGENKANNING }
fun lokalFelText(fel: LokalFel, kalla: Kalla): String
```

Exakta texter:
- `TOLKNING`, `NATFEL`: `Datorn svarade inte. Kontrollera att den är på och att adressen stämmer.`
- `TOLKNING`, `TIMEOUT`: `Datorn tog för lång tid på sig att svara.`
- `TOLKNING`, `OGILTIGT_SVAR`: `Datorn gav ett svar som inte gick att tolka.`
- `TALIGENKANNING`, `NATFEL`: `Taligenkänningen på datorn svarade inte. Kontrollera att den är på och att adressen stämmer.`
- `TALIGENKANNING`, `TIMEOUT`: `Taligenkänningen på datorn tog för lång tid på sig.`
- `TALIGENKANNING`, `OGILTIGT_SVAR`: `Taligenkänningen på datorn gav inget läsbart svar.`

Texterna innehåller aldrig någon adress eller token (funktionen tar inga sådana argument). Texterna flyttas till `strings.xml` när funktionen kopplas in; det görs inte här.

## Exempel
- `lokalFelText(NATFEL, TOLKNING)` → raden ovan ordagrant.
- Testa alla 6 kombinationer mot de exakta texterna, och att ingen text är tom.

## Test som ska gå igenom
gradle :rostfangst:testDebugUnitTest --tests "*LokalFelTextTest"

## Gör inte
- Ändra andra filer (inte heller `ValAvTolkare.kt`).
- Lägga till beroenden.

## Frågor till Claude
- inga
