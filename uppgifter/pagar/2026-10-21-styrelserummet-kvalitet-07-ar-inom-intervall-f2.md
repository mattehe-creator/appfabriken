# Uppgift: arInomIntervall för nyttjandeperiod

uppdrag: uppdrag/2026-10-21-styrelserummet-kvalitet-utkast.md
försök: 2
beror-på: ingen

## Vad som saknades
- PR #338 levererade **ingen implementation**: varken `arInomIntervall` i `NyttjandeperiodForslag.kt` eller testmetoden `arInomIntervall` i `NyttjandeperiodForslagTest.kt`.
- Enda ändringen var att uppgiftsfilen flyttades till `uppgifter/pagar/` — uppgiften kräver kod och tester enligt avsnitten nedan.

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/NyttjandeperiodForslag.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/NyttjandeperiodForslagTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/NyttjandeperioderTabell.kt

## Uppgift
Lägg till i `NyttjandeperiodForslag.kt` (samma paket som `forslagFor`):

`fun arInomIntervall(ar: Int, rad: NyttjandeperiodRad): Boolean`

- Om `rad.intervallAr` **inte** är null: returnera om `ar` ligger **inklusive** i intervallet (Kotlin `IntRange`, t.ex. `40..100` → `ar in rad.intervallAr`).
- Om `rad.intervallAr` är null (fast värde i tabellen, t.ex. STAMMAR med intervallkolumn `50`): returnera `ar == rad.forslagAr`.

## Exempel
- Rad FASAD (`intervallAr = 40..100`, `forslagAr = 40`), `ar = 40` → `true`
- Rad FASAD, `ar = 100` → `true`
- Rad FASAD, `ar = 30` → `false`
- Rad STAMMAR (`intervallAr = null`, `forslagAr = 50`), `ar = 50` → `true`
- Rad STAMMAR, `ar = 49` → `false`

Tips: hämta FASAD och STAMMAR från `lasNyttjandeperioderFranResurs()` i testet, eller bygg `NyttjandeperiodRad` manuellt med samma intervall/null som ovan.

## Test som ska gå igenom
gradle :styrelserummet:testDebugUnitTest --tests "*NyttjandeperiodForslagTest.arInomIntervall"

Lägg testmetoden `arInomIntervall` i befintliga `NyttjandeperiodForslagTest.kt` med JUnit 4 (`import org.junit.Test`, `import org.junit.Assert.assertEquals` för booleans: `assertEquals(true, …)` / `assertEquals(false, …)` — **inte** `kotlin.test` eller JUnit Jupiter). Asserta **alla fem** exemplen ovan.

## Gör inte
- Ändra andra filer (inga `uppgifter/`, `BESLUTSLOGG.md`, `STATUS.md`).
- Lägga till beroenden.
- Ändra `nyttjandeperioder.csv`.
