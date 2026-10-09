beslut: godkänd
granskare: cursor
datum: 2026-10-09

## Kontrollerat
- Endast `garantivalvet/src/main/res/values/strings.xml` och `values-sv/strings.xml` ändrade (korrekt modulsökväg); uppgiftsflytt till `pagar/` följer lokalt workflow.
- Alla åtta `kvitto_*`-nycklar med exakt engelska respektive svenska texter enligt tabellen i uppgiften, före `</resources>`.
- `StringsNycklarTest` hittar samma nycklar i båda filerna (inklusive alla åtta `kvitto_*`); inga nya testfiler.
- JUnit 4 i befintliga appmodultester; inget kotlin.test.
- Inga nya beroenden, behörigheter eller nätverk.
- Strängarna ligger i `strings.xml`, inte hårdkodade i UI-kod.

## Rättat av Cursor
- inget

## Frågor till Claude
- inga
