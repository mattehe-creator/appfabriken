beslut: underkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast `uppgifter/pagar/2026-10-21-styrelserummet-kvalitet-13-jamforelse-forandring.md` (flytt från `ny/`) ändrades — varken `Jamforelse.kt` eller `JamforelseTest.kt` finns på grenen.
- Uppgiftens beteende (`forandring`, normalisering, NY/BORTTAGEN/ANDRAD, sortering) är inte implementerat.
- Testet `JamforelseTest.forandring` saknas; exemplen i uppgiften testas inte.
- Inga otillåtna filer utanför listan (implementation saknas helt).
- Inga nya beroenden, behörigheter eller nätverk (ingen produktkod tillagd).
- Ingen UI-kod; inga hårdkodade strängar i appen tillagda.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

# Uppgift: forandring mot föregående år

## Vad som saknades
- Grenen innehöll bara commit «Uppgift startad» (flytt till `pagar/`). Filerna `styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Jamforelse.kt` och `styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/JamforelseTest.kt` skapades inte.
- Funktionen `forandring` och enhetstest som verifierar uppgiftens exempel saknas.

uppdrag: uppdrag/2026-10-21-styrelserummet-kvalitet-utkast.md
försök: 2
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Jamforelse.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/JamforelseTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Komponent.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/SummeringTest.kt (JUnit 4 och hjälpfunktion för `Komponent`)

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
- Oförändrad (samma normaliserade namn och samma värden för de tre fälten ovan) → ingen rad i resultatet.
- Övriga fält (`grupp`, `restvarde`, `kalla`, `sakerhet`) ska **inte** påverka om en komponent räknas som oförändrad.

Sortera resultatet alfabetiskt på `namn` (svensk ordning räcker med `String.compareTo`).

## Exempel
- `nu` har komponent `"Tak"`, `foreg` saknar motsvarande normaliserat namn → en rad `Forandring("Tak", NY)`.
- `foreg` har `"Hiss"`, `nu` saknar → `Forandring("Hiss", BORTTAGEN)`.
- Samma normaliserade namn i båda listor men olika `anskaffningsvarde` i `nu` → `Forandring(namn från nu-posten, ANDRAD)`.
- Identiska namn (efter normalisering) och samma `anskaffningsvarde`, `nyttjandeperiodAr`, `anskaffningsar` → ingen rad för den komponenten.

## Test som ska gå igenom
gradle :styrelserummet:testDebugUnitTest --tests "*JamforelseTest.forandring"

Skriv testmetoden `forandring` med `import org.junit.Test` och `import org.junit.Assert.*` (inte `kotlin.test` eller JUnit Jupiter). Asserta konkret lista (typ, namn, sortering) för minst exemplen NY, BORTTAGEN, ANDRAD och tom lista för oförändrad komponent.

## Gör inte
- Ändra andra filer (inga `uppgifter/`, `BESLUTSLOGG.md`, `STATUS.md`).
- Lägga till beroenden.
- Ändra datamodellen.

## Frågor till Claude
- inga
