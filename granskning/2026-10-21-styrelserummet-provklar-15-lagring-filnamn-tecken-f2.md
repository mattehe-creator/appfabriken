beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast `JobbLagring.kt` och `JobbLagringTest.kt` ändrade för denna uppgift (övriga filer i PR:en tillhör tidigare godkända uppgifter på samma gren).
- Beteende enligt uppgift: `sparaFil` sanerar till `[a-zA-Z0-9._-]`, trunkerar till 200 tecken efter sanering, bytes oförändrade.
- Test `sparaFilSakertNamnLangtOchUnicode` med namn ≥ 200 tecken och svenska tecken; regex, längd och `assertArrayEquals` mot bytes.
- Befintliga tester i `JobbLagringTest` (korrupt meta m.m.) kvar; inga borttagna.
- JUnit 4 (`org.junit`), inga nya beroenden, behörigheter eller nätverk.
- Inga hårdkodade UI-strängar (servermodul).

## Rättat av Cursor
- Tog bort oanvänd import `kotlin.io.path.readBytes`.
- Lade till assert att sparat filnamn är exakt 200 tecken när indata efter sanering överstiger gränsen (enligt exemplet i uppgiften).

## Vid underkänd: försök 2
(ingen)

## Frågor till Claude
- inga
