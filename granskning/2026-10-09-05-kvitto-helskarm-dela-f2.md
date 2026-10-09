beslut: godkänd
granskare: cursor
datum: 2026-10-09

## Kontrollerat
- Försök 2 från lokal modell: otillåtna filer i repots rot (`KvittoHelskarm.kt`, `MainActivity.kt`), tom modulfil och ofylld helskärmsgren — uppfyllde inte uppgiften (CLAUDE.md punkt 8).
- Efter Cursors övertagande: endast `KvittoHelskarm.kt` och `MainActivity.kt` under `garantivalvet/` ändrade; ingen `KopSkarmar.kt`; inga `.kt` i rot.
- `KvittoHelskarm`: Scaffold + TopAppBar, stäng/dela via `kvitto_stang` / `kvitto_dela`, bitmap via ContentResolver, pinch + dubbeltryck 1f↔2f, dela med ACTION_SEND och chooser-titel `context.getString(R.string.kvitto_dela)`.
- `MainActivity`: helskärmsgren med `sparatKvittoUri` och `visaDetalj` vid tillbaka.
- Inga nya beroenden, behörigheter eller nätverk; inga nya strängnycklar.
- `:garantivalvet:testDebugUnitTest` grönt lokalt; appmodultester använder JUnit 4.

## Rättat av Cursor
- Hel implementation enligt uppgift (försök 2 underkänt enligt uppgifter/MALL.md — Cursor gör uppgiften).
- Tog bort rotfiler; fyllde `garantivalvet/.../ui/KvittoHelskarm.kt`; kopplade helskärm i modulens `MainActivity`.
- Stäng-knapp saknades i lokalt utkast; zoom via `graphicsLayer`; TextButton i appbar (ingen `material-icons-extended`).

## Frågor till Claude
- inga
