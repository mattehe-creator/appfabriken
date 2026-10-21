# Uppgift: Felmeddelande för jobbgräns och okänt fel

uppdrag: uppdrag/2026-10-21-styrelserummet-felhantering.md
försök: 1
beror-på: 2026-10-21-styrelserummet-felhantering-03-felmeddelande-ai-timeout-ogiltigt-f2.md

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/FelHantering.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/FelHanteringTest.kt

## Läs som förebild (ändra inte)
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/IpGrans.kt
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Granser.kt

## Uppgift
Utöka `ServerFel` med:
- `ForMangaJobb(val kanForsokaIgenEpochMs: Long)` — fler än `MAX_JOBB_PER_IP_TIMME` jobb från samma adress inom en timme
- `Okant` — övriga fel

Utöka `felTillMeddelande`. För `ForMangaJobb` ska meddelandet nämna att gränsen är fem jobb per timme och ungefär **när** användaren kan försöka igen, formaterat i svensk tid (t.ex. `14:35`) utifrån `kanForsokaIgenEpochMs`.

## Exempel
- `felTillMeddelande(ServerFel.ForMangaJobb(1_700_000_000_000L))` → text om fem jobb per timme och en klockslagsangivelse för nästa försök.
- `felTillMeddelande(ServerFel.Okant)` → generiskt svenskt meddelande utan tekniska detaljer.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*FelHanteringTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
