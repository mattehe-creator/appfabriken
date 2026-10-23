# Uppgift: Exempelåtgärder el, värme, ventilation, hiss

uppdrag: uppdrag/2026-10-23-styrelserummet-exempelhandlingar.md
försök: 2
beror-på: 2026-10-23-styrelserummet-exempel-02-atgarder-tak-fasad-fonster-stammar-f2.md

## Vad som saknades
- `ExempelData.kt` lämnades oförändrad; listan ska ha **åtta** poster (grupp 1 + fyra nya enligt tabellen).
- `ExempelDataAtgarderGrupp2Test.kt` skapades som tom fil utan package, imports, klass eller testmetod `grupp2TackerElVarmeVentilationHiss`.

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelData.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelDataAtgarderGrupp2Test.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelDataAtgarderGrupp1Test.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelDataAtgarderGrupp1Test.kt (mönster för JUnit 4 och `first { it.namn == "..." }`)

## Uppgift
1. I `ExempelData.kt`: **behåll** de fyra befintliga `ExempelAtgard`-posterna oförändrade och lägg till **exakt** dessa fyra efter dem:

| namn | år | kostnad |
|---|---|---|
| Elstammar och elcentral | 2029 | 2_500_000 |
| Fjärrvärmecentral | 2027 | 2_000_000 |
| Ventilation FTX | 2034 | 1_800_000 |
| Hiss | 2031 | 1_500_000 |

   Exempel: `ExempelAtgard("Hiss", 2031, 1_500_000L)`.

2. I `ExempelDataAtgarderGrupp2Test.kt`: skriv testet `grupp2TackerElVarmeVentilationHiss` som:
   - kontrollerar att `EXEMPEL_ATGARDER` innehåller poster med namnen Elstammar och elcentral, Fjärrvärmecentral, Ventilation FTX och Hiss;
   - för varje av de fyra: `ar` och `kostnadKronor` stämmer med tabellen;
   - kontrollerar att summan av **alla** `kostnadKronor` i `EXEMPEL_ATGARDER` är `23_300_000L` (grupp 1 = 15_500_000, grupp 2 = 7_800_000).

   Använd JUnit 4 (`org.junit.Test`, `org.junit.Assert`), inte `kotlin.test`.

3. I `ExempelDataAtgarderGrupp1Test.kt`: ändra `Assert.assertEquals(4, EXEMPEL_ATGARDER.size)` till `Assert.assertEquals(8, EXEMPEL_ATGARDER.size)` så att hela modulens enhetstester fortfarande går igenom efter tillägget.

## Exempel
- Post "Ventilation FTX" → `ar == 2034`, `kostnadKronor == 1_800_000L`
- `EXEMPEL_ATGARDER.sumOf { it.kostnadKronor!! } == 23_300_000L`

## Test som ska gå igenom
gradle :styrelserummet:testDebugUnitTest --tests "*ExempelDataAtgarderGrupp2Test"
gradle :styrelserummet:testDebugUnitTest --tests "*ExempelDataAtgarderGrupp1Test"

## Gör inte
- Ändra namn, år eller kostnad för grupp 1:s fyra åtgärder.
- Lägga till balkong eller mark ännu.
- Lämna testfilen tom.

utfall: godkänd 2026-10-23 (inget)
