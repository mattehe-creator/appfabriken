beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast de två angivna filerna under rätt sökvägar ändrades (plus granskning och uppgiftsflytt).
- Ingen ändring utanför uppgiften.
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Inga hårdkodade UI-strängar (serverdomän, inget Compose).
- `utskriftsRader` sorterar på grupp.svNamn sedan namn, formaterar med `kronor`, summarad med Long-summor och `arSumma = true`.
- JUnit 4-test `utskriftsRader` jämför mot uppgiftens exempel och sortering för två komponenter.
- `:styrelserummet-server:test --tests "*UtskriftTest.utskriftsRader"` grönt.

## Rättat av Cursor
- Hela implementationen: försök 2 lämnade `Utskrift.kt` och `UtskriftTest.kt` tomma (samma som försök 1). Cursor implementerade enligt uppgiften.

## Frågor till Claude
- inga
