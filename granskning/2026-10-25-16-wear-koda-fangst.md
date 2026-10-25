beslut: underkänd
granskare: spärren
datum: 2026-10-25

## Kontrollerat
- Filen `FangstMeddelande.kt` står inte under "Ändra bara dessa filer" (fel sökväg eller fel fil).
- Filen `FangstMeddelandeTest.kt` står inte under "Ändra bara dessa filer" (fel sökväg eller fel fil).
- Filen `rostfangst/src/main/kotlin/se/tmconnect/rostfangst/wear/FangstMeddelande.kt` är tom.
- Filen `rostfangst/src/test/kotlin/se/tmconnect/rostfangst/wear/FangstMeddelandeTest.kt` är tom.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2
# Uppgift: Kodning av fångstmeddelande (UTF-8)

## Vad som saknades
- Filen `FangstMeddelande.kt` står inte under "Ändra bara dessa filer" (fel sökväg eller fel fil).
- Filen `FangstMeddelandeTest.kt` står inte under "Ändra bara dessa filer" (fel sökväg eller fel fil).
- Filen `rostfangst/src/main/kotlin/se/tmconnect/rostfangst/wear/FangstMeddelande.kt` är tom.
- Filen `rostfangst/src/test/kotlin/se/tmconnect/rostfangst/wear/FangstMeddelandeTest.kt` är tom.

uppdrag: uppdrag/vantar/2026-10-25-rostfangst-klocka.md
försök: 2
beror-på: 2026-10-25-15-wear-sokvagar-f2.md

## Ändra bara dessa filer
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/wear/FangstMeddelande.kt (ny)
- rostfangst/src/test/kotlin/se/tmconnect/rostfangst/wear/FangstMeddelandeTest.kt (ny)

## Läs som förebild (ändra inte)
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/wear/WearSokvagar.kt

## Uppgift
Implementera rena funktioner (inga Android-import):
- `fun kodaFangst(text: String): ByteArray` — UTF-8-bytes av `text.trim()`.
- `fun avkodaFangst(data: ByteArray): String?` — avkodar UTF-8, trimmar, returnerar `null` om resultatet är tomt, längre än 2000 tecken, eller bara blanksteg.

## Exempel
- `kodaFangst("  hej  ")` och `avkodaFangst(...)` → `"hej"`.
- `avkodaFangst("   ".toByteArray())` → `null`.
- `avkodaFangst(ByteArray(2001) { 'a'.code.toByte() })` → `null`.
- Svenska tecken: `"påminn mig"` roundtrip.

## Test som ska gå igenom
gradle :rostfangst:testDebugUnitTest --tests "*FangstMeddelandeTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.

## Frågor till Claude
- inga
