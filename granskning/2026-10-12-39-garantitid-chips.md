beslut: underkänd
granskare: cursor
datum: 2026-10-12

## Kontrollerat
- Tre av de angivna filerna finns på rätt sökväg (`GarantitidVal.kt`, `GarantitidValTest.kt`, `KopFormularSkarm.kt`); ingen Kotlin-fil i repots rot.
- `GarantitidVal.manaderFranVal` och `GarantitidValTest` följer exemplen för index 0–2 och «Annat» med «18»; JUnit 4 (`org.junit`). Inga nya beroenden, behörigheter eller nätverk i diffen.
- `KopFormularSkarm.kt` bryter mot uppgiften: `KvittoKort` är borttagen och anropet kommenterat bort — kvitto-UI ska vara kvar oförändrad utöver garantifältet.
- Chip-etiketter och «Annat»-fältet är hårdkodade på svenska; nycklarna `garanti_chip_*` saknas i `strings.xml` (finns inte efter del A) och lades inte till.
- Vid redigering av köp sätts chip alltid till index 1 (24) i stället för att tolka `initialGarantiManader` (12/24/36 eller «Annat»).
- Oanvänd state `garantiManaderText`; `isError` på garantifält saknas inte längre på chip-raden (mindre).
- `manaderFranVal` för index 3 accepterar 0 och negativa tal via `toIntOrNull()`; uppgiften kräver positivt heltal.
- Ändring utanför uppgiftslistan: borttagen `KvittoKort` och import av `AppfabrikKortForm`.
- CI på grenen: success. Lokalt kunde `testDebugUnitTest` inte köras (saknar Android SDK i denna miljö).

## Rättat av Cursor
- inget (stora fel — se försök 2)

## Vid underkänd: försök 2

# Uppgift: garantitid som chips

## Vad som saknades (försök 1)
- **`KvittoKort` får inte tas bort eller kommenteras bort.** Återställ `private fun KvittoKort(...)` och anropet överst i formuläret exakt som på main (före garantiändringen). Rör inte kvitto-logiken.
- **Inga hårdkodade UI-strängar** för chips eller «Annat»-fält. Lägg till nycklarna `garanti_chip_12`, `garanti_chip_24`, `garanti_chip_36`, `garanti_chip_annat` i `values/strings.xml` och `values-sv/strings.xml` (engelska/svenska). För «Annat»-fältets etikett: använd `R.string.falt_garanti_manader`.
- **Chip-val vid redigering:** När `initialGarantiManader` (sträng) tolkas till 12, 24 eller 36, markera motsvarande chip. Annars välj index 3 («Annat») och fyll `annatText` med strängen. Nytt köp: index **1** (24 mån) som standard.
- **`GarantitidVal`:** Behåll `FORVALD_MANADER`, `CHIP_MANADER` och `manaderFranVal`. För index 3: returnera `null` om texten inte är ett **positivt** heltal (`> 0`). Onödig sealed-hierarki utanför companion kan förenklas till t.ex. `object GarantitidVal` om du vill, men beteendet ska stämma.
- Ta bort oanvänd state (t.ex. `garantiManaderText`). Visa `FormularFel.GARANTI_OGILTIG` under garantisektionen som på main.
- Bygg och kör: `gradle :garantivalvet:testDebugUnitTest --tests "*GarantitidValTest"`.

uppdrag: uppdrag/2026-10-12-ny-design.md
försök: 2
beror-på: ingen

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/GarantitidVal.kt
- garantivalvet/src/test/kotlin/se/tmconnect/garantivalvet/regler/GarantitidValTest.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopFormularSkarm.kt
- garantivalvet/src/main/res/values/strings.xml
- garantivalvet/src/main/res/values-sv/strings.xml

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/FormularValidering.kt

## Uppgift
1. **`GarantitidVal.kt`**: `FORVALD_MANADER = 24`, `CHIP_MANADER = listOf(12, 24, 36)`, och `fun manaderFranVal(chipIndex: Int, annatText: String): Int?` där index 0–2 är fasta månader och index 3 tolkar `annatText` som positivt heltal.

2. **`KopFormularSkarm.kt`**: Ersätt garantitid-textfältet med `FilterChip` (M3) för 12, 24, 36 mån och «Annat». Standardval **24** vid nytt köp. När «Annat» är valt, visa kort numeriskt fält under. Vid spara, skicka valt antal månader till `onSpara` som idag. **Låt `KvittoKort` vara kvar.**

3. Strängar `garanti_chip_12`, `garanti_chip_24`, `garanti_chip_36`, `garanti_chip_annat` (sv/en) i `strings.xml`.

## Exempel
- Nytt formulär → chip 24 markerat.
- Användaren väljer «Annat» och skriver «18» → sparar med `garantiManader = 18`.
- `manaderFranVal(1, "")` → 24.
- `manaderFranVal(3, "0")` → null.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*GarantitidValTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
- Ta bort eller kommentera bort kvitto-kortet.

## Frågor till Claude
- inga
