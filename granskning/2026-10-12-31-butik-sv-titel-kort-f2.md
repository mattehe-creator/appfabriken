beslut: godkänd
granskare: cursor
datum: 2026-10-12

## Kontrollerat
- Endast `garantivalvet/butik/sv/titel.txt` och `garantivalvet/butik/sv/kort.txt` ändrade (korrekt sökväg under modulen); inga filer i repots rot; `sv/lang.txt` och `en/` orörda.
- `titel.txt` är en trimmad rad (`Garantivalvet`, 13 tecken ≤ 30); `kort.txt` är ren butikstext (75 tecken ≤ 80), utan markdown, sökvägar eller kodblock; inga http/www.
- Innehåll stämmer med appen och nämner offline, 15 gratis köp och Pro (obegränsat, PDF, backup) i linje med uppgiften och `en/kort.txt`.
- `ButikTexterTest` (JUnit 4) passerar lokalt: `gradle :garantivalvet:testDebugUnitTest --tests "*ButikTexterTest"`.
- Inga nya beroenden, behörigheter eller nätverk; ingen appkod eller manifest ändrad.

## Rättat av Cursor
- Tog bort Aider-skrot (backticks, Windows-sökväg i `titel.txt`, extra backticks i `kort.txt`) och skrev korrekta svenska butikstexter enligt uppgiften.

## Frågor till Claude
- inga
