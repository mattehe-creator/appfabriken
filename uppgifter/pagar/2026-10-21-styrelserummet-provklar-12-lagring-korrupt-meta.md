# Uppgift: Korrupt meta.json vid listning och rensning

uppdrag: uppdrag/2026-10-21-styrelserummet-provklar.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/JobbLagring.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/JobbLagringTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/JobbKo.kt (`rensaGamlaJobb`)

## Uppgift
Gör så att **ogiltig JSON**, **tom fil** och **truncerad/halv JSON** i `meta.json` inte kastar undantag vid `lasMeta`, `allaJobb` + rensning, eller `rensaGamlaJobb`.

- Jobbet ska behandlas som **skadat**: hoppas över vid rensning/listning av metadata.
- En **loggrad** ska skrivas (injectbar lambda i test, t.ex. `logg: (String) -> Unit`) som säger att meta är skadad — **utan jobb-id** i texten.
- `allaJobb()` får fortfarande returnera katalognamn om mappen finns; `lasMeta` returnerar `null` för skadad fil.

Tre testmetoder: `korruptMetaOgiltigJson`, `korruptMetaTomFil`, `korruptMetaHalvFil`.

Ändra `JobbLagring.kt` **bara** om test visar krasch idag.

## Exempel
- `meta.json` = `{` → `lasMeta` → `null`, rensning kastar inte, logg anropad en gång utan id i strängen.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*JobbLagringTest.korruptMeta*"

## Gör inte
- Ändra API-routes.
- Logga filinnehåll.
