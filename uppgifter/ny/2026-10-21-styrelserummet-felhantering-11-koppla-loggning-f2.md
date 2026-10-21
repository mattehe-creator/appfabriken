# Uppgift: Logga jobbfel utan känslig data

uppdrag: uppdrag/2026-10-21-styrelserummet-felhantering.md
försök: 2
beror-på: 2026-10-21-styrelserummet-felhantering-10-koppla-jobbko-f2.md

## Vad som saknades
- PR #405 levererade **ingen implementation**: `JobbKo` loggar inte vid `FEL`, och `JobbKoTest` verifierar inte loggraden.
- Enda ändringen var flytt av uppgiftsfilen till `uppgifter/pagar/` — uppgiften kräver Kotlin-kod och tester enligt avsnitten nedan.

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/JobbKo.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/JobbKoTest.kt

## Läs som förebild (ändra inte)
- `styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/FelHantering.kt` — använd befintlig `fun loggJobbFel(jobbId: String, fel: ServerFel, tidMs: Long): String`
- Befintliga tester i `JobbKoTest.kt` (JUnit 4: `org.junit.Test`, `org.junit.Assert`)

## Uppgift
När `JobbKo` sätter status `FEL` (i `private fun fel(...)`), skriv **en** loggrad:

1. Bygg raden med `loggJobbFel(jobbId, serverFel, tidMs)` där `tidMs` är samma timestamp du redan sätter på meta (`System.currentTimeMillis()` vid fel).
2. Skicka raden till en injicerbar funktion `(String) -> Unit` på `JobbKo`-konstruktorn, standard `::println`, så produktion beter sig som idag.

Anrop **efter** att meta är sparad med `JobbStatus.FEL`, eller i samma `fel`-block — logga inte användarmeddelandet (`felTillMeddelande`) och logga inte stacktrace.

I `JobbKoTest`: spela in loggrad(er) via en lista/mutableList som skickas in som `loggSkriv` (eller liknande parameternamn). Lägg **minst ett** nytt test (eller utöka befintligt) som:

- Triggar `FEL` med PDF som innehåller känslig text (t.ex. personnummer `19800101-1234` i PDF-innehållet) och filnamn `underhallsplan.pdf` via befintlig `skapaJobbMedPdf`.
- Assertar att inspelad loggrad innehåller **jobb-id** och felkod (t.ex. `IngenLasbarText` eller `AiOgiltigtSvar` beroende på scenario).
- Assertar att loggraden **inte** innehåller `19800101-1234`, inte PDF-texten från dokumentet, och inte filnamnet `underhallsplan.pdf`.

## Exempel
- Misslyckad PDF-tolkning (tom/skannad PDF) → minst en inspelad loggrad med jobb-id och t.ex. `IngenLasbarText`.
- PDF med text `19800101-1234` men AI ogiltigt svar → loggrad med id och `AiOgiltigtSvar`; samma rad får **inte** innehålla `19800101-1234`.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*JobbKoTest"

## Gör inte
- Ändra andra filer (inkl. `FelHantering.kt`, `uppgifter/`, workflow).
- Logga användarmeddelandet eller stacktrace.
- Lägga till beroenden.
