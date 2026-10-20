beslut: godkänd
granskare: cursor
datum: 2026-10-20

## Kontrollerat
- PR #298 innehöll ingen kodändring (endast flytt av uppgiftsfil till pagar/); implementation saknades helt vid granskning.
- Efter andra misslyckade försöket implementerade Cursor uppgiften enligt CLAUDE.md punkt 3.
- Endast filer under "Ändra bara dessa filer" är ändrade; korrekta sökvägar under styrelserummet/.
- parseBelopp känner igen minus och parentes; befintlig tusentalsavgränsare oförändrad.
- hittaByggnadensVarde matchar negativa belopp med tkr och utan enhet enligt uppgiftens exempel.
- Tester jämför mot uppgiftens exempel; parseBeloppAvgransare kvar och grönt.
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Ingen UI-kod; inga hårdkodade användarsträngar.
- Enhetstest använder JUnit 4 (org.junit).

## Rättat av Cursor
- Hela uppgiftslösningen: avmarkeraNegativt, utökad parseBelopp, regex för parentes/minus i hittaByggnadensVarde, testmetoden hittaByggnadensVardeNegativa.
- Regex-teckenklass: bindestreck sist i `[^\d\n(-]` (undviker PatternSyntaxException).

## Vid underkänd: försök 2
(ingen — lokal modell levererade ingen kod; Cursor tog över efter försök 2 enligt direktiv.)

## Frågor till Claude
- inga
