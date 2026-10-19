# Uppgift: Tolka tolkningssvar (JSON)

uppdrag: uppdrag/vantar/2026-10-19-styrelserummet-underlag.md
försök: 1
beror-på: cursor:2026-10-19-styrelserummet-underlag

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/TolkaForslag.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/TolkaForslagTest.kt (ny)

## Uppgift
`fun tolkaForslag(json: String, filnamn: String): ForslagResultat` enligt schema i uppdraget.

Ogiltig JSON, okänd grupp eller icke-heltal: rad med `LAG` och anmärkning, eller hoppa över raden med anmärkning.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*TolkaForslagTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
