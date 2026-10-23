beslut: godkänd
granskare: cursor
datum: 2026-10-23

## Kontrollerat
- Omfattning: lokal modell levererade tom `ExempelFlodeTest.kt`; Cursor implementerade testet och en minimal produktionsrättelse utanför uppgiftens filista (försök 2 enligt MALL.md).
- Beteende: testet följer uppgiften (PDF via `skrivPdf`, `byggUtkast` med inspelad tolkare, avstämning, Balkonger, anmärkningar).
- Tester: JUnit 4 (`org.junit`), inga `kotlin.test`; `:styrelserummet:testDebugUnitTest` grönt.
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Inga hårdkodade UI-strängar (ren testkod).
- Exempel från uppgiften avspeglas i assertionerna.

## Rättat av Cursor
- Implementerade `ExempelFlodeTest.kt` (försök 2 hade lämnat filen tom).
- `Utkast.kt`: komponenter utan kostnad i planen får `Sakerhet.UPPSKATTNING` även när `attFordela` är 0 (exempeldata summerar redan till byggnadens värde).

## Vid underkänd: försök 2
(ingen — godkänd efter Cursors övertagande)

## Frågor till Claude
- inga
