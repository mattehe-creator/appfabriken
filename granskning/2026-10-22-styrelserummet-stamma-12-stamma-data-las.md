beslut: underkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- Endast filer enligt uppgiften (plus uppgiftsfil i `pagar/`) — inga filer i repots rot.
- `StammaData.kt` finns på rätt sökväg men är **tom**: `lasStammaData` och `StammaRad` saknas helt, så koden uppfyller inte uppgiften och bygget/test kompilerar inte.
- `StammaDataLasTest.kt` täcker exemplen (giltig rad, tom rad, rubrik, tom kalla, negativt värde, ogiltigt år, ogiltigt tal) och jämför mot förväntade `StammaRad`/nycklar.
- Testerna använder `org.junit.jupiter` och `kotlin.test.assertEquals` — ska vara JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals`; undantag t.ex. `@Test(expected = …)` eller `assertThrows` från `org.junit` om modulen har det, annars try/catch som i andra regler-tester).
- Inga nya beroenden, behörigheter eller nätverk.
- Ingen UI-kod; inga hårdkodade användarsträngar utöver korta svenska felmeddelanden i `IllegalArgumentException` i produktionskoden (enligt uppgift).
- Ingen databasändring.

## Rättat av Cursor
- inget (saknad implementation är större än småfel)

## Vid underkänd: försök 2

```
# Uppgift: lasStammaData

uppdrag: uppdrag/2026-10-22-styrelserummet-stammotidplan.md
försök: 2
beror-på: 2026-10-22-styrelserummet-stamma-11-stamma-csv.md

## Vad som saknades
- `StammaData.kt` lämnades tom — `StammaRad` och `lasStammaData` måste implementeras enligt uppgiften nedan.
- `StammaDataLasTest.kt` använder JUnit Jupiter och `kotlin.test`; byt till JUnit 4 som i t.ex. `AvgiftCsvTest.kt` (`org.junit.Test`, `org.junit.Assert.assertEquals`).

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/StammaData.kt (ny/färdig)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/StammaDataLasTest.kt (rätta imports och ev. undantagstest)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/resources/stamma.csv

## Uppgift
Skapa `StammaData.kt`:

```kotlin
data class StammaRad(val typ: String, val ar: Int, val varde: Long, val enhet: String, val kalla: String)

fun lasStammaData(rader: List<String>): Map<String, StammaRad>
```

- Första raden är rubrik (ignoreras om den börjar med `typ`).
- Nyckel i map: `"${typ}_${ar}"` (t.ex. `prisbasbelopp_2026`).
- Tom `kalla`, ogiltigt år (< 0 eller > 9999), negativt `varde`, eller ogiltigt tal → `IllegalArgumentException` med kort svenskt meddelande.
- Tom rad ignoreras.
- Varje datarad ska ha fem fält separerade med `;` (typ, ar, varde, enhet, kalla). Ogiltig rad (fel antal fält eller ogiltiga tal) → `IllegalArgumentException`.

## Exempel
- Rad `prisbasbelopp;2026;59200;kr;SCB` → nyckel `prisbasbelopp_2026`, varde 59200
- Rad med tom kalla → IllegalArgumentException
- Rad `prisbasbelopp;2026;-1;kr;x` → IllegalArgumentException

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*StammaDataLasTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
```

## Frågor till Claude
- inga
