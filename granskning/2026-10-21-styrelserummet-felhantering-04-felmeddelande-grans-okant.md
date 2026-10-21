beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast `FelHantering.kt` och `FelHanteringTest.kt` ändrade i uppgiftscommiten; inga filer i repots rot.
- `ServerFel` utökad med `ForMangaJobb(kanForsokaIgenEpochMs)` och `Okant`; `felTillMeddelande` och `loggJobbFel` har uttömmande grenar.
- `ForMangaJobb`-meddelande nämner fem jobb per timme och klockslag för nästa försök; `Okant` ger generiskt svenskt meddelande utan tekniska detaljer.
- Tester `forMangaJobbForklararFemJobbPerTimmeOchNastaForsok` och `okantForklararOkantFel` jämför innehåll mot uppgiftens exempel (inkl. epoch 1_700_000_000_000 → klockslag).
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Meddelanden är server-API-strängar (inte Compose-UI).
- Enhetstest använder JUnit 4 (`org.junit`).

## Rättat av Cursor
- `felTillMeddelande` för `ForMangaJobb` använder `Europe/Stockholm` i stället för `ZoneId.systemDefault()` (uppgiftens «svensk tid»); testet assertar `klockan 23:13` för exempelets epoch.

## Vid underkänd: försök 2
(ingen)

## Frågor till Claude
- inga
