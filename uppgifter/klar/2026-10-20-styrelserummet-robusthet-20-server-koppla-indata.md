# Uppgift: Koppla in indatavalidering i API:t

uppdrag: uppdrag/2026-10-20-styrelserummet-robusthet-handlingar.md
försök: 1
beror-på: 2026-10-20-styrelserummet-robusthet-19-server-indata-hjalpare-f2.md

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Application.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/ApiTest.kt

## Uppgift
Använd `sakertFilnamn`, `arPdf`, `valideraForeningsnamn` och `valideraArsslut` i `POST /api/jobb` i stället för ad hoc-kontroller:
- validera förening och räkenskapsår innan jobbet slutförs,
- spara filer med säkert filnamn,
- avvisa uppladdning som inte är PDF enligt `arPdf` (inte bara filändelse).

Lägg till minst ett test i `ApiTest` som visar att ogiltigt föreningsnamn eller icke-PDF ger `400` med begripligt fel.

## Exempel
- Multipart med `forening = ""` → `400`, jobb raderas som idag.
- Fil med ändelse `.pdf` men bytes som inte börjar med `%PDF-` → `400`.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*ApiTest*"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.

utfall: underkänd 2026-10-20 av Cursor (PR #317). Ersatt av 2026-10-20-styrelserummet-robusthet-20-server-koppla-indata-f2.md
