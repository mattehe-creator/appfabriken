# Status

Uppdaterad 2026-10-09.

## Klart: Grundplattan (2026-10-08–09)

Flödet går hela vägen utan att Mattias rör koden: Claude skriver uppdrag, Cursor delar upp och gör det tunga, den lokala modellen (Aider + Ollama `qwen3-coder:30b`) skriver uppgifterna, Cursor granskar, workflowet `cursor-beslut` mergar, Claude granskar i efterhand. Se ARBETSFLODE.md.

På Mattias dator: körskriptet i Schemaläggaren (när datorn är oanvänd 10 min, och kl. 01), startfilen "Starta lokal modell" och panelen "Appfabriken panel" på skrivbordet.

## Nu: Garantivalvet 1.0

Omfång i IDEURVAL.md.

- Klart: köp i databasen, lista sorterad efter garantislut, formulär med validering, detaljvy med ändra och ta bort, formatering av pris och datum (uppdrag `2026-10-08-kop-lista-formular`).
- Pågår: foto av kvittot (uppdrag `2026-10-09-kvittofoto`). Cursors del (migration 1→2, KvittoLager, FileProvider) mergad i PR #12. Uppgift 01 och 07 klara, 02–06 i kön.
- Kvar i 1.0, i ordning:
  1. Påminnelser: dagligt jobb (WorkManager) och notis 30 dagar före garantislut. Kräver notisbehörighet på Android 13+ (`POST_NOTIFICATIONS`), som begärs först när användaren slår på påminnelser.
  2. Reklamationsrätt enligt konsumentköplagen (2022:260). Fristerna kontrolleras mot lagtexten på riksdagen.se innan uppdraget skrivs.
  3. Gratisgräns (15 köp) och Pro via Google Play Billing, i `:karna`.
  4. Export: reklamationsunderlag som PDF och säkerhetskopia till fil (Pro).
  5. Butiksmaterial i `garantivalvet/butik/`: texter, skärmbilder, Data safety-svar.
- Öppet beslut: `android:allowBackup` (Androids automatiska molnkopia) krockar med CLAUDE.md punkt 6. Beslutas när exporten görs.

## Senare

- App 2: Husets underhållslogg. App 3: Mätarställning (IDEURVAL.md).
- Uppdrag i två steg (ARBETSFLODE.md) från och med påminnelser.
