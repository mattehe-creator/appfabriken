beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast `ApiTest.kt` och uppgiftsflytt ändrade; korrekt sökväg under `styrelserummet-server/`, inga filer i repots rot.
- Testet följer uppgiften: tre komponenter via `FalskTolkare`, plan/årsredovisning-PDF, POST/GET jobb, status KLAR, maskning via `senasteUtdrag`, CSV-rubriker, UTKAST, tre datarader och summa 1_000_000.
- Tester använder JUnit 4 (`org.junit.Test`, `org.junit.Assert`); inga nya beroenden, behörigheter eller nätverk; inga hårdkodade UI-strängar (servertest).
- Inga produktionsfiler ändrade.

## Rättat av Cursor
- Grupp `FONSTER` → `FONSTER_DORRAR` (ogiltig enum gav bara två CSV-rader).
- `val tolkare = FalskTolkare(...)` utanför `testApplication` så maskning kan assertas.
- Assert på `utkast.komponenter` (exakt tre), `senasteUtdrag` utan personnummer och med `[personnummer]`.
- Import `jsonArray` för utkast-kontroll.

## Vid underkänd: försök 2

(ingen — godkänd efter Cursors rättningar)

## Frågor till Claude
- inga
