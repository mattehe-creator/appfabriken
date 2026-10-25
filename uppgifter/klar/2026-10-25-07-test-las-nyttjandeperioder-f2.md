# Uppgift: Test av inläsning av nyttjandeperioder från textrader

## Vad som saknades
- `styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/LasNyttjandeperioderTest.kt` skapades aldrig — PR #680 innehåller bara commit «Uppgift startad» (flytt till `pagar/`).
- Ingen JUnit 4-klass med `las()`-hjälpmetod och `assertEquals` mot varje rad under Exempel i uppgiften.

uppdrag: uppdrag/claude/2026-10-25-natt-testtackning.md
försök: 2
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/LasNyttjandeperioderTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/NyttjandeperioderTabell.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Komponentgrupp.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/NyttjandeperioderTabellGransfallTest.kt

## Uppgift
Skriv klassen `LasNyttjandeperioderTest` med JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals`). Importera `lasNyttjandeperioder` från samma paket som förebilden och `Komponentgrupp` från modellpaketet.

Använd hjälpmetoden:

private fun las(rad: String) = lasNyttjandeperioder(listOf("grupp;intervall;forslag;andel", rad))

Minst en `@Test` per rad under **Exempel** (tom lista: `assertEquals(emptyList(), las(...))`). Jämför hela `NyttjandeperiodRad` i listor, t.ex. `assertEquals(listOf(NyttjandeperiodRad(Komponentgrupp.STOMME, 75..120, 100, 63)), las("STOMME;75-120;100;63"))`.

Funktionen under test: `lasNyttjandeperioder(rader: List<String>): List<NyttjandeperiodRad>` — hoppar över första raden (rubrik), delar på `;`, hoppar rader med färre än 4 delar, okänd grupp eller icke-heltal i forslag/andel.

## Exempel
- `las("STOMME;75-120;100;63;FAR tabell;nej")` → `[NyttjandeperiodRad(STOMME, 75..120, 100, 63)]`
- `las("STAMMAR;50;50;6")` → `[NyttjandeperiodRad(STAMMAR, null, 50, 6)]`
- `las("MARK_OCH_GARAGE;0;0;0")` → `[NyttjandeperiodRad(MARK_OCH_GARAGE, null, 0, 0)]`
- `las("FASAD; 40 - 100 ;40;5")` → `[NyttjandeperiodRad(FASAD, 40..100, 40, 5)]`
- `las(" TAK ;30-50;40;4")` → `[NyttjandeperiodRad(TAK, 30..50, 40, 4)]`
- `las("VARME;;80;6")` → `[NyttjandeperiodRad(VARME, null, 80, 6)]`
- `las("tak;30-50;40;4")` → tom lista
- `las("TAK;30-50;40")` → tom lista
- `las("HISS;25-30;tjugo;2")` → tom lista
- `las("")` → tom lista
- `lasNyttjandeperioder(emptyList())` → tom lista
- `lasNyttjandeperioder(listOf("STOMME;75-120;100;63"))` → tom lista (enda raden är rubrik)

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*LasNyttjandeperioderTest"

## Gör inte
- Ändra andra filer.
- Använd `kotlin.test` — endast JUnit 4 (`org.junit`).
- Ändra `NyttjandeperioderTabell.kt` eller `nyttjandeperioder.csv`.

utfall: godkänd 2026-10-25 (Cursor skrev hela LasNyttjandeperioderTest.kt — lokal modell lämnade tom fil)
