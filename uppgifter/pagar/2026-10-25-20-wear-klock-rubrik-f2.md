# Uppgift: Kort rubrik för förslag på klockan

## Vad som saknades
- Bygget eller testerna är röda. Kör testet i uppgiften och rätta tills det är grönt. Testerna i appmodulerna använder JUnit 4: import org.junit.Test och import org.junit.Assert.assertEquals (eller assertTrue, assertFalse), inte kotlin.test.

uppdrag: uppdrag/vantar/2026-10-25-rostfangst-klocka.md
försök: 2
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

Tid/datum: använd `start.atZone(zon)`. Om lokalt datum är `idag` + 1 dag, skriv `i morgon HH:mm` (24 h). Om samma kalenderdag som `idag`, skriv bara `HH:mm`. Annars kort datum `d/M HH:mm`.

Om hela strängen skulle bli längre än 40 tecken, korta `titel` med `…` så att resultatet blir exakt 40 tecken (titeln tappar tecken, prefix och tid behålls).

## Exempel
(zon `Europe/Stockholm`, `idag` = `LocalDate.of(2026, 10, 25)`, `start` = `Instant.parse("2026-10-26T06:00:00Z")`, vilket är 07:00 lokal tid eftersom vintertid gäller från 25 oktober)
- `klockRubrik("PAMINNELSE", "ring Eva", start, zon, idag)` → `"Påminnelse: ring Eva, i morgon 07:00"` (36 tecken).
- `klockRubrik("PAMINNELSE", "a".repeat(50), null, zon, idag)` → `"Påminnelse: "` + 27 stycken `a` + `"…"` (40 tecken).
- `klockRubrik("ATT_GORA", "mjölk", null, zon, idag)` → `"Att göra: mjölk"`.
- `klockRubrik("HEM", "tänd lampan", null, zon, idag)` → `"Hem: tänd lampan"`.
- `klockRubrik("OKAND", "x", null, zon, idag)` → `"Förslag: x"`.
- `start` = `Instant.parse("2026-10-25T10:30:00Z")` med `idag` 2026-10-25 → slutar på `", 11:30"`.

## Test som ska gå igenom
gradle :rostfangst:testDebugUnitTest --tests "*KlockRubrikTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
