# Uppgift: Kodning av utför-meddelande

uppdrag: uppdrag/vantar/2026-10-25-rostfangst-klocka.md
försök: 1
beror-på: 2026-10-25-17-wear-koda-forslag-f2.md

## Ändra bara dessa filer
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/wear/UtforMeddelande.kt (ny)
- rostfangst/src/test/kotlin/se/tmconnect/rostfangst/wear/UtforMeddelandeTest.kt (ny)

## Läs som förebild (ändra inte)
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/wear/ForslagMeddelande.kt

## Uppgift
- `fun kodaUtfor(id: Long): ByteArray` — UTF-8 av decimalsträngen för `id` (samma som `id.toString().toByteArray(Charsets.UTF_8)`).
- `fun avkodaUtfor(data: ByteArray): Long?` — parsar trimmad UTF-8 som positivt `Long`; `null` vid tom data, ogiltigt tal, noll eller negativt.

## Exempel
- `avkodaUtfor(kodaUtfor(99))` → `99`.
- `avkodaUtfor("abc".toByteArray())` → `null`.
- `avkodaUtfor(kodaUtfor(0))` → `null`.

## Test som ska gå igenom
gradle :rostfangst:testDebugUnitTest --tests "*UtforMeddelandeTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
