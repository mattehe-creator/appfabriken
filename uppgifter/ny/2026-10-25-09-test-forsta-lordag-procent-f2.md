# Uppgift: Test av första lördagen och procenttext i Styrelserummet

uppdrag: uppdrag/claude/2026-10-25-natt-testtackning.md
försök: 2
beror-på: ingen

## Vad som saknades
- `ForstaLordagenProcentTest.kt` var helt tom efter försök 1. Filen måste innehålla en testklass med `@Test`-metoder som anropar `forstaLordagenFran` och `procent` och jämför resultat med exemplen nedan (`org.junit.Test`, `org.junit.Assert.assertEquals`, `java.time.LocalDate`).
- CI kräver att kommandot under «Test som ska gå igenom» hittar minst en test i klassen `ForstaLordagenProcentTest`.

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/ForstaLordagenProcentTest.kt (ny eller fyll i befintlig tom fil)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Helgdagar.kt (funktionen `forstaLordagenFran`)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Formatering.kt (funktionen `procent`)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/FormateringTest.kt

## Uppgift
Skriv JUnit 4-tester (`org.junit.Test`, `assertEquals`) för:

- `forstaLordagenFran(datum: LocalDate): LocalDate` — samma dag om den är en lördag, annars nästa lördag.
- `procent(del: Long, helhet: Long): String` — andelen i procent med en decimal, decimalkomma, mellanslag och `%`. Om `helhet` är 0 blir svaret tankstrecket `"–"`.

Importera `java.time.LocalDate`.

## Exempel
Veckodagar: 2026-10-24 är en lördag, 2026-10-25 en söndag, 2026-10-19 en måndag, 2026-12-31 en torsdag och 2027-06-20 en söndag.

- `forstaLordagenFran(LocalDate.of(2026, 10, 24))` → `LocalDate.of(2026, 10, 24)`
- `forstaLordagenFran(LocalDate.of(2026, 10, 25))` → `LocalDate.of(2026, 10, 31)`
- `forstaLordagenFran(LocalDate.of(2026, 10, 19))` → `LocalDate.of(2026, 10, 24)`
- `forstaLordagenFran(LocalDate.of(2026, 6, 20))` → `LocalDate.of(2026, 6, 20)` (midsommardagen 2026)
- `forstaLordagenFran(LocalDate.of(2026, 12, 31))` → `LocalDate.of(2027, 1, 2)` (över årsskiftet)
- `forstaLordagenFran(LocalDate.of(2027, 6, 20))` → `LocalDate.of(2027, 6, 26)` (midsommardagen 2027)
- `procent(1, 3)` → `"33,3 %"`
- `procent(2, 3)` → `"66,7 %"`
- `procent(3, 2)` → `"150,0 %"`
- `procent(0, 7)` → `"0,0 %"`
- `procent(1, 1000)` → `"0,1 %"`
- `procent(5, 0)` → `"–"`

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*ForstaLordagenProcentTest"

## Gör inte
- Ändra andra filer.
- Ändra `Helgdagar.kt` eller `Formatering.kt`. Om ett exempel inte stämmer med koden: skriv det i PR-beskrivningen och låt testet följa exemplet.
- Lägga till beroenden.
