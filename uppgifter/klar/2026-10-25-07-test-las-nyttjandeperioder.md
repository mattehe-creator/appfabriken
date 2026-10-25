# Uppgift: Test av inläsning av nyttjandeperioder från textrader

uppdrag: uppdrag/claude/2026-10-25-natt-testtackning.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/LasNyttjandeperioderTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/NyttjandeperioderTabell.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Komponentgrupp.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/NyttjandeperioderTabellGransfallTest.kt

## Uppgift
Skriv JUnit 4-tester (`org.junit.Test`, `assertEquals`) för `lasNyttjandeperioder(rader: List<String>): List<NyttjandeperiodRad>`.

Funktionen hoppar alltid över första raden (rubriken). Varje övrig rad delas på `;`. Rader med färre än 4 delar, okänd grupp (stora bokstäver krävs) eller förslag/andel som inte är heltal hoppas över. Intervallet blir `null` om texten är `"0"`, tom eller inte innehåller "tal-tal".

Testa varje rad för sig med en rubrikrad först:

```kotlin
private fun las(rad: String) = lasNyttjandeperioder(listOf("grupp;intervall;forslag;andel", rad))
```

Importera `se.tmconnect.styrelserummet.modell.Komponentgrupp`. `NyttjandeperiodRad` är en dataklass och kan jämföras hela, till exempel `assertEquals(listOf(NyttjandeperiodRad(Komponentgrupp.STOMME, 75..120, 100, 63)), las("STOMME;75-120;100;63"))`.

## Exempel
- `las("STOMME;75-120;100;63;FAR tabell;nej")` → `[NyttjandeperiodRad(STOMME, 75..120, 100, 63)]` (delar efter den fjärde ignoreras)
- `las("STAMMAR;50;50;6")` → `[NyttjandeperiodRad(STAMMAR, null, 50, 6)]` (ett ensamt tal blir inget intervall)
- `las("MARK_OCH_GARAGE;0;0;0")` → `[NyttjandeperiodRad(MARK_OCH_GARAGE, null, 0, 0)]`
- `las("FASAD; 40 - 100 ;40;5")` → `[NyttjandeperiodRad(FASAD, 40..100, 40, 5)]`
- `las(" TAK ;30-50;40;4")` → `[NyttjandeperiodRad(TAK, 30..50, 40, 4)]` (gruppnamnet trimmas)
- `las("VARME;;80;6")` → `[NyttjandeperiodRad(VARME, null, 80, 6)]`
- `las("tak;30-50;40;4")` → tom lista (små bokstäver är okänd grupp)
- `las("TAK;30-50;40")` → tom lista (bara 3 delar)
- `las("HISS;25-30;tjugo;2")` → tom lista (förslaget är inte ett heltal)
- `las("")` → tom lista
- `lasNyttjandeperioder(emptyList())` → tom lista
- `lasNyttjandeperioder(listOf("STOMME;75-120;100;63"))` → tom lista (första raden räknas alltid som rubrik)

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*LasNyttjandeperioderTest"

## Gör inte
- Ändra andra filer.
- Ändra `NyttjandeperioderTabell.kt` eller `nyttjandeperioder.csv`. Om ett exempel inte stämmer med koden: skriv det i PR-beskrivningen och låt testet följa exemplet.
- Lägga till beroenden.

utfall: underkänd 2026-10-24 av Cursor (PR #680). Ersatt av 2026-10-25-07-test-las-nyttjandeperioder-f2.md
