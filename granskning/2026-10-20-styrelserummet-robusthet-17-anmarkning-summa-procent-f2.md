beslut: godkänd
granskare: cursor
datum: 2026-10-20

## Kontrollerat
- PR #315 innehöll ingen kodändring (endast flytt av uppgiftsfil till pagar/ och tom `AnmarkningarTest.kt`); implementation saknades helt vid granskning.
- Efter andra misslyckade försöket implementerade Cursor uppgiften enligt CLAUDE.md punkt 3 och uppgifter/MALL.md.
- Endast filer under "Ändra bara dessa filer" är ändrade; korrekta sökvägar under `styrelserummet/`.
- Befintlig differensanmärkning vid `!avstamning.stammer` oförändrad.
- Procentregler: strikt över 150 % respektive strikt under 50 % av byggnadens redovisade värde; inga procent-anmärkningar när `byggnadensVarde` är null; gränser 150 % och 50 % exkluderas.
- Tester jämför delsträngar och belopp mot uppgiftens exempel inklusive gränsfall.
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Anmärkningstexter i domänlogik (inte Compose-UI); svenska enligt befintlig stil i `Anmarkningar.kt`.
- Enhetstest använder JUnit 4 (`org.junit`).

## Rättat av Cursor
- Hela uppgiftslösningen: procentkontroller i `Anmarkningar.kt` och `AnmarkningarTest.kt` med metoden `anmarkningSummaProcent`.

## Vid underkänd: försök 2
(ingen — lokal modell levererade ingen kod; Cursor tog över efter försök 2 enligt direktiv.)

## Frågor till Claude
- inga
