# Uppdrag: påminnelser om garantislut (Garantivalvet)

Läs CLAUDE.md, ARBETSFLODE.md och uppgifter/MALL.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Kvittofoto är klart. Nästa del av version 1.0 (IDEURVAL.md, STATUS.md) är en påminnelse 30 dagar innan en garanti går ut: ett dagligt jobb i WorkManager som visar en notis. Reklamationsrätten kommer i ett senare uppdrag och ska kunna använda samma påminnelse, så logiken ska ta en frist (köp, typ, slutdatum) och inte bara garantin.

## Uppdraget görs i två PR:er (ARBETSFLODE.md, "Uppdrag i två steg")

**PR 1, bara uppgiftsfiler.** Dela upp del B i uppgifter i `uppgifter/ny/` enligt `uppgifter/MALL.md` och CLAUDE.md punkt 4. Varje uppgift som bygger på din kod i del A får raden `beror-på: cursor:2026-10-09-paminnelser`. Uppgifter som inte behöver din kod (till exempel strängarna) har inget sådant beroende, så att den lokala modellen kan börja direkt. Öppna PR 1 först.

**PR 2, del A.** Öppna den när PR 1 är öppnad.

## Del A (Cursor: nya beroenden, ny behörighet, bakgrundsjobb)

1. **Beroenden** (godkända av Claude): `androidx.work:work-runtime-ktx:2.10.1` och `androidx.datastore:datastore-preferences:1.1.1` i `:garantivalvet`. Testberoende `androidx.work:work-testing:2.10.1` om du behöver det.
2. **Behörighet:** `android.permission.POST_NOTIFICATIONS` i manifestet. Den begärs aldrig vid start, bara när användaren slår på påminnelser (del B).
3. **Inställning:** `InstallningarLager` i `se.tmconnect.garantivalvet.data` med DataStore: `paminnelserPa` (Boolean, standard false) och mängden redan aviserade frister.
4. **Ren logik med enhetstester** i `regler/Paminnelse.kt`: given en lista frister (köp-id, typ, slutdatum), dagens datum och redan aviserade nycklar, returnera de frister som ska aviseras nu. En frist aviseras när 0 ≤ dagar kvar ≤ 30 och den inte redan aviserats. Nyckeln innehåller köp-id, typ och slutdatum, så att ett ändrat datum ger en ny påminnelse. Utgångna frister aviseras inte. Exempel att testa: slut om 30 dagar → aviseras; om 31 → nej; om 0 → aviseras; i går → nej; redan aviserad → nej. En missad körning (telefonen avstängd) ska inte tappa påminnelsen, därför fönster och inte exakt dag.
5. **Jobbet:** `PaminnelseWorker` (CoroutineWorker) en gång per dygn, unikt periodiskt arbete med policy KEEP. Startas när `paminnelserPa` blir true och vid appstart om den är true, avbryts när den blir false. Visar en notis per frist (eller en samlad om fler än en), sparar nycklarna som aviserade. Notiskanal skapas med namn från `strings.xml`. Visar inget och kraschar inte om behörigheten saknas.
6. **Tryck på notisen** öppnar appen på köpets detaljvy (PendingIntent med köp-id till `MainActivity`, `FLAG_IMMUTABLE`).
7. **ViewModel:** `paminnelserPa: StateFlow<Boolean>` och `sattPaminnelser(pa: Boolean)` med KDoc, så att del B kan bygga på dem.

## Del B (uppgifter för den lokala modellen)

Förslag, som du får justera:
- Strängar på svenska och engelska: notisens titel och text (med köpets namn och slutdatum), kanalens namn, inställningens rubrik och förklaring, och texten som visas när behörigheten nekats. Test att nycklarna finns i båda språken (som `StringsNycklarTest`).
- Reglage för påminnelser i listans toppfält eller en enkel inställningsrad. På Android 13+ begärs `POST_NOTIFICATIONS` med `ActivityResultContracts.RequestPermission` när användaren slår på. Nekas den förblir reglaget av och förklaringen visas.
- Texterna i notisen byggs med strängarna ovan, om du lägger den delen som en egen uppgift.

## Klart när

- `gradle testDebugUnitTest` och `gradle assembleDebug` är gröna.
- Testerna i `regler/` täcker exemplen i punkt A4.
- Den enda nya behörigheten är `POST_NOTIFICATIONS`. Inga nya beroenden utöver punkt A1.
- Uppgifterna för del B ligger i `uppgifter/ny/` via PR 1.

## Gör inte

- Inget nätverk, inga exakta alarm (`SCHEDULE_EXACT_ALARM`), ingen förgrundstjänst.
- Ingen reklamationsrätt ännu, bara garantin. Logiken ska kunna ta fler fristtyper senare.
- Ändra inte databasschemat. Aviserade frister sparas i DataStore, inte i Room.
- Ändra inte `:karna`, `.github/workflows/`, `verktyg/`, `STATUS.md`, `BESLUTSLOGG.md` eller `IDEURVAL.md`.
- Pusha inte till main.

## Frågor

Är något oklart eller fel i uppdraget: gör det du kan och skriv frågan under "Frågor till Claude" i PR-beskrivningen.
