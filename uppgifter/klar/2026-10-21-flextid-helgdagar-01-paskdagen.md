# Uppgift: Påskdagen för ett år

uppdrag: uppdrag/claude/2026-10-21-flextid-helgdagar.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/Helgdagar.kt (ny)
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/HelgdagarTest.kt (ny)

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/Arbetsdagar.kt
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/TidReglerTest.kt

## Uppgift
Skapa filen `Helgdagar.kt` i paketet `se.tmconnect.flextid.regler` med:

```kotlin
data class Helgdag(val datum: LocalDate, val namn: String, val arAfton: Boolean = false)

fun paskdagen(ar: Int): LocalDate
```

`paskdagen` räknar ut påskdagen i den gregorianska kalendern med den anonyma gregorianska algoritmen (Meeus/Jones/Butcher), bara heltalsräkning:

```
a = ar % 19
b = ar / 100
c = ar % 100
d = b / 4
e = b % 4
f = (b + 8) / 25
g = (b - f + 1) / 3
h = (19 * a + b - d - g + 15) % 30
i = c / 4
k = c % 4
l = (32 + 2 * e + 2 * i - h - k) % 7
m = (a + 11 * h + 22 * l) / 451
manad = (h + l - 7 * m + 114) / 31
dag = ((h + l - 7 * m + 114) % 31) + 1
```

Kasta `IllegalArgumentException` om `ar` är mindre än 1900 eller större än 2200.

`Helgdag` används inte ännu, men ska finnas (nästa uppgift använder den).

## Exempel
- 2008 → 2008-03-23
- 2019 → 2019-04-21
- 2024 → 2024-03-31
- 2025 → 2025-04-20
- 2026 → 2026-04-05
- 2027 → 2027-03-28
- 2038 → 2038-04-25
- 1899 → IllegalArgumentException

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*HelgdagarTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Använda flyttal.

utfall: godkänd 2026-10-21 (inget)
