# Uppgift: Månads-CSV med dagtyp i klartext och helgdag

uppdrag: uppdrag/claude/2026-10-22-flextid-oversikt.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/export/ManadsCsv.kt
- flextid/src/test/kotlin/se/tmconnect/flextid/export/ManadsCsvHelgdagTest.kt (ny)

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/Helgdagar.kt

## Uppgift
I dag skrivs dagtypen som enum-namn ("SEMESTER") och helgdagar syns inte.

1. Lägg till två fält sist i `ManadsCsvTexter`, med standardvärden:
   `val typEtiketter: Map<DagTyp, String> = emptyMap()` och `val helgdagNamn: (Helgdag) -> String = { it.namn }`.
2. I `byggManadsCsv`: dagtypens cell blir `texter.typEtiketter[dag.typ] ?: dag.typ.name`.
3. Lägg till en sista kolumn per rad: `helgdagPa(dag.datum, medAftnar = true)?.let(texter.helgdagNamn) ?: ""`. Rubrikraden kommer från `texter.rubriker` och ändras inte här (Claude lägger till rubriken).
4. Importera `DagTyp`, `Helgdag` och `helgdagPa` från `se.tmconnect.flextid.regler`.

## Exempel
- En dag 2026-04-03 typ HELGDAG, typEtiketter {HELGDAG → "Helgdag"} → raden slutar med `Helgdag,Långfredagen` (avgränsaren är den som `csvRad` använder).
- En dag 2026-04-07 typ ARBETE utan etiketter → raden slutar med `ARBETE,` och en tom sista cell.
- Med `helgdagNamn = { "X" }` och dagen 2026-12-24 → sista cellen "X".

Skriv testet efter vad `csvRad` faktiskt ger för avgränsare och citattecken (läs `karna/src/main/kotlin/se/tmconnect/karna/export/`).

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*ManadsCsv*"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
