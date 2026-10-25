# Uppgift: Kodning av förslagsmeddelande (JSON)

uppdrag: uppdrag/vantar/2026-10-25-rostfangst-klocka.md
försök: 1
beror-på: 2026-10-25-15-wear-sokvagar.md

## Ändra bara dessa filer
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/wear/ForslagMeddelande.kt (ny)
- rostfangst/src/test/kotlin/se/tmconnect/rostfangst/wear/ForslagMeddelandeTest.kt (ny)

## Läs som förebild (ändra inte)
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/regler/Forslag.kt

## Uppgift
Dataklass `ForslagMeddelande(val id: Long, val typ: String, val rubrik: String)`.

- `fun kodaForslag(id: Long, typ: String, rubrik: String): ByteArray` — JSON med nycklarna `id`, `typ`, `rubrik`. Rubriken trimmas; om den är längre än 120 tecken avkortas den till 119 tecken plus tecknet `…` (U+2026).
- `fun avkodaForslag(data: ByteArray): ForslagMeddelande?` — `null` vid ogiltig JSON, saknade fält, negativt `id`, tom `typ`, eller tom `rubrik` efter trim.

Använd `org.json` (Android-plattformen har det). I enhetstest är `org.json` bara en stubb som kastar fel, så testklassen måste ha `@RunWith(RobolectricTestRunner::class)` (Robolectric finns redan som testberoende i modulen). Lägg inte till något beroende.

## Exempel
- `kodaForslag(42, "PAMINNELSE", "Ring tandläkaren")` avkodas till samma värden.
- Rubrik 121 tecken → avkortad med `…` i JSON.
- `{` ogiltig → `null`.

## Test som ska gå igenom
gradle :rostfangst:testDebugUnitTest --tests "*ForslagMeddelandeTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
