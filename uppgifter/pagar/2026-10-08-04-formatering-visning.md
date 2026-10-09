# Uppgift: formatera pris och datum för visning

uppdrag: uppdrag/2026-10-08-kop-databas-lista-formular.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/Formatering.kt (ny)
- garantivalvet/src/test/kotlin/se/tmconnect/garantivalvet/regler/FormateringTest.kt (ny)

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/Garanti.kt

## Uppgift
Ren Kotlin i `se.tmconnect.garantivalvet.regler`. Använd `java.time.format.DateTimeFormatter` och `java.util.Locale`.

1. `fun formateraPrisKr(prisOre: Long?, locale: Locale = Locale.forLanguageTag("sv-SE")): String`
   - `null` → tom sträng `""`.
   - Annars `"1 299,50 kr"` för `129950` med svensk locale (mellanslag som tusentalsavgränsare, komma som decimal, suffix ` kr`).
   - Med `Locale.US` och `129950` → `"1,299.50 kr"` (suffix behålls som ` kr`).

2. `fun formateraDatum(datum: LocalDate, locale: Locale = Locale.forLanguageTag("sv-SE")): String`
   - Kort datum enligt locale (t.ex. `2026-03-15` → `"2026-03-15"` för sv-SE i test med ISO-lik formatter, eller locale SHORT — välj `DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)` och testa med fast locale).

## Exempel
- `formateraPrisKr(129950L)` → `"1 299,50 kr"`
- `formateraPrisKr(null)` → `""`
- `formateraPrisKr(100L)` → `"1,00 kr"`
- `formateraDatum(LocalDate.of(2026, 3, 15), Locale.forLanguageTag("sv-SE"))` → `"15 mars 2026"` (MEDIUM svenska).

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*FormateringTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Hårdkoda strängar i Compose (formateraren returnerar färdig text).
