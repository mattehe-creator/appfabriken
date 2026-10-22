beslut: underkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- Ändringar utanför uppgiften: nej (endast tom testfil och uppgiftsflytt till `pagar/` i PR #587).
- Bara tillåtna filer: delvis — `OverlatelseavgiftGiltigTest.kt` skapades på rätt sökväg, men `Overlatelseavgift.kt` är oförändrad trots att den ska utökas.
- Uppgiftens beteende: nej — varken `avgiftarGiltig` eller `avgiftFelmeddelande` finns; testfilen är tom (0 byte), inga JUnit-tester.
- Tester mot exemplen: nej — inga tester; `:styrelserummet:test --tests "*OverlatelseavgiftGiltigTest*"` misslyckas med «No tests found».
- Nya beroenden, behörigheter eller nätverk: nej.
- Hårdkodade UI-strängar: ej tillämpligt (ingen UI-kod); felmeddelandefunktion saknas helt.
- JUnit 4: ej tillämpligt — ingen testkod.

## Rättat av Cursor
- inget (saknad implementation, inte småfel)

## Vid underkänd: försök 2

```
# Uppgift: avgiftarGiltig och felmeddelande

uppdrag: uppdrag/2026-10-22-styrelserummet-stammotidplan.md
försök: 2
beror-på: 2026-10-22-styrelserummet-stamma-14-avgift-berakna-f2.md

## Vad som saknades
- PR #587 / commit «Lokal modell: … (okommittade ändringar efter Aider)» lade bara till en **tom** `OverlatelseavgiftGiltigTest.kt` (0 byte). `Overlatelseavgift.kt` ändrades inte.
- Implementera båda funktionerna i `Overlatelseavgift.kt` och skriv JUnit 4-tester som assertar exakt mot exemplen nedan (giltighet, null-meddelande, felmeddelande innehåller rätt siffror och typord).

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Overlatelseavgift.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/OverlatelseavgiftGiltigTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/regler/STAMMA.md
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/OverlatelseavgiftBeraknaTest.kt (referens-`data` via `lasStammaData`, promille 25/10)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/StegvisHojningTest.kt (JUnit 4-stil)

## Uppgift
Lägg till i `Overlatelseavgift.kt` (samma paket som befintliga avgiftsfunktioner):

```kotlin
fun avgiftarGiltig(avgift: Long, prisbasbelopp: Long, typ: Avgiftstyp, data: Map<String, StammaRad>): Boolean

fun avgiftFelmeddelande(avgift: Long, prisbasbelopp: Long, typ: Avgiftstyp, data: Map<String, StammaRad>): String?
```

**Gräns per typ** (återanvänd befintliga hjälpfunktioner):
- `OVERLATELSE` → `hogstaOverlatelseavgift(prisbasbelopp, data)`
- `PANTSATTNING` → `hogstaPantsattningsavgift(prisbasbelopp, data)`
- `ANDRAHAND` → `hogstaAndrahandsavgift(prisbasbelopp)`

**`avgiftarGiltig`:** returnera `true` om `avgift <=` respektive högsta gräns, annars `false`.

**`avgiftFelmeddelande`:** returnera `null` om avgiften är giltig. Annars en kort **svensk** mening på klarspråk som:
- nämner avgiftstypen (överlåtelse / pantsättning / andrahand),
- anger den **inmatade** avgiften i kronor,
- anger **gränsen** i kronor (samma tal som högsta-funktionen ger),
- för **överlåtelse** och **pantsättning**: tydliggör att **stadgarna** styr den bindande nivån (referensvärde från data, **inte** ett lagtak).

Ingen juridisk rådgivning i texten. Strängen behöver inte vara exakt ordagrant, men testerna ska kunna verifiera innehåll enligt exemplen.

## Exempel
Bygg `standarddata` som i `OverlatelseavgiftBeraknaTest` (`referens_overlatelse` 25 promille → gräns **1480** kr vid pbb **59200**).

- `avgiftarGiltig(1480, 59200, OVERLATELSE, standarddata)` → `true`; `avgiftFelmeddelande(...)` → `null`
- `avgiftarGiltig(2000, 59200, OVERLATELSE, standarddata)` → `false`; felmeddelande **innehåller** `"1480"` och `"överlåtelse"` (skiftlägesokänsligt i test räcker)
- `avgiftarGiltig(6000, 59200, ANDRAHAND, standarddata)` → `false` (gräns **5920** kr)

## Test som ska gå igenom
Skriv `OverlatelseavgiftGiltigTest` med JUnit 4 (`org.junit.Test`, `org.junit.Assert`) — **inte** `kotlin.test`. Minst tre tester som motsvarar exemplen ovan (`assertTrue`/`assertFalse`, `assertNull`, `assertTrue(msg.contains(...))`).

Kör:
`gradle :styrelserummet:test --tests "*OverlatelseavgiftGiltigTest*"`

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
```

## Frågor till Claude
- inga
