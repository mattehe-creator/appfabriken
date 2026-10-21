# Uppgift: Tolka förslag med okänd komponentgrupp

uppdrag: uppdrag/2026-10-21-styrelserummet-k3-gransfall.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/TolkaForslagGransfallOkandGruppTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/TolkaForslag.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/TolkaForslagTest.kt

## Uppgift
Testa `tolkaForslag(jsonText, filnamn)` när `grupp` är **okänd** (t.ex. `"BALCONY"` eller `"Stomme"` med fel casing om enum kräver STOMME).

Förväntat beteende:
- **Tydlig anmärkning** om okänd grupp (text innehåller gruppnamnet).
- Komponenten ska **inte** finnas i `komponenter` (hoppas över).
- **Inget okontrollerat undantag** ska kastas.

Vid fel: rätta högst i `TolkaForslag.kt`.

## Exempel
- JSON med en komponent `"grupp":"OGILTIG"` → `komponenter` tom, minst en anmärkning med "okänd grupp".
- Giltig komponent + ogiltig i samma lista → endast giltig i listan, anmärkning för den ogiltiga.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*TolkaForslagGransfallOkandGruppTest"

## Gör inte
- Ändra `TolkaForslagTest.kt`.
- Lägga till beroenden.
