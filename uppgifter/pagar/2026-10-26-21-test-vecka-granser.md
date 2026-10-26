# Uppgift: Test av måndag och söndag i samma vecka (Flextid)

uppdrag: uppdrag/claude/2026-10-26-testtackning-dag.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/VeckaGranserTest.kt (ny)

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/TidRegler.kt (funktionerna `mandagISammaVecka` och `sondagISammaVecka`)

## Uppgift
Skriv JUnit 4-tester (`org.junit.Test`, `assertEquals`) i paketet `se.tmconnect.flextid.regler` för `mandagISammaVecka(datum: LocalDate): LocalDate` och `sondagISammaVecka(datum: LocalDate): LocalDate`. Veckan går måndag till söndag. Importera `java.time.LocalDate`.

## Exempel
- 2026-10-26 (måndag): måndag 2026-10-26, söndag 2026-11-01.
- 2026-10-28 (onsdag): måndag 2026-10-26, söndag 2026-11-01.
- 2026-10-25 (söndag): måndag 2026-10-19, söndag 2026-10-25.
- 2026-01-01 (torsdag): måndag 2025-12-29, söndag 2026-01-04 (veckan korsar årsskiftet).

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*VeckaGranserTest"

## Gör inte
- Ändra produktionskod eller andra filer än den nya testfilen.
- Lägga till beroenden.
- Ändra databasschemat.
