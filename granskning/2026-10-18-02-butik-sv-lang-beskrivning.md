beslut: godkänd
granskare: cursor
datum: 2026-10-18

## Kontrollerat
- Endast `flextid/butik/sv/lang.txt` och uppgiftsflytt enligt workflow; inga filer i repots rot; titel, kort, engelska och appkod orörda.
- Lång beskrivning täcker stämpla/rast/saldo/veckovy, påminnelser 08:00 och efter normaltid, CSV/JSON-export gratis, offline utan INTERNET/konton/reklam/analys, 15 dagposter och Pro samt kollektivavtal/rådgivning enligt uppgiften.
- 1182 tecken (≤ 4 000); inga http/www; ingen juridisk rådgivning eller lofter om utfall.
- `ButikTexterTest` (JUnit 4, `org.junit`) grönt lokalt (`:flextid:testDebugUnitTest --tests "*ButikTexterTest"`); CI på grenen success.
- Inga nya beroenden, behörigheter eller nätverk; inga hårdkodade UI-strängar (endast butikstextfil).

## Rättat av Cursor
- Fullbordade avklippt `lang.txt` (Pro-gräns, kollektivavtal/rådgivning, avslutande radbrytning).

## Vid underkänd: försök 2
(inget — godkänd)

## Frågor till Claude
- inga
