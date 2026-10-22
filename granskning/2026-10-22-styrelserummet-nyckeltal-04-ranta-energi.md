beslut: underkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- PR #481 ändrade bara flytt av uppgiftsfil från `uppgifter/ny/` till `uppgifter/pagar/`; varken `Nyckeltal.kt` eller `NyckeltalTest.kt` uppdaterades.
- Uppgiften krävde tre nya funktioner och JUnit-tester mot exemplen — saknas helt på grenen.
- CI grönt eftersom inga källfiler ändrats; det verifierar inte uppgiftens krav.
- Inga filer i fel sökväg (t.ex. repots rot); inga nya beroenden, behörigheter eller nätverk.
- Ingen UI-kod tillagd.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

# Uppgift: Räntekänslighet, energikostnad och avgiftsandel

uppdrag: uppdrag/claude/2026-10-22-styrelserummet-nyckeltal.md
försök: 2
beror-på: 2026-10-22-styrelserummet-nyckeltal-03-sparande-f2.md

## Vad som saknades
Försök 1 ändrade bara uppgiftsfilens plats (`ny` → `pagar`). Varken `Nyckeltal.kt` eller `NyckeltalTest.kt` uppdaterades. Lägg till de tre funktionerna enligt nedan och JUnit-tester som assertar alla tal i exemplen (inklusive `null` när årsavgifter är 0 för räntekänslighet).

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Nyckeltal.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/NyckeltalTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Nyckeltal.kt

## Uppgift
Lägg till i samma fil som övriga nyckeltalsfunktioner (inga flyttal):

```kotlin
fun rantekanslighetTiondelar(u: NyckeltalUnderlag): Long?
fun energikostnadPerKvm(u: NyckeltalUnderlag): Long?
fun arsavgiftsandelTiondelsProcent(u: NyckeltalUnderlag): Long?
```

- `rantekanslighetTiondelar(u)` = `avrundaKvot(u.rantebarandeSkulder * 10, u.arsavgifter)` när `u.arsavgifter > 0`, annars `null` (anropa inte `avrundaKvot` med ogiltig nämnare).
- `energikostnadPerKvm(u)` = `avrundaKvot(u.energikostnad, totalYta(u))` när `totalYta(u) > 0`, annars `null`.
- `arsavgiftsandelTiondelsProcent(u)` = `avrundaKvot(u.arsavgifter * 1000, u.rorelseintakter)` när `u.rorelseintakter > 0`, annars `null`.

## Exempel
Samma underlag som uppgift 02: årsavgifter 4 200 000, räntebärande skulder 38 000 000, resultat −350 000, avskrivningar 1 600 000, utrangeringar 0, planerat underhåll 450 000, justeringar 0, energi 1 150 000, rörelseintäkter 4 600 000, kvm bostadsrätt 5 200, kvm hyresrätt 300.
- `rantekanslighetTiondelar` → 90 (9,0 i tiondelar)
- `energikostnadPerKvm` → 209
- `arsavgiftsandelTiondelsProcent` → 913 (91,3 % i tiondels procent)

Samma underlag med `arsavgifter = 0` → `rantekanslighetTiondelar` är `null`.

## Test som ska gå igenom
gradle :styrelserummet:testDebugUnitTest --tests "*NyckeltalTest"

Lägg till minst ett `@Test` (JUnit 4: `org.junit.Test`, `org.junit.Assert.assertEquals` / `assertNull`) som bygger `NyckeltalUnderlag` enligt basexemplet och assertar alla tre funktioner samt `null` för räntekänslighet när årsavgifter är 0. Befintliga tester ska fortsätta gå. Använd `Long`-literaler i `assertEquals` där värdet är `Long`.

## Gör inte
- Ändra andra filer eller befintliga funktioner.
- Använda flyttal eller `kotlin.test`.

## Frågor till Claude
- inga
