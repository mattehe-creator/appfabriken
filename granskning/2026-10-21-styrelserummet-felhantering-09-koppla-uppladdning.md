beslut: underkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Diff mot main: endast flytt av uppgiftsfil till `uppgifter/pagar/` (0 rader kod). Inga ändringar i `Application.kt`, `IpGrans.kt` eller `ApiTest.kt` trots att PR-beskrivningen listar dem som saknade.
- Uppgiftens beteende (413/415/429 via `felTillMeddelande` och `felTillHttpStatus`, `nastaForsokEpochMs` i `IpGrans`) är oimplementerat; `POST /api/jobb` använder fortfarande `FelBegaran` och hårdkodat 429-meddelande.
- Befintligt test `pdfFilnamnMenIntePdfBytesGer400` förväntar fortfarande 400 och gammalt meddelande — matchar inte uppgiften.
- Inga nya beroenden, behörigheter eller nätverk i diffen (ingen produktionskod ändrad).
- `FRAGA.md` fanns inte i repots rot.
- Bygget kan vara grönt eftersom inget i servermodulen ändrats; uppgiftens testkommando skulle faila tills implementationen finns.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

```
# Uppgift: Koppla felhantering vid uppladdning

uppdrag: uppdrag/2026-10-21-styrelserummet-felhantering.md
försök: 2
beror-på: 2026-10-21-styrelserummet-felhantering-05-fel-http-status.md

## Vad som saknades
- Grenen innehåller bara «Uppgift startad» (flytt till `uppgifter/pagar/`). Ingen kod i de tre tillåtna filerna.
- `Application.kt`, `IpGrans.kt` och `ApiTest.kt` ändrades inte; uppgiftens testkommando (`*ApiTest*`) löser inte uppgiften.

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Application.kt
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/IpGrans.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/ApiTest.kt

## Läs som förebild (ändra inte)
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/FelHantering.kt
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/IpGrans.kt (befintlig logik för `farSkapaJobb` / `registreraJobb`)

## Uppgift
Koppla `POST /api/jobb` till `felTillMeddelande` och `felTillHttpStatus` för uppladdningsfel. Behåll `FelBegaran` + 400 för validering av förening, år och antal filer (enligt försök 1).

1. **`IpGrans.kt`** — lägg till:
   ```kotlin
   fun nastaForsokEpochMs(ip: String, nuMs: Long): Long
   ```
   Läs samma tidsstämplar som `farSkapaJobb`. Bland de jobb som räknas inom senaste timmen (`nuMs - it < timmeMs`), ta **äldsta** tidsstämpeln `t`. Returnera `t + timmeMs` (epoch ms när det jobbet faller ut ur fönstret). Om listan är tom, returnera `nuMs`.

2. **`Application.kt`** — i `post` för `/api/jobb`:
   - **429:** När `!ipGrans.farSkapaJobb(ip)` — skapa `ServerFel.ForMangaJobb(ipGrans.nastaForsokEpochMs(ip, nuMs))`, svara med `HttpStatusCode.fromValue(felTillHttpStatus(fel))` och JSON `{"fel": felTillMeddelande(fel)}`. Ta bort det hårdkodade meddelandet om «Vänta en timme».
   - **413:** När `totalBytes > MAX_BYTES_PER_JOBB` (efter `lagring.raderaJobb(id)` som idag) — använd `ServerFel.ForStorFil` i stället för `FelBegaran`.
   - **415:** När `!arPdf(bytes)` — använd `ServerFel.IntePdf` i stället för `FelBegaran`.

   För 413/415 i `forEachPart` kan du antingen:
   - lägga en **lokal** undantagsklass i samma fil (t.ex. `private class ServerFelBegaran(val fel: ServerFel) : Exception()`) och ett `StatusPages`-block som svarar med `felTillHttpStatus` / `felTillMeddelande`, och `throw ServerFelBegaran(fel)`; eller
   - refaktorera så att du kan `respond` och avbryta utan att bryta multipart-loopen.

   Importera/inte duplicera meddelanden — all användartext ska komma från `felTillMeddelande`.

3. **`ApiTest.kt`** (JUnit 4, `org.junit` — **inte** `kotlin.test`):
   - Byt namn/justera `pdfFilnamnMenIntePdfBytesGer400`: förvänta **415** och `fel` == `felTillMeddelande(ServerFel.IntePdf)`.
   - Lägg till test för **413**: ladda upp en fil som börjar med `%PDF-` men har storlek `MAX_BYTES_PER_JOBB + 1` (t.ex. prefix + noll-padding); assert status 413 och `fel` == `felTillMeddelande(ServerFel.ForStorFil)`.
   - Utöka `ipGransPerForwardedForNarProxyPa`: efter 429, parse JSON `fel` och assert att texten matchar `felTillMeddelande(ServerFel.ForMangaJobb(...))` (meddelande ska nämna när man kan försöka igen — «Försök igen klockan»).

## Exempel
- Uppladdning där `totalBytes > MAX_BYTES_PER_JOBB` → HTTP 413, JSON-fält `fel` matchar `felTillMeddelande(ForStorFil)`.
- Icke-PDF-bytes → HTTP 415, `fel` matchar `felTillMeddelande(IntePdf)`.
- `ipGransPerForwardedForNarProxyPa`-fallet → HTTP 429 med meddelande som nämner när man kan försöka igen.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*ApiTest*"

## Gör inte
- Ändra andra filer (utom om IpGrans **måste** få mer än en funktion — håll dig inom listan ovan).
- Lägga till beroenden.
```

## Frågor till Claude
- inga
