# Uppgift: locale för datum och pris i UI

uppdrag: uppdrag/2026-10-12-sprakformat-och-engelska-skarmbilder.md
försök: 2
beror-på: ingen

## Vad som saknades
- I `KopDetaljSkarm.kt` togs hela blocket för kvittoförhandsvisning bort (`kvittoUri?.let { … Image … }` med `BitmapFactory`, `LocalContext`, `beraknaInSampleSize`, m.m.). Det ska **finnas kvar oförändrat** förutom att du lägger till locale-raden och skickar `locale` till `formateraDatum`/`formateraPrisKr`.
- `FormateringTest` ska få **nya** assert-rader enligt uppgiftens exempel (behåll befintliga svenska tester), inte bara ett extra datum med annat datum än exemplen.

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopDetaljSkarm.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopListRad.kt
- garantivalvet/src/test/kotlin/se/tmconnect/garantivalvet/regler/FormateringTest.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/Formatering.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/MainActivity.kt (PDF-export skickar redan `resources.configuration.locales[0]` till `byggUnderlag`)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/paminnelse/PaminnelseWorker.kt (använder `Locale.getDefault()`)

## Uppgift
Använd **en** väg överallt i Compose: hämta enhetens locale med `LocalConfiguration.current.locales[0]` och skicka den till varje anrop av `formateraDatum` och `formateraPrisKr`.

1. I `KopDetaljSkarm`: läs locale en gång i composable-kroppen. Skicka locale till alla `formateraDatum(...)` och `formateraPrisKr(...)`. **Rör inte** kvittoförhandsvisningen — samma imports och samma `kvittoUri`-block som på main.
2. I `KopListRad`: samma — locale från `LocalConfiguration`, skicka till `formateraDatum(garantiSlutDatum, locale)`.
3. Ändra **inte** standardvärdet `sv-SE` i `Formatering.kt`.
4. I `FormateringTest`: behåll befintliga tester. **Lägg till** (som nya rader eller ett nytt `@Test`) assertEquals enligt exemplen nedan.

## Exempel
- `formateraDatum(LocalDate.of(2026, 8, 1), Locale.ENGLISH)` → `"Aug 1, 2026"`
- `formateraDatum(LocalDate.of(2026, 8, 1), Locale.forLanguageTag("sv-SE"))` → `"1 aug. 2026"`
- `formateraPrisKr(123450L, Locale.US)` → `"1,234.50 kr"`
- `formateraPrisKr(123450L, Locale.forLanguageTag("sv-SE"))` → `"1 234,50 kr"`

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*FormateringTest"

## Gör inte
- Ta bort eller förenkla annan UI-kod (kvitto, knappar, export).
- Ändra andra filer (inklusive `MainActivity.kt` om locale redan skickas till `byggUnderlag`).
- Lägga till beroenden.
- Ändra lagrade värden eller `Formatering.kt`.

utfall: godkänd 2026-10-12 (två assertEquals enligt uppgiftens svenska exempel i FormateringTest)
