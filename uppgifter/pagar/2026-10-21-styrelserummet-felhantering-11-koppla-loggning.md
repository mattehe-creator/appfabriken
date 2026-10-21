# Uppgift: Logga jobbfel utan känslig data

uppdrag: uppdrag/2026-10-21-styrelserummet-felhantering.md
försök: 1
beror-på: 2026-10-21-styrelserummet-felhantering-10-koppla-jobbko-f2.md

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/JobbKo.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/JobbKoTest.kt

## Uppgift
När `JobbKo` sätter status `FEL`, skriv en loggrad via `loggJobbFel(jobbId, fel, tidMs)` till en injicerbar funktion `(String) -> Unit` (standard `::println` så produktion beter sig som idag).

I `JobbKoTest`, spela in loggraden och verifiera att den innehåller jobb-id och feltyp men **inte** PDF-innehåll, personnummer eller filnamn från uppladdningen.

## Exempel
- Misslyckad PDF-tolkning → minst en loggrad med jobb-id och t.ex. `IngenLasbarText`.
- Samma loggrad får inte innehålla `"19800101-1234"` om sådant fanns i PDF:en.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*JobbKoTest"

## Gör inte
- Ändra andra filer.
- Logga användarmeddelandet eller stacktrace.
