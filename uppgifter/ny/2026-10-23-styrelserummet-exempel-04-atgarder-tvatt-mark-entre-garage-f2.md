# Uppgift: Exempelåtgärder tvättstuga, mark, entré, garage

uppdrag: uppdrag/2026-10-23-styrelserummet-exempelhandlingar.md
försök: 2
beror-på: 2026-10-23-styrelserummet-exempel-03-atgarder-el-varme-ventilation-hiss-f2.md

## Vad som saknades
- `ExempelData.kt` lämnades oförändrad; listan ska ha **tolv** poster (grupp 1 + grupp 2 + fyra nya enligt tabellen).
- `ExempelDataAtgarderGrupp3Test.kt` skapades inte alls.

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelData.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelDataAtgarderGrupp3Test.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelDataAtgarderGrupp1Test.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelDataAtgarderGrupp2Test.kt (mönster för JUnit 4, `first { it.namn == "..." }` och totalsumma)

## Uppgift
1. I `ExempelData.kt`: **behåll** alla åtta befintliga `ExempelAtgard`-poster oförändrade och lägg till **exakt** dessa fyra efter dem:

| namn | år | kostnad |
|---|---|---|
| Tvättstuga | 2033 | 1_200_000 |
| Mark och gård | 2035 | 1_000_000 |
| Entréparti | 2037 | 800_000 |
| Garageport | 2039 | 500_000 |

   Exempel: `ExempelAtgard("Garageport", 2039, 500_000L)`.

2. I `ExempelDataAtgarderGrupp3Test.kt`: skriv testet `grupp3TackerTvattMarkEntreGarage` som:
   - kontrollerar att `EXEMPEL_ATGARDER.size == 12`;
   - för Tvättstuga, Mark och gård, Entréparti och Garageport: `namn`, `ar` och `kostnadKronor` stämmer med tabellen (hitta varje post med `first { it.namn == "..." }`);
   - kontrollerar att summan av **alla** `kostnadKronor` i `EXEMPEL_ATGARDER` är `26_800_000L` (grupp 3 tillför 3_500_000 till befintliga 23_300_000).

   Använd JUnit 4 (`org.junit.Test`, `org.junit.Assert`), inte `kotlin.test`.

3. I `ExempelDataAtgarderGrupp1Test.kt`: ändra `Assert.assertEquals(8, EXEMPEL_ATGARDER.size)` till `Assert.assertEquals(12, EXEMPEL_ATGARDER.size)`.

## Exempel
- Post "Tvättstuga" → `ar == 2033`, `kostnadKronor == 1_200_000L`
- Post "Mark och gård" → `ar == 2035`, `kostnadKronor == 1_000_000L`
- `EXEMPEL_ATGARDER.sumOf { it.kostnadKronor!! } == 26_800_000L`

## Test som ska gå igenom
gradle :styrelserummet:testDebugUnitTest --tests "*ExempelDataAtgarderGrupp3Test"
gradle :styrelserummet:testDebugUnitTest --tests "*ExempelDataAtgarderGrupp1Test"

## Gör inte
- Ändra namn, år eller kostnad för grupp 1:s eller grupp 2:s åtgärder.
- Lägga till balkong eller byggnadsvärde ännu.
- Committa enbart uppgiftsflytt utan kod.
