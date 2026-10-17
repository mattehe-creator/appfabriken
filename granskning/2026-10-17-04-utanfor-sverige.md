beslut: godkänd
granskare: cursor
datum: 2026-10-17

## Kontrollerat
- Endast filerna under «Ändra bara dessa filer» är ändrade (HusSkarmar.kt, SammanstallningSkarmTest.kt); uppgiftsflytt pagar ingår i PR:en som förväntat.
- `visaSkatteInfo` sätts i `HusStartSkarm` med `locale.country.equals("SE", ignoreCase = true)` och skickas till `SammanstallningSkarm`.
- Utanför SE: testet verifierar att bedömning, gränstexter och skatteinformationsrubrik saknas; exportknappar finns kvar.
- Med sv-SE: befintligt test verifierar förbättringsetiketter (Ja/Nej/bedömning) enligt uppgift 02.
- Inga nya beroenden, behörigheter eller nätverksanrop.
- UI-texter i test hämtas via `R.string`, inga hårdkodade strängar i produktionskod i diffen.
- Tester använder JUnit 4 (`org.junit`).

## Rättat av Cursor
- Tog bort oanvänd import `arSverigeForSkatteInfo` i HusSkarmar.kt efter byte till inline landskodkontroll.

## Vid underkänd: försök 2
(inget — godkänd)

## Frågor till Claude
- inga
