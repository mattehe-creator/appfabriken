# Uppgift: Kort rubrik för förslag på klockan

uppdrag: uppdrag/vantar/2026-10-25-rostfangst-klocka.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/wear/KlockRubrik.kt (ny)
- rostfangst/src/test/kotlin/se/tmconnect/rostfangst/wear/KlockRubrikTest.kt (ny)

## Läs som förebild (ändra inte)
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/regler/ForslagTyp.kt
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/regler/PaminnelseFordrojning.kt

## Uppgift
`fun klockRubrik(typ: String, titel: String, start: Instant?, zon: ZoneId, idag: LocalDate = LocalDate.now(zon)): String`

Bygg en kort svensk rad (högst **40 tecken** inklusive ellips om avkortning). Format: `"<Typ>: <titel>"` med valfri tid/datum efter komma när `start` finns.

Typ-prefix (exakt):
- `KALENDER` → `Kalender`
- `PAMINNELSE` → `Påminnelse`
- `ATT_GORA` → `Att göra`
- `ANTECKNING` → `Anteckning`
- `FRAGA` → `Fråga`
- `HEM` → `Hem`
- Okänd typ → `Förslag`

Tid/datum: använd `start.atZone(zon)`. Om lokal datum är `idag` + 1 dag, skriv `i morgon HH:mm` (24 h). Om samma kalenderdag som `idag`, skriv bara `HH:mm`. Annars kort datum `d/M HH:mm`.

Om hela strängen skulle bli längre än 40 tecken, korta `titel` med `…` så att resultatet passar.

## Exempel
(zon `Europe/Stockholm`, `idag` = 2026-10-25, `start` = 2026-10-26 07:00 UTC+2)
- `klockRubrik("PAMINNELSE", "ring tandläkaren", start, zon, idag)` → `"Påminnelse: ring tandläkaren, i morgon 09:00"` (≤ 40 tecken; korta titel om nödvändigt).
- `klockRubrik("ATT_GORA", "mjölk", null, zon)` → `"Att göra: mjölk"`.
- `klockRubrik("HEM", "tänd lampan", null, zon)` → `"Hem: tänd lampan"`.
- Mycket lång titel → max 40 tecken totalt.

## Test som ska gå igenom
gradle :rostfangst:testDebugUnitTest --tests "*KlockRubrikTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
