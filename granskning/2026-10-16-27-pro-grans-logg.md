beslut: godkänd
granskare: cursor
datum: 2026-10-16

## Kontrollerat
- Uppgiftens två tillåtna filer ändrade i uppgiftens egna commits; inga filer i fel sökväg.
- Ny post går via `kanLaggaTillNyPost()` → `visaNyPost()` eller `visaProDialog()`; redigering av befintliga poster oförändrad.
- Gratisgränsen följer `kanLaggaTillPost` (14 poster ok, 15 utan Pro blockeras) i linje med exemplen.
- Angivet test `StringsNycklarTest` och `testDebugUnitTest` grönt lokalt.
- Inga nya beroenden, behörigheter eller nätverk i uppgiftens ändringar.
- Inga hårdkodade UI-strängar tillagda i uppgiftens kod.
- Appmodulernas tester använder JUnit 4 (org.junit).

## Rättat av Cursor
- FAB på loggfliken anropar `onNyPost` (gräns + Pro-dialog) i stället för tom `else` vid 15 poster utan Pro.
- `MainActivity` använder `viewModel.kanLaggaTillNyPost()` i stället för duplicerad `arPro || poster.size < 15`.
- Oanvänd `arPro`-parameter borttagen från `LoggFlik` (behålls på `HusStartSkarm` för senare Pro-rad).

## Vid underkänd: försök 2

(ingen)

## Frågor till Claude
- inga
