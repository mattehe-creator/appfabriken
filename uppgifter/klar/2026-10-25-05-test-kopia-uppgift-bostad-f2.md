# Uppgift: Test av säkerhetskopians omvandling för bostäder och uppgifter

## Vad som saknades
- Testfilen `SakerhetskopiaUppgiftBostadTest.kt` skapades inte. PR:en innehåller bara att uppgiftsfilen flyttades till `uppgifter/pagar/`.
- Uppgiften kräver en ny testklass med JUnit 4-tester mot alla exempel nedan; utan den filen är uppgiften ogjord.

uppdrag: uppdrag/claude/2026-10-25-natt-testtackning.md
försök: 2
beror-på: ingen

## Ändra bara dessa filer
- husloggen/src/test/kotlin/se/tmconnect/husloggen/export/SakerhetskopiaUppgiftBostadTest.kt (ny)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/export/SakerhetskopiaModell.kt (raderna med `bostadTillSakerhetskopia` till `sakerhetskopiaUppgiftTillUppgift`, `arSakerhetskopiaUppgiftLik` och `arSakerhetskopiaUppgiftDubblett`)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/data/Bostad.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/data/Uppgift.kt
- husloggen/src/test/kotlin/se/tmconnect/husloggen/export/SakerhetskopiaJsonTest.kt (JUnit 4-imports och paket)

## Uppgift
Skapa klassen `SakerhetskopiaUppgiftBostadTest` i paketet `se.tmconnect.husloggen.export` med JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals`, `assertTrue`, `assertFalse`, `assertNull` — **inte** `kotlin.test`).

Testa dessa funktioner i `SakerhetskopiaModell.kt`:
- `bostadTillSakerhetskopia(bostad: Bostad): SakerhetskopiaBostad`
- `sakerhetskopiaBostadTillBostad(export: SakerhetskopiaBostad): Bostad`
- `uppgiftTillSakerhetskopia(uppgift: Uppgift, bostadIdTillIndex: Map<Long, Int>): SakerhetskopiaUppgift`
- `sakerhetskopiaUppgiftTillUppgift(export: SakerhetskopiaUppgift, bostadId: Long): Uppgift`
- `arSakerhetskopiaUppgiftLik(a, b)` (jämför namn, intervallManader, startDatum, senastGjord och anteckning, men inte bostadIndex eller paminnelse)
- `arSakerhetskopiaUppgiftDubblett(export: SakerhetskopiaUppgift, befintlig: Uppgift)`

Importera `se.tmconnect.husloggen.data.Bostad`, `se.tmconnect.husloggen.data.Uppgift` och `java.time.LocalDate`.

`Uppgift` har fältet `skapad = System.currentTimeMillis()`, så jämför **aldrig** hela `Uppgift`-objekt med `assertEquals`; kontrollera fält för fält (särskilt i `sakerhetskopiaUppgiftTillUppgift`). `Bostad`, `SakerhetskopiaBostad` och `SakerhetskopiaUppgift` kan jämföras hela med `assertEquals`.

Lägg minst ett `@Test` per bullet i **Exempel** nedan (namn på svenska eller engelska spelar ingen roll).

## Exempel
- `bostadTillSakerhetskopia(Bostad(id = 4, namn = "Villan", adress = "Storgatan 1", anteckning = null))` → `SakerhetskopiaBostad(namn = "Villan", adress = "Storgatan 1", anteckning = null)`
- `sakerhetskopiaBostadTillBostad(SakerhetskopiaBostad(namn = " Villan ", adress = " Storgatan 1 ", anteckning = ""))` → `Bostad(id = 0, namn = "Villan", adress = "Storgatan 1", anteckning = null)`
- `uppgiftTillSakerhetskopia(Uppgift(id = 3, bostadId = 2, namn = "Rensa hängrännor", intervallManader = 12, startDatum = LocalDate.of(2026, 4, 1), senastGjord = LocalDate.of(2026, 4, 15), paminnelse = false, anteckning = "Stege i garaget"), mapOf(2L to 1))` → `SakerhetskopiaUppgift(namn = "Rensa hängrännor", intervallManader = 12, startDatum = LocalDate.of(2026, 4, 1), senastGjord = LocalDate.of(2026, 4, 15), anteckning = "Stege i garaget", bostadIndex = 1, paminnelse = false)`
- `sakerhetskopiaUppgiftTillUppgift(SakerhetskopiaUppgift(namn = " Byt filter ", intervallManader = 6, startDatum = LocalDate.of(2026, 1, 1), senastGjord = null, anteckning = "  ", bostadIndex = 0, paminnelse = true), bostadId = 4)` → `id == 0L`, `bostadId == 4L`, `namn == "Byt filter"`, `intervallManader == 6`, `startDatum == LocalDate.of(2026, 1, 1)`, `senastGjord == null`, `paminnelse == true`, `anteckning == null`
- Låt `bas = SakerhetskopiaUppgift(namn = "Byt filter", intervallManader = 6, startDatum = LocalDate.of(2026, 1, 1))`.
  - `arSakerhetskopiaUppgiftLik(bas, bas.copy(bostadIndex = 3, paminnelse = false))` → `true`
  - `arSakerhetskopiaUppgiftLik(bas, bas.copy(senastGjord = LocalDate.of(2026, 7, 1)))` → `false`
  - `arSakerhetskopiaUppgiftLik(bas, bas.copy(namn = "byt filter"))` → `false`
- `arSakerhetskopiaUppgiftDubblett(bas.copy(bostadIndex = 3, paminnelse = false), Uppgift(id = 1, bostadId = 9, namn = "Byt filter", intervallManader = 6, startDatum = LocalDate.of(2026, 1, 1), paminnelse = true))` → `true` (bostad och påminnelse spelar ingen roll)
- `arSakerhetskopiaUppgiftDubblett(bas, Uppgift(id = 1, bostadId = 9, namn = "Byt filter", intervallManader = 6, startDatum = LocalDate.of(2026, 1, 1), anteckning = "x"))` → `false`

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*SakerhetskopiaUppgiftBostadTest"

## Gör inte
- Ändra andra filer (inklusive `uppgifter/` — körskriptet hanterar det).
- Ändra `SakerhetskopiaModell.kt`. Om ett exempel inte stämmer med koden: skriv det i PR-beskrivningen och låt testet följa exemplet.
- Jämföra hela `Uppgift`-objekt (fältet `skapad` skiljer sig).
- Lämna testfilen tom eller ute.
- Lägga till beroenden.
- Använda `kotlin.test`.

utfall: godkänd 2026-10-25 (hela testfilen — lokal modell levererade ingen kod)
