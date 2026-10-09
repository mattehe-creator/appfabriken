# Uppdrag: foto av kvittot (Garantivalvet)

Läs CLAUDE.md, ARBETSFLODE.md och uppgifter/MALL.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Köp kan läggas till, listas, ändras och tas bort. Nästa del av version 1.0 (IDEURVAL.md) är ett foto av kvittot till varje köp. Kvittot är det användaren behöver vid en reklamation, så det är appens viktigaste innehåll.

## Uppdraget har två delar

**Del A gör du själv** (tungt enligt CLAUDE.md punkt 2: databasmigration och filhantering).
**Del B delar du upp i uppgifter** för den lokala modellen, som filer i `uppgifter/ny/` enligt `uppgifter/MALL.md`. Allt i samma PR.

## Del A (Cursor)

1. **Migration 1 till 2:** kolumnen `kvitto_fil` (String, nullable) i tabellen `kop`. Riktig `Migration`, inget `fallbackToDestructiveMigration`. Exportera schemat för version 2. Test av migrationen med Room `MigrationTestHelper` eller motsvarande: en rad från version 1 finns kvar med `kvitto_fil = null`.
2. **Lagring:** `KvittoLager` i `se.tmconnect.garantivalvet.data` som kopierar en vald bild (content-URI) till appens interna lagring (`filesDir/kvitton/<uuid>.jpg`), skalar ned till högst 2000 px på längsta sidan med JPEG-kvalitet 85, och returnerar filnamnet. Tar bort filen när köpet tas bort eller kvittot byts. Inga behörigheter: fotoväljaren (`PickVisualMedia`) och kamera via `TakePicture` med en `FileProvider`-URI i appens egen cache.
3. **Koppling i ViewModel:** spara, byta och ta bort kvitto på ett köp.
4. Del B behöver anrop att bygga på. Lägg dem som funktioner med tydliga signaturer och KDoc.

## Del B (uppgifter för den lokala modellen)

Förslag, som du får justera:
- Knappar i formuläret: "Välj bild" och "Ta foto", och en liten förhandsvisning av valt kvitto. Strängar på svenska och engelska.
- Kvittot i detaljvyn: miniatyr som öppnar helskärm.
- Helskärmsvy med zoom (nyp och dubbeltryck) och knappen Dela (`ACTION_SEND` via `FileProvider`).
- Ikon i listraden när ett köp har kvitto, med innehållsbeskrivning.

Varje uppgift följer CLAUDE.md punkt 4, anger exakt vilka filer som får ändras och vilket test som ska gå igenom, och har `beror-på` där ordningen spelar roll. Ren logik (till exempel beräkning av nedskalad storlek) ska ha egna enhetstester.

## Klart när

- `gradle testDebugUnitTest` och `gradle assembleDebug` är gröna.
- Migrationstestet går igenom.
- Inga nya behörigheter i manifestet. `FileProvider` är den enda nya komponenten.
- Uppgifterna för del B ligger i `uppgifter/ny/`.

## Gör inte

- Inga nya beroenden utöver det som redan finns, utom `androidx.room:room-testing` som testberoende.
- Ingen OCR, ingen molnlagring, inget nätverk.
- Ändra inte `regler/`, `:karna`, `.github/workflows/`, `verktyg/`, `STATUS.md`, `BESLUTSLOGG.md` eller `IDEURVAL.md`.
- Pusha inte till main.

## Frågor

Är något oklart eller fel i uppdraget: gör det du kan och skriv frågan under "Frågor till Claude" i PR-beskrivningen.
