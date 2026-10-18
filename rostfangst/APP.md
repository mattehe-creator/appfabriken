# Säg det (Röstfångst)

Provapp. Publiceras inte i det här steget. Modul `:rostfangst`, paket `se.tmconnect.rostfangst`, appnamn **Säg det**.

## Syfte

Mattias säger vad han behöver i stunden och får det gjort: påminnelse, kalenderhändelse, att göra, anteckning eller fråga med svar. Måttet för provet är minst en fångst per dag i 30 dagar (visas i inställningar).

## Avsteg från CLAUDE.md

- **INTERNET** finns för tolkning via Anthropics Messages API. Ljud lämnar aldrig telefonen; bara transkriberad text skickas.
- API-nyckel och modell anges i inställningar och sparas i DataStore, aldrig i repot.
- Standardmodell: `claude-haiku-5-5`.

## Tal och tolkning

- Tal till text: Android `SpeechRecognizer`, språk `sv-SE`. På enheten när `SpeechRecognizer.isOnDeviceRecognitionAvailable` (API 31+), annars molnigenkänning via samma API.
- `RECORD_AUDIO` frågas första gången inspelning behövs.
- Tolkning: gränssnitt `Tolkare` med implementation mot Anthropic. JSON-schema för handlingstyper: KALENDER, PAMINNELSE, ATT_GORA, ANTECKNING, FRAGA.

## Handlingar

- Kalender: `Intent.ACTION_INSERT` mot `CalendarContract.Events` (användaren bekräftar i kalenderappen).
- Påminnelse: avisering vid angiven tid via WorkManager (kräver notisbehörighet på Android 13+).
- Att göra och anteckning: appens egna listor.
- Fråga: svar visas i appen.

## Data

All fångst sparas lokalt innan tolkning. Utan nät sparas texten och kan tolkas om senare.
