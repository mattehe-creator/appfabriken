# Uppgift: Koppla felhantering vid uppladdning

uppdrag: uppdrag/2026-10-21-styrelserummet-felhantering.md
försök: 1
beror-på: 2026-10-21-styrelserummet-felhantering-05-fel-http-status.md

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Application.kt
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/IpGrans.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/ApiTest.kt

## Läs som förebild (ändra inte)
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/FelHantering.kt
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/IpGrans.kt

## Uppgift
I `POST /api/jobb`, använd `felTillMeddelande` och `felTillHttpStatus` för:
- fil som inte är PDF → `ServerFel.IntePdf`, svar med rätt status och `{"fel": "<meddelande>"}`
- total storlek över gräns → `ServerFel.ForStorFil`, status 413
- för många jobb från samma IP → `ServerFel.ForMangaJobb(...)`, status 429; lägg till `fun nastaForsokEpochMs(ip: String, nuMs: Long): Long` i `IpGrans` som returnerar tidpunkt då äldsta jobbet inom timmen faller ut (nuMs + kvarvarande ms till en timme efter det jobbet).

Behåll befintlig validering av förening, år och antal filer; de kan fortfarande använda `FelBegaran` med 400 tills de flyttas till `ServerFel` i en senare uppgift.

Uppdatera/ lägg till tester i `ApiTest` som kontrollerar status **413** för för stor fil och **415** för icke-PDF (justera befintligt test om det förväntar 400).

## Exempel
- Uppladdning där `totalBytes > MAX_BYTES_PER_JOBB` → HTTP 413, JSON-fält `fel` matchar `felTillMeddelande(ForStorFil)`.
- `ipGransPerForwardedForNarProxyPa`-fallet → HTTP 429 med meddelande som nämner när man kan försöka igen.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*ApiTest*"

## Gör inte
- Ändra andra filer (utom om IpGrans **måste** få en rad — i så fall avbryt och låt Cursor ta båda filerna).
- Lägga till beroenden.
