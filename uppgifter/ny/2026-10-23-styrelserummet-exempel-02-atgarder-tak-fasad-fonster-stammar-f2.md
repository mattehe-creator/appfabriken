# Uppgift: Exempelåtgärder tak, fasad, fönster, stammar

uppdrag: uppdrag/2026-10-23-styrelserummet-exempelhandlingar.md
försök: 2
beror-på: 2026-10-23-styrelserummet-exempel-01-forening.md

## Vad som saknades
- `ExempelData.kt` lämnades oförändrad; listan ska innehålla exakt fyra `ExempelAtgard`-poster enligt tabellen nedan.
- `ExempelDataAtgarderGrupp1Test.kt` skapades som tom fil utan package, imports, klass eller testmetod `grupp1TackerTakFasadFonsterStammar`.

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelData.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelDataAtgarderGrupp1Test.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Komponentgrupp.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelDataForeningTest.kt (JUnit 4: `org.junit.Test`, `org.junit.Assert`)

## Uppgift
1. I `ExempelData.kt`: sätt `EXEMPEL_ATGARDER` till en lista med **exakt** dessa fyra poster (kostnad i hela kronor, icke-null `Long`):

| namn | år | kostnad |
|---|---|---|
| Yttertak | 2032 | 5_000_000 |
| Fasadputs | 2036 | 4_000_000 |
| Fönster | 2028 | 3_500_000 |
| Stammar vatten och avlopp | 2030 | 3_000_000 |

   Exempel på en rad: `ExempelAtgard("Yttertak", 2032, 5_000_000L)`.

2. I `ExempelDataAtgarderGrupp1Test.kt`: skriv testet `grupp1TackerTakFasadFonsterStammar` som:
   - kontrollerar att `EXEMPEL_ATGARDER` innehåller poster med namnen Yttertak, Fasadputs, Fönster och Stammar vatten och avlopp (minst dessa fyra; listan får inte ha fler åtgärder i denna uppgift);
   - för varje av de fyra: `kostnadKronor` är inte null;
   - för varje av de fyra: `ar` stämmer med tabellen (2032, 2036, 2028, 2030);
   - för Yttertak: `kostnadKronor == 5_000_000L` (samma mönster för övriga kostnader enligt tabellen).

   Använd JUnit 4 (`org.junit.Test`, `org.junit.Assert`), inte `kotlin.test`.

## Exempel
- Rad "Yttertak" → `kostnadKronor == 5_000_000L`
- Hitta post: `EXEMPEL_ATGARDER.first { it.namn == "Fönster" }.ar == 2028`

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*ExempelDataAtgarderGrupp1Test"

## Gör inte
- Lägga till andra åtgärder än de fyra.
- Ändra föreningskonstanterna eller `exempelForening()`.
- Lämna testfilen tom.
