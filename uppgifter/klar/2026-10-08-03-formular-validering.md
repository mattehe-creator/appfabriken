# Uppgift: validera formulärfält för köp

uppdrag: uppdrag/2026-10-08-kop-databas-lista-formular.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/FormularValidering.kt (ny)
- garantivalvet/src/test/kotlin/se/tmconnect/garantivalvet/regler/FormularValideringTest.kt (ny)

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/Garanti.kt

## Uppgift
Ren Kotlin i `se.tmconnect.garantivalvet.regler`.

1. `enum class FormularFel { VAD_SAKNAS, GARANTI_OGILTIG, KOPDATUM_FRAMTID, PRIS_OGILTIGT }`
2. `fun tolkaPrisTillOre(text: String?): Long?` — tom/null → `null`. Acceptera `"1299"`, `"1 299,50"`, `"1299,5"` → öre (`129950`, `129950`, `129950`). Ogiltigt → `null`.
3. `fun valideraKopFormular(vad: String, garantiManader: Int, kopdatum: LocalDate, idag: LocalDate, prisText: String?): List<FormularFel>`
   - `VAD_SAKNAS` om `vad` är blank efter trim.
   - `GARANTI_OGILTIG` om `garantiManader` inte är 0–120.
   - `KOPDATUM_FRAMTID` om `kopdatum` är efter `idag`.
   - `PRIS_OGILTIGT` om `prisText` inte är tom/null och `tolkaPrisTillOre` returnerar null.

Returnera alla fel som gäller (ingen early return).

## Exempel
- `valideraKopFormular("  ", 24, 2026-01-01, idag = 2026-06-01, null)` → `[VAD_SAKNAS]`
- `valideraKopFormular("TV", 121, 2026-01-01, idag = 2026-06-01, null)` → `[GARANTI_OGILTIG]`
- `valideraKopFormular("TV", 24, 2026-07-01, idag = 2026-06-01, null)` → `[KOPDATUM_FRAMTID]`
- `tolkaPrisTillOre("1 299,50")` → `129950L`
- `tolkaPrisTillOre("abc")` → `null`; då `valideraKopFormular("TV", 24, 2026-01-01, idag = 2026-06-01, "abc")` → `[PRIS_OGILTIGT]`

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*FormularValideringTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Android-klasser i valideringslogiken.

utfall: godkänd 2026-10-09 (Claude flyttade filerna till rätt mapp och avrundade öre med Math.round)
