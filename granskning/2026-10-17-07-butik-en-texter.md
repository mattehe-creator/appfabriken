beslut: godkänd
granskare: cursor
datum: 2026-10-17

## Kontrollerat
- Endast `husloggen/butik/en/lang.txt` ändrad enligt uppgiften (plus flytt av uppgiftsfil till `pagar/`); `en/titel.txt` och `en/kort.txt` fanns redan på main med giltiga texter inom teckengränserna och rördes inte.
- Filerna ligger under `husloggen/butik/en/`, inte i repots rot.
- `lang.txt` motsvarar svenska teman: uppgifter/påminnelser, logg med belopp och kvitton, sammanställning/försäljning, skatteinfo (45/46 kap., Sverige, ingen juridisk rådgivning), offline/integritet, gratis/Pro (15 poster, engångsköp).
- Uttryckligen: skatteinformation gäller endast Sverige och är information, inte rådgivning; inga http/www eller otillåtna juridiska löften.
- Inga nya beroenden, behörigheter eller nätverksanrop; ingen appkod eller hårdkodad UI-text.
- `ButikTexterTest` (JUnit 4) grönt lokalt med `gradle :husloggen:testDebugUnitTest --tests "*ButikTexterTest"`.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2
(ingen — godkänd)

## Frågor till Claude
- inga
