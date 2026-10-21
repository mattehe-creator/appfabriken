# Uppgift: forandring mot föregående år

uppdrag: uppdrag/2026-10-21-styrelserummet-kvalitet-utkast.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Jamforelse.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/JamforelseTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Komponent.kt

## Uppgift
```kotlin
enum class ForandringTyp { NY, BORTTAGEN, ANDRAD }

data class Forandring(val namn: String, val typ: ForandringTyp)

fun forandring(nu: List<Komponent>, foreg: List<Komponent>): List<Forandring>
```

Matcha komponenter på normaliserat namn: gemener och omgivande blanksteg borttagna (`"  Tak  "` = `"tak"`).

- Finns i `nu` men inte i `foreg` → `NY` (visningsnamn från `nu`-posten).
- Finns i `foreg` men inte i `nu` → `BORTTAGEN` (visningsnamn från `foreg`).
- Finns i båda men skiljer sig i `anskaffningsvarde`, `nyttjandeperiodAr` eller `anskaffningsar` → `ANDRAD` (namn från `nu`).
- Oförändrad → ingen rad i resultatet.

Sortera resultatet alfabetiskt på `namn` (svensk ordning räcker med `String.compareTo`).

## Exempel
- `nu` har `"Tak"`, `foreg` saknar → `NY`
- `foreg` har `"Hiss"`, `nu` saknar → `BORTTAGEN`
- Samma namn, nytt anskaffningsvärde → `ANDRAD`
- Identiska → tom lista för den komponenten

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*JamforelseTest.forandring"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra datamodellen.

utfall: underkänd 2026-10-21 av Cursor (PR #332). Ersatt av 2026-10-21-styrelserummet-kvalitet-13-jamforelse-forandring-f2.md
