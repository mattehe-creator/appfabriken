# Uppdrag: Husloggen, uppgifter (formulär, färdiga förslag, markera som gjord)

Läs CLAUDE.md, ARBETSFLODE.md, uppgifter/MALL.md och husloggen/APP.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Grunden är mergad (uppdraget 2026-10-16-husloggen-grund): modulen `:husloggen`, databas version 1 med `uppgift` och `post`, flikarna Att göra och Logg, och reglerna `nastaDatum`, `uppgiftStatus`, `sorteraUppgifter` och `grupperaPerAr`. Det här uppdraget gör fliken Att göra användbar: användaren kan skapa, ändra och ta bort uppgifter, välja bland färdiga förslag och markera en uppgift som gjord med ett tryck.

Garantivalvet är funktionsfryst. Inget i `garantivalvet/` får ändras.

## Beteende

- **Ny uppgift**: en knapp (flytande eller i raden överst, minst 48 dp) öppnar ett formulär med namn (obligatoriskt), intervall i månader (1–120, obligatoriskt), startdatum (standard idag, datumväljare), påminnelse på/av (standard på) och anteckning. Fel visas under fältet. Spara skapar uppgiften och går tillbaka.
- **Färdiga förslag**: överst i formuläret kan användaren välja ett förslag som fyller i namn och intervall, som sedan går att ändra. Förslagen ligger på ett ställe i `regler/` som en lista (namn-resurs, intervall i månader, text om vem som bestämmer). Förslag: rensa hängrännor (12), kontrollera brandvarnare (12), byt filter i ventilationen (6), rensa golvbrunn (12), kontrollera takets skick (12), sotning (enligt kommunen, 12), service av värmepump (enligt tillverkaren, 12), service av ventilation (enligt tillverkaren, 24). Där intervallet bestäms av kommunen eller tillverkaren visas "Enligt kommunen" respektive "Enligt tillverkaren" som hjälptext, aldrig ett lagkrav. Alla texter på svenska och engelska i `strings.xml`.
- **Ändra och ta bort**: tryck på en uppgift öppnar samma formulär ifyllt. Ta bort kräver bekräftelse. Poster som hört till uppgiften finns kvar (främmande nyckel sätts till null, finns redan).
- **Markera som gjord**: en knapp på uppgiftsraden (minst 48 dp, innehållsbeskrivning) sätter `senast_gjord` till idag och skapar en post med datum idag, `vad` = uppgiftens namn, typ `UNDERHALL`, `uppgift_id` = uppgiften och inget belopp. Båda sker i en och samma transaktion. Nästa datum flyttas därmed automatiskt. En snackbar bekräftar. Gratisgränsen på 15 poster kommer i ett senare uppdrag och ska inte byggas nu, men logiken för "markera som gjord" ligger i en funktion i repository så att gränsen kan läggas in på ett ställe.
- **Rader**: uppgiftsraden visar redan namn, nästa datum och status. Lägg till intervallet i hjälptexten, till exempel "Var 6:e månad" / "Every 6 months" (pluralresurs).

## Uppdraget görs i två PR:er (ARBETSFLODE.md, "Uppdrag i två steg")

**PR 1, bara uppgiftsfiler.** Dela upp del B i uppgifter i `uppgifter/ny/` enligt `uppgifter/MALL.md` och CLAUDE.md punkt 4. De som bygger på din kod får `beror-på: cursor:2026-10-16-husloggen-uppgifter`. Öppna PR 1 först.

**PR 2, del A.** Öppna den när PR 1 är öppnad. Frågor under "Frågor till Claude" i PR-beskrivningen.

## Del A (Cursor)

1. `HusRepository`: `sparaUppgift`, `uppdateraUppgift`, `taBortUppgift` och `markeraSomGjord(uppgift, idag)` som i en transaktion uppdaterar `senast_gjord` och infogar posten. DAO-metoder vid behov. Ingen schemaändring och ingen migrering.
2. `HusViewModel`: tillstånd och händelser för formuläret (inklusive validering som anropar den rena valideringsfunktionen från del B) och för markera som gjord.
3. Navigeringen till formuläret och själva formulärskärmen i Compose, med datumväljare. Använd samma mönster som formuläret i Garantivalvet (`garantivalvet/.../ui/`) som förebild, utan att kopiera in dess strängar eller ändra något där. Behöver du något gemensamt, lyft det till `:karna`.
4. Robolectric-test för `markeraSomGjord` (uppgiften och posten ändras tillsammans, en uppgift utan tidigare `senast_gjord` fungerar) och för att ta bort en uppgift (posten finns kvar med `uppgift_id = null`).

## Del B (uppgifter för den lokala modellen)

Förslag som du får justera. Varje uppgift högst 3 filer och med test, i `husloggen/.../regler/` om inget annat sägs.

- **Validering**: `valideraUppgift(namn: String, intervallManader: Int?): UppgiftFel?` ger `NAMN_SAKNAS` för tomt eller blankt namn, `INTERVALL_SAKNAS` för null, `INTERVALL_UTANFOR` för värden utanför 1–120, annars null. Namnet kontrolleras före intervallet. Exempel: ("", 12) → NAMN_SAKNAS; ("  ", null) → NAMN_SAKNAS; ("Hängrännor", null) → INTERVALL_SAKNAS; ("Hängrännor", 0) → INTERVALL_UTANFOR; ("Hängrännor", 121) → INTERVALL_UTANFOR; ("Hängrännor", 120) → null.
- **Förslagslistan**: `UPPGIFTSFORSLAG` med de åtta förslagen ovan (id, intervall, vem som bestämmer: `INGEN`, `KOMMUNEN` eller `TILLVERKAREN`). Test: åtta förslag, alla intervall 1–120, sotning har `KOMMUNEN`, värmepump och ventilationsservice har `TILLVERKAREN`, id:n är unika.
- **Strängar för förslagen och formuläret** (svenska och engelska) med test som `StringsNycklarTest`: att varje förslags namn-resurs finns på båda språken.
- **Intervalltexten**: `intervallEtikett(manader: Int)` returnerar ett par av typ och antal som UI:t mappar till en pluralresurs: 1 → (MANAD, 1), 12 → (AR, 1), 24 → (AR, 2), 18 → (MANAD, 18), 6 → (MANAD, 6). Hela år ger år, annars månader.
- **Formulärraderna** och förslagsväljaren som composables, enligt Del A:s tillstånd. Tryckytor minst 48 dp.

## Klart när

- `gradle testDebugUnitTest` och `gradle assembleDebug` är gröna för alla moduler, och `senaste-main` får en ny `husloggen-<sha>.apk`.
- Den sammanslagna manifestet för `:husloggen` saknar fortfarande `INTERNET`, och inga nya behörigheter eller beroenden har tillkommit.
- Databasschemat är oförändrat (schema 1).
- Man kan skapa en uppgift från ett förslag och från tomt formulär, ändra den, ta bort den och markera den som gjord, och posten syns i Logg.
- Uppgifterna för del B ligger i `uppgifter/ny/` via PR 1.
- Ingen fil i `garantivalvet/` är ändrad.

## Gör inte

- Ändra något i `garantivalvet/` eller databasschemat.
- Lägga till beroenden. Påminnelser (WorkManager) har ett eget uppdrag. Fältet `paminnelse` sparas men skickar inget än.
- Gratisgräns, Pro, kvittofoto, export eller loggformulär (senare uppdrag).
- Ändra `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md` eller `release.json`.
