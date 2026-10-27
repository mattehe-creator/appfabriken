# Säg det (Röstfångst)

Provapp. Publiceras inte i det här steget. Modul `:rostfangst`, paket `se.tmconnect.rostfangst`, appnamn **Säg det**.

## Syfte

Mattias säger vad han behöver i stunden och får det gjort: påminnelse, kalenderhändelse, att göra, anteckning eller fråga med svar. Måttet för provet är minst en fångst per dag i 30 dagar (visas i inställningar).

## Avsteg från CLAUDE.md

- **INTERNET** finns för tolkning via Anthropics Messages API. Ljud lämnar aldrig telefonen; bara transkriberad text skickas.
- API-nyckel och modell anges i inställningar och sparas i DataStore, aldrig i repot.
- Standardmodell: `claude-haiku-5-5`.
- **Home Assistant** (beslut 2026-10-25): adress, long-lived token och att-göra-lista anges i inställningarna och sparas i DataStore, aldrig i repot eller loggar. Utan adress och token görs inga anrop. Fångster skickas som händelsen `rostfangst_fangst`, ATT_GORA även till vald `todo`-lista, och typen HEM skickas till Assist (`conversation/process`) först efter tryck på Utför. HA får bara text, aldrig ljud.
- **`usesCleartextTraffic`** är på, så att HA på hemnätet nås över `http://`. Gäller bara provappen.
- Rekommendation: exponera aldrig lås, larm eller garageport för Assist i HA (Inställningar → Röstassistenter → Exponera).
- Token skapas i HA: Profil → Säkerhet → Long-lived access tokens.

## Tal och tolkning

- Tal till text: Android `SpeechRecognizer`, språk `sv-SE`. På enheten när `SpeechRecognizer.isOnDeviceRecognitionAvailable` (API 31+), annars molnigenkänning via samma API.
- `RECORD_AUDIO` frågas första gången inspelning behövs.
- Tolkning: gränssnitt `Tolkare` med implementation mot Anthropic. JSON-schema för handlingstyper: KALENDER, PAMINNELSE, ATT_GORA, ANTECKNING, FRAGA, HEM.

## Handlingar

- Kalender: `Intent.ACTION_INSERT` mot `CalendarContract.Events` (användaren bekräftar i kalenderappen).
- Påminnelse: avisering vid angiven tid via WorkManager (kräver notisbehörighet på Android 13+).
- Att göra och anteckning: appens egna listor.
- Fråga: svar visas i appen.

## Data

All fångst sparas lokalt innan tolkning. Utan nät sparas texten och kan tolkas om senare.
