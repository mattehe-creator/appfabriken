beslut: underkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- Endast tillåtna filer ändrade (testfil + flytt av uppgiftsfil till `pagar/`); inga filer i fel sökväg.
- Uppgiften inte utförd: `HelgdagarTabellTest.kt` är tom (0 byte), ingen testklass och inga tester.
- `./gradlew :styrelserummet:test --tests "*HelgdagarTabellTest"` misslyckas lokalt med «No tests found for given includes».
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Ingen UI-kod; hårdkodade strängar i UI ej aktuellt.
- FRAGA.md fanns inte i repots rot.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

```
# Uppgift: helgdagar 2025–2030 mot tabell

uppdrag: uppdrag/2026-10-22-styrelserummet-stammotidplan.md
försök: 2
beror-på: 2026-10-22-styrelserummet-stamma-04-narmaste-arbetsdag.md

## Vad som saknades
- `styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/HelgdagarTabellTest.kt` committades tom. Filen måste innehålla en JUnit 4-testklass (`org.junit.Test`, `org.junit.Assert`) som anropar `arSvenskHelgdag` med datumen nedan.
- Kommandot i uppgiften (`gradle :styrelserummet:test --tests "*HelgdagarTabellTest"`) måste hitta minst en testmetod och bli grönt.

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/HelgdagarTabellTest.kt (ny/innehåll)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Helgdagar.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/HelgdagarMidsommarTest.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/HelgdagarRodDagTest.kt

## Uppgift
Testa att `arSvenskHelgdag` stämmer för **midsommarafton** och **midsommardagen** åren 2025–2030 enligt tabellen (datum från lag 1989:253 om midsommar + afton dagen före):

| År | Midsommarafton | Midsommardagen |
|---|---|---|
| 2025 | 2025-06-20 | 2025-06-21 |
| 2026 | 2026-06-19 | 2026-06-20 |
| 2027 | 2027-06-25 | 2027-06-26 |
| 2028 | 2028-06-23 | 2028-06-24 |
| 2029 | 2029-06-22 | 2029-06-23 |
| 2030 | 2030-06-21 | 2030-06-22 |

Lägg också tester: `arSvenskHelgdag` true för 2026-04-03, 2026-04-05, 2026-04-06, 2026-05-14; false för 2026-04-07 och 2026-05-24 (pingst, söndag).

## Exempel
- `arSvenskHelgdag(LocalDate.of(2025, 6, 20))` → true (midsommarafton 2025)
- `arSvenskHelgdag(LocalDate.of(2025, 6, 21))` → true (midsommardagen 2025)
- `arSvenskHelgdag(LocalDate.of(2026, 4, 7))` → false
- `arSvenskHelgdag(LocalDate.of(2026, 5, 24))` → false (pingst, söndag)

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*HelgdagarTabellTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Använda kotlin.test; använd org.junit.
```

## Frågor till Claude
- inga
