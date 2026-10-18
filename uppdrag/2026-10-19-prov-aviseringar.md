# Uppdrag: Provapp som loggar aviseringar från Tempus och InfoMentor (Skolinkorgen, test)

Läs CLAUDE.md, ARBETSFLODE.md och uppgifter/MALL.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Idéurval 2 (IDEURVAL.md, 2026-10-18) har Skolinkorgen på kortlistan: en inkorg och kalender för förskola och skola byggd på aviseringarna på telefonen, tolkade lokalt. Innan något byggs ska vi veta om aviseringarna innehåller tillräckligt (datum, tid, plats, vad som ska med, vilket barn). Mattias familj använder **Tempus** och **InfoMentor**.

Det här är en provapp för Mattias egen telefon. Den publiceras aldrig. Den ska vara liten och klar på kort tid.

## Beslut (Claude, 2026-10-19)

- Ny modul `:prov-aviseringar`, `namespace` och `applicationId` `se.tmconnect.prov.aviseringar`, appnamn "Aviseringslogg". Samma versioner som `husloggen/build.gradle.kts`. Använder `:karna` för temat. Room för loggen. Inga andra beroenden.
- Regler i `prov-aviseringar/APP.md` (skriv den): provapp, publiceras inte, ingen `INTERNET`, data stannar på telefonen tills Mattias själv exporterar.
- `NotificationListenerService` med behörigheten `BIND_NOTIFICATION_LISTENER_SERVICE`. Appen visar en knapp till systemets inställning för aviseringsåtkomst och visar om åtkomsten är på.
- Paketnamnen för Tempus och InfoMentor är inte kända. Därför: tjänsten räknar alla aviseringar per paket (bara paketnamn, appnamn och antal, inget innehåll). Inställningssidan listar de appar som har skickat aviseringar och Mattias kryssar i vilka som ska loggas. Bara för ikryssade appar sparas innehållet.

## Del A (Cursor)

1. Modulen, manifestet, ikon (enkel, i temats färger), strängar på svenska och engelska, `APP.md`.
2. Tjänsten och databasen. Per avisering från en ikryssad app sparas: tid, paket, appnamn, kanal-id, `EXTRA_TITLE`, `EXTRA_TEXT`, `EXTRA_BIG_TEXT`, `EXTRA_SUB_TEXT`, `EXTRA_TEXT_LINES` (sammanslagna), `EXTRA_CONVERSATION_TITLE` och meddelandena i `EXTRA_MESSAGES` om de finns, samt aviseringens `key` och `postTime`. Samma `key` med samma innehåll sparas inte två gånger (uppdaterade aviseringar med nytt innehåll sparas).
3. Skärmar: startsidan visar status för åtkomsten, antal sparade aviseringar per app och de senaste 50 i en lista. Inställningar med appvalet. En knapp "Exportera" som skriver en JSON-fil (alla fält ovan) via systemets filväljare (`ACTION_CREATE_DOCUMENT`). En knapp "Rensa" med bekräftelse.
4. Robolectric-test för DAO:n och för dubblettspärren.

## Del B (uppgifter för den lokala modellen)

Förslag, justera fritt. Högst 3 filer och med test.

- **Hitta datum**: `hittaDatum(text: String, idag: LocalDate): List<LocalDate>` för svenska skrivsätt. Exempel med idag 2026-10-19: "imorgon" → 2026-10-20; "på fredag" → 2026-10-23; "fredag 24/10" → 2026-10-24; "24 oktober" → 2026-10-24; "2026-11-03" → 2026-11-03; "v. 45" → måndagen i vecka 45 (2026-11-02); text utan datum → tom lista.
- **Hitta tid**: `hittaTid(text: String): LocalTime?`. Exempel: "kl 8.15" → 08:15; "08:15" → 08:15; "klockan 14" → 14:00; "15-tiden" → 15:00; utan tid → null.
- **Klassning**: `klassa(text: String): Typ` med `FRANVARO`, `UTFLYKT`, `ATT_TA_MED`, `MOTE`, `INFO`. Nyckelord: frånvaro/sjuk/ledig → FRANVARO; utflykt/utedag/besök → UTFLYKT; ta med/packa/kläder/matsäck/ombyte → ATT_TA_MED; möte/utvecklingssamtal/föräldramöte → MOTE; annars INFO. Första träffen i den ordningen vinner.
- **Sammanställning**: en vy i appen som per app visar andel aviseringar där `hittaDatum` gav minst ett datum, andel med tid och fördelningen per `Typ`. Det är resultatet av provet.

## Klart när

- Bygget och testerna är gröna för alla moduler och `senaste-main` får en APK för `:prov-aviseringar`.
- Manifestet saknar `INTERNET`.
- Mattias kan ge åtkomst, kryssa i Tempus och InfoMentor, se aviseringar dyka upp och exportera JSON.

## Gör inte

- Ändra något i `garantivalvet/` eller `husloggen/`, eller i `:karna` utöver att använda temat.
- Läsa e-post, SMS eller andra appars data. Bara aviseringar.
- Spara innehåll från appar som inte är ikryssade.
- Ändra `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md`.
