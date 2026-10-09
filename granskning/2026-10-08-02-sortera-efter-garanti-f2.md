beslut: godkänd
granskare: cursor
datum: 2026-10-09

## Kontrollerat
- Endast de två angivna Kotlin-filerna plus uppgiftsflytt och denna beslutsfil ändras; inga filer i fel sökväg.
- `sorteraEfterGaranti` delar i aktiva/utgångna, sorterar aktiva stigande på garantislut och utgångna fallande, med `vad` som tie-break — enligt uppgiften och exemplen.
- `KopSorteringTest` har ett testfall per exempel (plus lika slut) och jämför ordning med `assertEquals`; JUnit 4 (`org.junit`).
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Ingen UI-kod; inga hårdkodade användarsträngar i produktionskod.
- Lokala modellens senaste commit (`d3b920c`) var tomma filer; CI grönt gav falskt lugn eftersom inga tester kördes för en tom testfil.

## Rättat av Cursor
- Implementerade `KopSortering.kt` och `KopSorteringTest.kt` i sin helhet (försök 2 underkänt enligt MALL.md — Cursor tar över).

## Frågor till Claude
- inga
