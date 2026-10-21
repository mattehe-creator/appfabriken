# Uppgift: Rörliga helgdagar runt påsk

uppdrag: uppdrag/claude/2026-10-21-flextid-helgdagar.md
försök: 2
beror-på: 2026-10-21-flextid-helgdagar-01-paskdagen.md

## Vad som saknades
- Grenen innehöll bara commit «Uppgift startad» (flytt till `pagar/`). Ingen produktionskod eller test tillagd.
- `flextid/src/main/kotlin/se/tmconnect/flextid/regler/Helgdagar.kt` och `HelgdagarTest.kt` **finns redan** (från uppgift 01 med `paskdagen`). Utöka dessa filer — skapa inte nya filer i repots rot eller under fel modul.
- Funktionen `rorligaHelgdagar(ar: Int): List<Helgdag>` saknas.
- Inga JUnit-tester som assertar de fem datumen för 2026 och 2025, samt 2008-05-01 och 2008-05-11, och att listan har exakt 5 element.

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/Helgdagar.kt
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/HelgdagarTest.kt

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/Arbetsdagar.kt

## Uppgift
Lägg till i `Helgdagar.kt` (behåll `paskdagen` oförändrad):

fun rorligaHelgdagar(ar: Int): List<Helgdag>

Den returnerar fem helgdagar, i denna ordning, räknat från `val p = paskdagen(ar)`:

| Namn | Datum |
|---|---|
| "Långfredagen" | p minus 2 dagar |
| "Påskdagen" | p |
| "Annandag påsk" | p plus 1 dag |
| "Kristi himmelsfärdsdag" | p plus 39 dagar |
| "Pingstdagen" | p plus 49 dagar |

Alla har `arAfton = false`. Använd `LocalDate.plusDays` och `minusDays`.

Namnen är svenska här eftersom de är regeldata. Visning i appen löser Claude senare med strängresurser; ändra inte `strings.xml`.

I `HelgdagarTest.kt`: lägg till en ny `@Test`-metod (behåll befintliga `paskdagen`-tester). Använd **JUnit 4** (`org.junit.Test`, `org.junit.Assert.assertEquals`) — **inte** `kotlin.test`. Jämför `Helgdag.datum` mot `LocalDate.of(...)` för varje element i listan, och asserta att `namn` stämmer där uppgiften anger det.

## Exempel
- 2026 → 2026-04-03, 2026-04-05, 2026-04-06, 2026-05-14, 2026-05-24
- 2025 → 2025-04-18, 2025-04-20, 2025-04-21, 2025-05-29, 2025-06-08
- 2008 → Kristi himmelsfärdsdag 2008-05-01, Pingstdagen 2008-05-11
- Listan har alltid 5 element.

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*HelgdagarTest"

## Gör inte
- Ändra andra filer.
- Ändra `paskdagen`.
- Lägga till beroenden.
- Lägga filer i repots rot.

utfall: utförd av Claude (gren claude/flextid-helgdagar-gahem), 2026-10-21
