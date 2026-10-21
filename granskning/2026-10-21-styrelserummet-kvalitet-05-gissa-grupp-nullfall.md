beslut: underkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Diff mot main: enbart rename av uppgiftsfil från `uppgifter/ny/` till `uppgifter/pagar/`; varken `GrupperaNamn.kt` eller `GrupperaNamnTest.kt` ändrades; inga filer i repots rot.
- Uppgiften kräver testmetoden `gissaGruppNullfall` med exemplen; den saknas helt i `GrupperaNamnTest.kt`.
- `gradle :styrelserummet:test --tests "*GrupperaNamnTest.gissaGruppNullfall"` → «No tests found for given includes».
- Befintliga tester använder JUnit 4 (`org.junit`); inga nya beroenden, behörigheter eller nätverk i diffen.
- `:styrelserummet:testDebugUnitTest` grönt lokalt (befintliga tester); uppgiftens angivna test kan inte köras.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

```
# Uppgift: gissaGrupp utan träff

uppdrag: uppdrag/2026-10-21-styrelserummet-kvalitet-utkast.md
försök: 2
beror-på: 2026-10-21-styrelserummet-kvalitet-04-gissa-grupp-hiss-tvatt-mark-f2.md

## Vad som saknades
- Grenen innehöll bara commit «Uppgift startad» (flytt till `pagar/`). Ingen ny testmetod `gissaGruppNullfall` lades till i `GrupperaNamnTest.kt`.
- `gradle :styrelserummet:test --tests "*GrupperaNamnTest.gissaGruppNullfall"` rapporterar «No tests found» och kan inte verifiera uppgiften.

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/GrupperaNamn.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/GrupperaNamnTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/GrupperaNamnTest.kt (befintliga fall)

## Uppgift
Säkerställ att `gissaGrupp` returnerar `null` när inget nyckelord matchar, inklusive namn som liknar nyckelord men inte ska klassas (inga nya grupper).

Lägg till JUnit 4-test (`org.junit.Test`, `org.junit.Assert.assertEquals` — **inte** `kotlin.test`):

@Test
fun gissaGruppNullfall() { ... }

med `assertEquals(null, gissaGrupp(...))` för alla fyra exemplen nedan. Du får samla dem i en metod även om två redan finns i andra testmetoder.

Ändra `GrupperaNamn.kt` **bara** om något assert i `gissaGruppNullfall` (eller befintliga tester) failar.

## Exempel (ska finnas som assert i `gissaGruppNullfall`)
- `""` → `null`
- `"Stomme i betong"` → `null`
- `"Elisabeths trädgård"` → `null`
- `"Balkong"` (utan dörr/fönster-nyckel) → `null`

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*GrupperaNamnTest.gissaGruppNullfall"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra datamodellen.
```

## Frågor till Claude
- inga
