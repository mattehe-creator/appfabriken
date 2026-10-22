# Uppgift: Månads-CSV med dagtyp i klartext och helgdag

uppdrag: uppdrag/claude/2026-10-22-flextid-oversikt.md
försök: 2
beror-på: ingen

## Vad som saknades
- Grenen innehåller bara commit «Uppgift startad» (flytt till `uppgifter/pagar/`). Varken `ManadsCsv.kt` eller testfilen har ändrats eller skapats — hela uppgiften saknas.

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/export/ManadsCsv.kt
- flextid/src/test/kotlin/se/tmconnect/flextid/export/ManadsCsvHelgdagTest.kt (ny)

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/Helgdagar.kt
- karna/src/main/kotlin/se/tmconnect/karna/export/CsvTabell.kt

## Uppgift
I dag skrivs dagtypen som enum-namn (`"SEMESTER"`) och helgdagar syns inte i CSV-exporten.

1. Lägg till två fält sist i `ManadsCsvTexter`, med standardvärden:
   `val typEtiketter: Map<DagTyp, String> = emptyMap()` och `val helgdagNamn: (Helgdag) -> String = { it.namn }`.
2. I `byggManadsCsv`: dagtypens cell blir `texter.typEtiketter[dag.typ] ?: dag.typ.name`.
3. Lägg till en sista kolumn per datarad: `helgdagPa(dag.datum, medAftnar = true)?.let(texter.helgdagNamn) ?: ""`. Rubrikraden kommer från `texter.rubriker` och ändras inte här (Claude lägger till rubriken i UI senare).
4. Importera `DagTyp`, `Helgdag` och `helgdagPa` från `se.tmconnect.flextid.regler`.

## Exempel
Avgränsare och citattecken ska följa `csvRad` / `csvCell` (semikolon, CRLF i dokumentet).

- En dag 2026-04-03 typ `HELGDAG`, `typEtiketter = mapOf(DagTyp.HELGDAG to "Helgdag")` → dataraden slutar med `Helgdag;Långfredagen` (Långfredagen från `helgdagPa`).
- En dag 2026-04-07 typ `ARBETE` utan etiketter → raden slutar med `ARBETE;` och en tom sista cell.
- Med `helgdagNamn = { "X" }` och dagen 2026-12-24 (julafton, `medAftnar = true`) → sista cellen `X`.

I `ManadsCsvHelgdagTest`: bygg minimal `List<Dag>`, `Installning()` och `ManadsCsvTexter(rubriker = listOf("datum", "start", "slut", "rast", "arbetad", "utfall", "typ", "helgdag"))` (rubriker räcker som platshållare). Anropa `byggManadsCsv(..., arManad = YearMonth.of(2026, 4), locale = Locale.forLanguageTag("sv-SE"))` och kontrollera att en specifik datarad (eller hela CSV om enklare) matchar förväntat suffix enligt exemplen ovan. Använd JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals` / `assertTrue`), inte `kotlin.test`.

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*ManadsCsv*"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Committa bara uppgiftsflytten utan kod och test.
