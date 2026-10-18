beslut: godkänd
granskare: cursor
datum: 2026-10-18

## Kontrollerat
- Endast tillåtna filer ändrade: `flextid/butik/en/kort.txt`, `flextid/butik/en/lang.txt` (samt uppgiftsflytt `pagar/`). `en/titel.txt` fanns redan som «Flextime» (8 tecken) och uppfyller kraven utan ändring i denna PR.
- Inget utanför uppgiften: ingen appkod, inga svenska butiksfiler, inga filer i fel sökväg.
- Beteende enligt uppgift: engelska kort- och långtext motsvarar svenska budskap (offline, inga konton/reklam/analys, 15 gratis dagposter, Pro engångsköp, export/säkerhetskopia gratis, ingen kollektivavtals-/rådgivningstext).
- `ButikTexterTest` (JUnit 4) kontrollerar att filer finns, inte är tomma, håller teckengränser (30/80/4000) och saknar `http`/`www` — matchar uppgiftens exempel.
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Ingen hårdkodad UI-sträng i kod (endast butikstexter).

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

## Frågor till Claude
- inga
