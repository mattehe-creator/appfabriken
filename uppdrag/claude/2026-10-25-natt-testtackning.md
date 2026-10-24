# Uppdrag: Testtäckning för otestade rena funktioner (natten 25 oktober)

Skrivet av Claude, lagt direkt som uppgifter till den lokala modellen (ingen Cursor-uppdelning).

## Bakgrund
En genomgång 2026-10-25 (grep efter varje funktionsnamn i modulens `src/test`) visade rena funktioner i Husloggen, Styrelserummet och Flextid som inget test anropar: föregående avläsning, sortering av dokument, säkerhetskopians omvandlingar för mätare, avläsningar, dokument, planerade kostnader, bostäder och uppgifter, några små hjälpfunktioner, inläsningen av nyttjandeperioder, restfördelningen i `fordela`, `forstaLordagenFran`, `procent` och `saldoVidOchMed`. Inget fel hittades i funktionerna; uppdraget lägger bara till tester så att framtida ändringar inte bryter beteendet obemärkt.

Filer som redan berörs av uppgifter i `uppgifter/ny/` (bland annat `MatareForbrukning.kt`, `AvlasningCsv.kt`, `HusHistorikFilnamn.kt`, `TidFormatering.kt`, `VanligDagForslag.kt`, `StamplaInPaminnelse.kt`, `Arbetsdagar.kt`) samt `Arbetstid.kt`, `TidRegler.kt`, `TomVeckomask*` och `CsvCell.kt` ändras inte.

Styrelserummet är en ren JVM-modul, så dess testkommando är `gradle :styrelserummet:test`, inte `testDebugUnitTest`.

## Uppgifter
`uppgifter/ny/2026-10-25-01` till `-10`. Var och en lägger bara till en ny testfil.

1. `01-test-foregaende-avlasning` (Husloggen)
2. `02-test-dokument-sortering` (Husloggen)
3. `03-test-kopia-matare-avlasning` (Husloggen)
4. `04-test-kopia-dokument-plan` (Husloggen)
5. `05-test-kopia-uppgift-bostad` (Husloggen)
6. `06-test-sma-hjalpfunktioner` (Husloggen)
7. `07-test-las-nyttjandeperioder` (Styrelserummet)
8. `08-test-fordela-rest` (Styrelserummet)
9. `09-test-forsta-lordag-procent` (Styrelserummet)
10. `10-test-saldo-vid-och-med` (Flextid)

## Klart när
Alla tio är mergade med grönt bygge och varje ny testklass går igenom med kommandot i sin uppgift.

## Gör inte
- Ändra produktionskod.
- Ändra filer utanför den nya testfilen i respektive uppgift.
- Lägga till beroenden.
