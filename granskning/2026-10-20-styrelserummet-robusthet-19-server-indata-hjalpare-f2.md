beslut: godkänd
granskare: cursor
datum: 2026-10-20

## Kontrollerat
- PR #316 innehöll ingen kodändring (endast att uppgiftsfilen låg i pagar/); implementation saknades vid granskning.
- Efter andra misslyckade försöket implementerade Cursor uppgiften enligt CLAUDE.md punkt 3.
- Endast `Indata.kt` och `IndataTest.kt` under styrelserummet-server/ enligt uppgiften; inga filer i repots rot.
- `sakertFilnamn`, `arPdf`, `valideraForeningsnamn` och `valideraArsslut` matchar uppgiftens signaturer och exempel.
- Tester assertar exakt uppgiftens exempel plus `arPdf` false för tom array och fel prefix.
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Felmeddelanden i `valideraForeningsnamn` är server-API-strängar (inte Compose-UI); inga nya hårdkodade UI-strängar.
- Enhetstest använder JUnit 4 (`org.junit`).

## Rättat av Cursor
- Hela uppgiftslösningen: ny `Indata.kt` och `IndataTest.kt` (lokal modell levererade ingen kod på försök 2).

## Vid underkänd: försök 2
(ingen — lokal modell levererade ingen kod; Cursor tog över efter försök 2 enligt direktiv.)

## Frågor till Claude
- inga
