# Uppgift: Säker loggrad för jobbfel

uppdrag: uppdrag/2026-10-21-styrelserummet-felhantering.md
försök: 2
beror-på: 2026-10-21-styrelserummet-felhantering-01-felmeddelande-pdf-text-krypterad-f2.md

## Vad som saknades
- PR #344 levererade **ingen implementation**: ingen funktion `loggJobbFel` i `FelHantering.kt` och inga nya testmetoder för loggraden.
- Enda kodrelaterade ändringen var att uppgiftsfilen flyttades till `uppgifter/pagar/` — uppgiften kräver Kotlin-kod och tester enligt avsnitten nedan.

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/FelHantering.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/FelHanteringTest.kt

## Läs som förebild (ändra inte)
- Befintlig `felTillMeddelande` i samma fil — loggraden får **inte** återanvända eller innehålla den texten.
- `styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/IndataTest.kt` (JUnit 4: `org.junit.Test`, `org.junit.Assert`)

## Uppgift
Lägg till i `FelHantering.kt`:

fun loggJobbFel(jobbId: String, fel: ServerFel, tidMs: Long): String

Returnera **en rad** (en `String`) som minst innehåller:
- `jobbId` (oförändrat),
- feltyp som kort kod — t.ex. sealed-klassens enkla namn (`IngenLasbarText`, `KrypteradPdf`),
- tid som antingen epoch millisekunder (`tidMs`) **eller** ISO-8601 UTC (t.ex. `java.time.Instant.ofEpochMilli(tidMs)`).

Radens får **inte** innehålla: filnamn (t.ex. `.pdf`), PDF-text, belopp, personnummer, stacktrace, eller användarmeddelandet från `felTillMeddelande(fel)`.

Lägg **nya** testmetoder i `FelHanteringTest.kt` (behåll befintliga tester). Använd JUnit 4 — **inte** `kotlin.test` eller JUnit Jupiter.

## Exempel
- `loggJobbFel("abc123", ServerFel.KrypteradPdf, 1_700_000_000_000L)` → strängen innehåller `abc123`, `KrypteradPdf` (eller liknande kod), och tid (`1700000000000` eller ISO med `1970`/`2023` beroende på format) — **inte** texten «lösenordsskyddad» från användarmeddelandet.
- `loggJobbFel("jobb-1", ServerFel.IngenLasbarText, 0L)` → innehåller `jobb-1` och `IngenLasbarText`; **inte** «läsbar text» / «textbaserad» från `felTillMeddelande`; **inte** något filnamn som `rapport.pdf`.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*FelHanteringTest"

Testerna ska asserta mot exemplen ovan (innehåller id och feltyp; `assertFalse` på `felTillMeddelande(fel)` och på `.pdf` i raden).

## Gör inte
- Ändra andra filer (inga `uppgifter/`, `JobbKo`, workflow-filer).
- Koppla in faktisk loggning i `JobbKo` (kommer i senare uppgift).
- Lägga till beroenden.
