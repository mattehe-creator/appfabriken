# Uppgift: Hitta byggnadens värde i årsredovisningen

uppdrag: uppdrag/vantar/2026-10-19-styrelserummet-underlag.md
försök: 1
beror-på: cursor:2026-10-19-styrelserummet-underlag

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/HittaByggnadensVarde.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/HittaByggnadensVardeTest.kt (ny)

## Uppgift
`data class Belopp(val kronor: Long, val iTusentals: Boolean)`

`fun hittaByggnadensVarde(text: String): Belopp?` för raderna "Byggnader och mark" eller "Byggnader" i balansräkningen. Tusentalsavgränsare med mellanslag. Tusen kronor eller kronor.

## Exempel
- `Byggnader och mark 45 000 tusen kronor` → 45_000_000 kr, iTusentals true.
- `Byggnader 12 500 000` → 12_500_000 kr, iTusentals false.
- Saknas rad → null.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*HittaByggnadensVardeTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
