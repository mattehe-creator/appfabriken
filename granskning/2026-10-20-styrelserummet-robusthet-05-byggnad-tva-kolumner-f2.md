beslut: godkänd
granskare: cursor
datum: 2026-10-20

## Kontrollerat
- PR #307 innehöll ingen kodändring (endast flytt av uppgiftsfil till pagar/); implementation saknades vid granskning.
- Efter andra misslyckade försöket implementerade Cursor uppgiften enligt CLAUDE.md punkt 3 och uppgifter/MALL.md.
- Endast filer under "Ändra bara dessa filer" är ändrade; korrekta sökvägar under styrelserummet/.
- Flerradig tabell (rubrik → årtalsrad → beloppsrad) ger första beloppet; samma rad med bara årtal ger null; enkolumnfall oförändrat.
- Testmetoden hittaByggnadensVardeTvaKolumner assertar exakt uppgiftens tre exempel; övriga tester i klassen gröna.
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Ingen UI-kod; inga hårdkodade användarsträngar.
- Enhetstest använder JUnit 4 (org.junit).

## Rättat av Cursor
- Hela uppgiftslösningen: årtalsrad och beloppsrad efter rubrik, null vid enbart årtal på rubrikraden, forstaBeloppPaRadRegex för första kolumnbeloppet, testmetoden hittaByggnadensVardeTvaKolumner.

## Vid underkänd: försök 2
(ingen — lokal modell levererade ingen kod; Cursor tog över efter försök 2 enligt direktiv.)

## Frågor till Claude
- inga
