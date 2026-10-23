# Uppgift: Exempeltexter årsredovisning

uppdrag: uppdrag/2026-10-23-styrelserummet-exempelhandlingar.md
försök: 1
beror-på: 2026-10-23-styrelserummet-exempel-06-texter-underhallsplan-f2.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelTexter.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelTexterArTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/SyntetiskArsredovisningTest.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/HittaByggnadensVarde.kt

## Uppgift
`fun arsredovisningSidor(): List<String>` returnerar minst en sida med:

- kort **förvaltningsberättelse** (påhittad, nämner `Brf Exempelgården` och räkenskapsåret `RAKENSKAPSAR`)
- **balansräkning** med två kolumner (innevarande år först, föregående år som andra kolumn med något lägre belopp)
- raden **Byggnader och mark** med innevarande års belopp = `BYGGNADENS_VARde` formaterat i kronor med mellanslag (samma som `HittaByggnadensVarde` ska hitta)

Test `arsredovisningInnehallerByggnaderOchMark`:

- sammanslagen text innehåller `Byggnader och mark`
- sammanslagen text innehåller `27 000 000` (formaterat)
- `hittaByggnadensVarde(sammanslagenText)` returnerar `BYGGNADENS_VARde`

## Exempel
- Balansrad: `Byggnader och mark 27 000 000 26 500 000` (föregående år får vara påhittat men lägre).

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*ExempelTexterArTest"

## Gör inte
- Ändra `HittaByggnadensVarde.kt`.
- Lägga till riktiga revisorsnamn.
