# Uppgift: gissaGrupp utan träff

uppdrag: uppdrag/2026-10-21-styrelserummet-kvalitet-utkast.md
försök: 1
beror-på: 2026-10-21-styrelserummet-kvalitet-04-gissa-grupp-hiss-tvatt-mark-f2.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/GrupperaNamn.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/GrupperaNamnTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/GrupperaNamnTest.kt (befintliga fall)

## Uppgift
Säkerställ att `gissaGrupp` returnerar `null` när inget nyckelord matchar, inklusive namn som liknar nyckelord men inte ska klassas (inga nya grupper).

Lägg till tester; ändra implementationen bara om något befintligt fall failar.

## Exempel
- `""` → `null`
- `"Stomme i betong"` → `null`
- `"Elisabeths trädgård"` → `null`
- `"Balkong"` (utan dörr/fönster-nyckel) → `null`

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*GrupperaNamnTest.gissaGruppNullfall"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra datamodellen.
