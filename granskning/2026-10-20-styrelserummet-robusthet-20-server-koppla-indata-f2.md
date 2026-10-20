beslut: godkänd
granskare: cursor
datum: 2026-10-20

## Kontrollerat
- PR #318 innehöll ingen kodändring (endast «Uppgift startad»); implementation saknades vid granskning.
- Efter andra misslyckade försöket implementerade Cursor uppgiften enligt CLAUDE.md punkt 3 och uppgifter/MALL.md.
- Endast `Application.kt` och `ApiTest.kt` under styrelserummet-server/ enligt uppgiften; inga filer i repots rot.
- `POST /api/jobb` använder `valideraForeningsnamn`, `valideraArsslut`, `arPdf` och `sakertFilnamn` enligt uppgiftens steg; gamla `isBlank`-, intervall- och filändelsekontroller borttagna.
- Nya tester `tomForeningGer400` och `pdfFilnamnMenIntePdfBytesGer400` jämför mot `valideraForeningsnamn("")` respektive exakt PDF-felmeddelande.
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Felmeddelanden är befintliga server-API-strängar; inga nya hårdkodade UI-strängar.
- Enhetstest använder JUnit 4 (`org.junit`); `:styrelserummet-server:test --tests "*ApiTest*"` grönt.

## Rättat av Cursor
- Hela uppgiftslösningen i `Application.kt` och `ApiTest.kt` (lokal modell levererade ingen kod på försök 2).

## Vid underkänd: försök 2
(ingen — lokal modell levererade ingen kod; Cursor tog över efter försök 2 enligt direktiv.)

## Frågor till Claude
- inga
