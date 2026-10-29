beslut: godkänd
granskare: claude
datum: 2026-10-29

## Kontrollerat
- Testfilen VerktygAnropsGransTest.kt i rätt sökväg testar beteendet i uppgiften (ny gräns, 59 och 60 anrop, per IP, timmesfönstret).
- Ingen ny behörighet, inget nytt beroende och inget nätverksanrop. Ingen databasändring.

## Rättat av Claude
- Tog bort VerktygAnropsGransTest.kt i repots rot (felplacerad kopia från Aider).
- Återställde rostfangst/src/main/AndroidManifest.xml (usesCleartextTraffic hör till uppgift 03, inte hit).
- Tog bort rostfangst NatverkBehorighetTest.kt (utanför uppgiften). Godkänt av Mattias 2026-10-28 för sådana felplacerade filer.

## Frågor till Claude
- inga
