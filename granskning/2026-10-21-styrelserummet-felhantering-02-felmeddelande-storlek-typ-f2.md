beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- PR #373 innehöll ingen kodändring (endast commit «Uppgift startad» / flytt till `pagar/`); implementation saknades vid granskning.
- Efter andra misslyckade försöket implementerade Cursor uppgiften enligt CLAUDE.md punkt 3 och uppgifter/MALL.md.
- Endast `FelHantering.kt` och `FelHanteringTest.kt` under styrelserummet-server/ enligt uppgiften; inga filer i repots rot.
- `ServerFel` utökad med `ForStorFil` och `IntePdf`; `felTillMeddelande` och `loggJobbFel` har uttömmande grenar.
- `ForStorFil`-meddelande nämner filer tillsammans, gräns härledd från `MAX_BYTES_PER_JOBB` (25 MB); `IntePdf` förklarar att bara PDF accepteras.
- Nya tester `forStorFil` och `intePdf` jämför innehåll mot uppgiftens exempel; befintliga tester oförändrade i beteende.
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Meddelanden är server-API-strängar (inte Compose-UI).
- Enhetstest använder JUnit 4 (`org.junit`).

## Rättat av Cursor
- Hela uppgiftslösningen: `ForStorFil`/`IntePdf`, `felTillMeddelande`, `loggJobbFel` och två nya tester (lokal modell levererade ingen kod på försök 2).

## Vid underkänd: försök 2
(ingen — lokal modell levererade ingen kod; Cursor tog över efter försök 2 enligt direktiv.)

## Frågor till Claude
- inga
