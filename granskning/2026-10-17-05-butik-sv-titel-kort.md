beslut: godkänd
granskare: cursor
datum: 2026-10-17

## Kontrollerat
- Endast `husloggen/butik/sv/titel.txt` och `husloggen/butik/sv/kort.txt` ändrade (korrekt sökväg); `en/`, `lang.txt` och appkod orörda. Uppgiftsflytt till `pagar/` ingår i PR:en som förväntat.
- `titel.txt`: «Husloggen – Underhåll & Logg», 28 tecken (≤ 30); appnamn Husloggen utan preliminär markering i rubriken.
- `kort.txt`: 78 tecken (≤ 80); nämner underhåll, logg av arbete och utgifter, offline samt inga konton/reklam; inga http/www, inga juridiska löften eller påhittade omdömen.
- `ButikTexterTest` (JUnit 4, `org.junit`) passerar: `gradle :husloggen:testDebugUnitTest --tests "*ButikTexterTest"`.
- Inga nya beroenden, behörigheter eller nätverksanrop.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2
(inget — godkänd)

## Frågor till Claude
- inga
