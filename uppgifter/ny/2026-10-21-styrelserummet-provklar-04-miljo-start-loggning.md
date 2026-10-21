# Uppgift: Miljökontroll vid serverstart

uppdrag: uppdrag/2026-10-21-styrelserummet-provklar-forsta-korning.md
försök: 1
beror-på: 2026-10-21-styrelserummet-provklar-03-miljo-port-proxy-modell.md

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Miljo.kt
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Application.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/MiljoTest.kt

## Läs som förebild (ändra inte)
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/JobbLagring.kt

## Uppgift
1. Lägg till `fun skrivMiljoRapporter(rader: List<String>, skriv: (String) -> Unit = System.err::println)` som skriver varje rad från `kontrolleraMiljo` via `skriv` (en rad per meddelande).
2. Anropa detta **innan** servern börjar lyssna: i `main()` efter `miljoFranEnv()` (eller motsvarande), bygg `env` från `System.getenv()` och använd `kanSkriva` som testar skrivbarhet med `Files.isWritable(Path.of(katalog))` när katalogen finns, annars `false`.
3. Servern ska **fortfarande starta** även vid fel/varningar (detta steg loggar bara).

Test: spion på `skriv`-lambda — inget API-nyckelvärde i utskriften när env innehåller `ANTHROPIC_API_KEY=sk-ant-hemlig`.

## Exempel
- `kontrolleraMiljo` returnerar två rader → `skriv` anropas exakt två gånger med samma text.
- Env med ogiltig nyckel `sk-fel` → minst en rad skrivs, ingen rad innehåller `sk-fel` som hel nyckel om ni testar med annat hemligt värde enligt regeln ovan.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*MiljoTest.skrivMiljoRapporter*"

## Gör inte
- Stoppa serverstart på miljöfel (kommer i senare drift om Claude beslutar annat).
- Skriva ut nycklar.
- Ändra fler än tre filer.
