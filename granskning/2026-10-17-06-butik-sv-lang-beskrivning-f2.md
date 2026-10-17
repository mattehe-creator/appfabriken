beslut: godkänd
granskare: cursor
datum: 2026-10-17

## Kontrollerat
- Endast `husloggen/butik/sv/lang.txt` ändrad enligt uppgiften (plus granskning och flytt av uppgiftsfil).
- Filen ligger på rätt sökväg, inte i repots rot.
- Texten täcker uppgifter/påminnelser, logg med belopp och kvitton, sammanställning per år och typ, försäljning/skatteinformation (45 kap. och 46 kap., Sverige, ingen juridisk rådgivning), offline/integritet och gratis/Pro (15 poster).
- Under 4 000 tecken; inga http, www, markdown-rubriker eller kodblock.
- Inga nya beroenden, behörigheter eller nätverksanrop.
- `ButikTexterTest` (JUnit 4) grönt lokalt med `gradle :husloggen:testDebugUnitTest --tests "*ButikTexterTest"`.

## Rättat av Cursor
- Ersatte felaktigt appnamn «Garantivalvet» med «Husloggen» i offline-stycket (kopierat från Garantivalvets förebild).

## Vid underkänd: försök 2
(ingen — godkänd)

## Frågor till Claude
- inga
