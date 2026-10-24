# Uppgift: Test av säkerhetskopians omvandling för mätare och avläsningar

uppdrag: uppdrag/claude/2026-10-25-natt-testtackning.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- husloggen/src/test/kotlin/se/tmconnect/husloggen/export/SakerhetskopiaMatareAvlasningTest.kt (ny)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/export/SakerhetskopiaModell.kt (raderna med `matareTillSakerhetskopia` till `arSakerhetskopiaAvlasningLik`)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/data/Matare.kt

## Uppgift
Skriv JUnit 4-tester (`org.junit.Test`, `assertEquals`, `assertTrue`, `assertFalse`, `assertNull`, `assertThrows`) i paketet `se.tmconnect.husloggen.export` för:

- `matareTillSakerhetskopia(matare: Matare, bostadIdTillIndex: Map<Long, Int>): SakerhetskopiaMatare`
- `sakerhetskopiaMatareTillMatare(export: SakerhetskopiaMatare, bostadId: Long): Matare`
- `avlasningTillSakerhetskopia(avlasning: Avlasning, matareIdTillIndex: Map<Long, Int>): SakerhetskopiaAvlasning`
- `sakerhetskopiaAvlasningTillAvlasning(export: SakerhetskopiaAvlasning, matareId: Long): Avlasning`
- `arSakerhetskopiaMatareLik(a, b)` och `arSakerhetskopiaAvlasningLik(a, b)`

Importera `se.tmconnect.husloggen.data.Matare`, `se.tmconnect.husloggen.data.Avlasning` och `java.time.LocalDate`.

`Matare` och `Avlasning` har fältet `skapad = System.currentTimeMillis()`, så jämför aldrig hela `Matare`- eller `Avlasning`-objekt med `assertEquals`. Jämför fält för fält. `SakerhetskopiaMatare` och `SakerhetskopiaAvlasning` saknar `skapad` och kan jämföras hela.

## Exempel
- `matareTillSakerhetskopia(Matare(id = 7, bostadId = 3, namn = "El", enhet = "kWh", anteckning = "Huvudmätare", prisOrePerEnhet = 150), mapOf(3L to 0))` → `SakerhetskopiaMatare(namn = "El", enhet = "kWh", anteckning = "Huvudmätare", bostadIndex = 0, prisOrePerEnhet = 150)`
- `matareTillSakerhetskopia(Matare(id = 7, bostadId = 3, namn = "El", enhet = "kWh"), mapOf(1L to 0))` → kastar `NoSuchElementException` (bostad 3 saknas i kartan). Använd `assertThrows(NoSuchElementException::class.java) { ... }`.
- `sakerhetskopiaMatareTillMatare(SakerhetskopiaMatare(namn = "  Vatten ", enhet = " m3 ", anteckning = "  ", prisOrePerEnhet = -5), bostadId = 2)` → `id == 0L`, `bostadId == 2L`, `namn == "Vatten"`, `enhet == "m3"`, `anteckning == null`, `prisOrePerEnhet == null` (negativt pris tas bort)
- `sakerhetskopiaMatareTillMatare(SakerhetskopiaMatare(namn = "El", enhet = "kWh", prisOrePerEnhet = 0), bostadId = 1)` → `prisOrePerEnhet == 0L`
- `avlasningTillSakerhetskopia(Avlasning(id = 5, matareId = 9, datum = LocalDate.of(2026, 3, 1), varde = 12_345_600), mapOf(9L to 2))` → `SakerhetskopiaAvlasning(matareIndex = 2, datum = LocalDate.of(2026, 3, 1), vardeTusendel = 12_345_600)`
- `sakerhetskopiaAvlasningTillAvlasning(SakerhetskopiaAvlasning(matareIndex = 1, datum = LocalDate.of(2026, 3, 1), vardeTusendel = 500), matareId = 42)` → `id == 0L`, `matareId == 42L`, `datum == LocalDate.of(2026, 3, 1)`, `varde == 500L`
- `arSakerhetskopiaMatareLik(SakerhetskopiaMatare("El", "kWh", null, 0, 150), SakerhetskopiaMatare("El", "kWh", null, 2, 99))` → `true` (bostadIndex och pris jämförs inte)
- `arSakerhetskopiaMatareLik(SakerhetskopiaMatare("El", "kWh"), SakerhetskopiaMatare("El", "KWh"))` → `false`
- `arSakerhetskopiaAvlasningLik(SakerhetskopiaAvlasning(0, LocalDate.of(2026, 1, 1), 500), SakerhetskopiaAvlasning(0, LocalDate.of(2026, 1, 1), 500))` → `true`
- `arSakerhetskopiaAvlasningLik(SakerhetskopiaAvlasning(0, LocalDate.of(2026, 1, 1), 500), SakerhetskopiaAvlasning(0, LocalDate.of(2026, 1, 1), 501))` → `false`

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*SakerhetskopiaMatareAvlasningTest"

## Gör inte
- Ändra andra filer.
- Ändra `SakerhetskopiaModell.kt`. Om ett exempel inte stämmer med koden: skriv det i PR-beskrivningen och låt testet följa exemplet.
- Jämföra hela `Matare`- eller `Avlasning`-objekt (fältet `skapad` skiljer sig).
- Lägga till beroenden.

utfall: underkänd 2026-10-24 av Cursor (PR #672). Ersatt av 2026-10-25-03-test-kopia-matare-avlasning-f2.md
