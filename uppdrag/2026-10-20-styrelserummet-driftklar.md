# Uppdrag: Styrelserummet, servern driftklar

Läs CLAUDE.md, ARBETSFLODE.md och uppgifter/MALL.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Servern `:styrelserummet-server` är mergad (PR #287) och fungerar lokalt. Innan den läggs hos en värd måste några saker rättas som bara syns bakom en proxy eller i en webbläsare. Jobblänken är en hemlighet (jobbet nås bara via länken), så den får inte läcka.

## Beslut (Claude, 2026-10-20)

- Inga nya beroenden. Två oanvända beroenden tas bort: `ktor-server-cors` och `ktor-server-call-logging` (de installeras inte).
- Värdens proxy ger klientens adress i `X-Forwarded-For`. Läsning av den är avstängd som standard och slås på med miljövariabeln `STYRELSERUMMET_PROXY=1`. När den är på används första adressen i `X-Forwarded-For` (värden sätter den), annars anslutningens adress. Utan detta delar alla besökare på gränsen 5 jobb per timme.
- Länken får inte hamna i loggar, i `Referer` eller i sökmotorer.

## Del A (Cursor)

1. `X-Forwarded-For` enligt beslutet ovan, i `IpGrans`-anropet i `Application.kt`, och uppdatering av `styrelserummet/DRIFT.md` (miljövariabeln, och att den ska vara på hos värden).
2. Svarshuvuden på alla svar: `Referrer-Policy: no-referrer`, `X-Content-Type-Options: nosniff`, `Content-Security-Policy` som bara tillåter egna skript, stilar och bilder (`default-src 'self'`, `style-src 'self' 'unsafe-inline'` bara om det behövs), `Cache-Control: no-store` på `/api/` och jobbsidor, och `X-Robots-Tag: noindex` på jobbsidor och API:t.
3. Hälsokontroll `GET /halsa` som svarar `ok` utan att röra lagringen eller tolkaren (värdar vill ha den).
4. Kontrollera att inga loggrader skriver jobb-id eller länk (sökväg med länk, undantag, felsvar) och att fel till användaren inte innehåller sökvägar eller stackspår. Rätta det som läcker.
5. Ta bort de två oanvända beroendena.
6. Dela upp resten i uppgifter för den lokala modellen när det går (till exempel en ren funktion `klientAdress(forwardedFor: String?, anslutning: String, proxyPaa: Boolean): String` med test, och en ren funktion för svarshuvudena).

## Klart när

- Alla moduler är gröna i GitHub Actions.
- Test visar: med proxy på och `X-Forwarded-For: 203.0.113.5, 10.0.0.1` räknas gränsen på `203.0.113.5`; med proxy av används anslutningens adress och huvudet ignoreras.
- Test visar att svarshuvudena finns på ett jobbsvar och på startsidan, och att `/halsa` svarar utan nyckel och utan data.

## Gör inte

- Lägga till beroenden, konton, betalning, e-post eller analys.
- Ändra `:styrelserummet` (kärnan och underlaget), andra moduler, `webb/`, `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md`.
