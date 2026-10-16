# Uppdrag: Husloggen, loggen (formulär för utfört arbete och utgifter, lista per år, detaljvy)

Läs CLAUDE.md, ARBETSFLODE.md, uppgifter/MALL.md och husloggen/APP.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Grunden och uppgifterna är mergade: modulen `:husloggen`, databas version 1 med `uppgift` och `post`, flikarna Att göra och Logg, formuläret för uppgifter och "markera som gjord" (som redan skapar poster av typ `UNDERHALL`). Det här uppdraget gör fliken Logg användbar på egen hand: användaren kan lägga till, se, ändra och ta bort poster för utfört arbete och utgifter.

Garantivalvet är funktionsfryst. Inget i `garantivalvet/` får ändras.

## Beteende

- **Ny post**: en knapp (minst 48 dp) öppnar ett formulär med vad som gjordes (obligatoriskt), datum (standard idag, datumväljare, inte i framtiden), typ (underhåll, reparation, ny-, till- eller ombyggnad; standard underhåll), belopp i kronor (valfritt, 0 till 99 999 999, lagras i öre som `Long`), vem som utförde arbetet (valfri fritext), koppling till en uppgift (valfri, väljs ur befintliga uppgifter) och anteckning. Fel visas under fältet. Spara skapar posten och går tillbaka.
- **Lista per år**: Logg visar poster grupperade per år med senaste året överst och senaste posten överst inom året (använd `grupperaPerAr` som redan finns). Varje rad visar vad, datum, typ och belopp (om det finns). Årsrubriken visar summan för året om minst en post har belopp.
- **Detaljvy**: tryck på en rad öppnar posten med alla fält och knapparna Ändra och Ta bort. Ta bort kräver bekräftelse. Ändra öppnar formuläret ifyllt.
- **Tom lista**: en tom logg visar en kort förklaring och knappen för ny post.
- Belopp formateras med enhetens språk via `:karna` (samma som Garantivalvet). Alla texter på svenska och engelska i `strings.xml`.
- Gratisgränsen på 15 poster byggs inte här. Spara via en funktion i `HusRepository` (som "markera som gjord" redan gör) så att gränsen kan läggas in på ett ställe senare.
- Kvittofoto och bilder byggs inte här. Fältet för bilder lämnas orört.

## Uppdraget görs i två PR:er (ARBETSFLODE.md, "Uppdrag i två steg")

**PR 1, bara uppgiftsfiler.** Dela upp del B i uppgifter i `uppgifter/ny/` enligt `uppgifter/MALL.md` och CLAUDE.md punkt 4. De som bygger på din kod får `beror-på: cursor:2026-10-16-husloggen-loggen`. Öppna PR 1 först.

**PR 2, del A.** Öppna den när PR 1 är öppnad. Frågor under "Frågor till Claude" i PR-beskrivningen.

## Del A (Cursor)

1. `HusRepository`: `sparaPost`, `uppdateraPost`, `taBortPost` och flöde för poster. DAO-metoder vid behov. Ingen schemaändring och ingen migrering.
2. `HusViewModel`: tillstånd och händelser för postformuläret och detaljvyn, med validering via den rena funktionen från del B.
3. Navigering, formulärskärm, listskärm och detaljvy i Compose med datumväljare. Använd uppgiftsformuläret i `husloggen/.../ui/` som förebild. Behöver du något gemensamt, lyft det till `:karna`.
4. Robolectric-test: spara, ändra och ta bort en post; en post kopplad till en uppgift behåller kopplingen; en post utan belopp sparas med null.

## Del B (uppgifter för den lokala modellen)

Förslag som du får justera. Varje uppgift högst 3 filer och med test, i `husloggen/.../regler/` om inget annat sägs.

- **Validering**: `validerapost(vad: String, datum: LocalDate, idag: LocalDate, beloppText: String): PostFel?` ger `VAD_SAKNAS` för tomt eller blankt, `DATUM_I_FRAMTIDEN` om datum är efter idag, `BELOPP_OGILTIGT` om beloppet inte går att tolka eller ligger utanför 0 till 99 999 999, annars null. Ordning: vad, datum, belopp. Tomt belopp är giltigt. Exempel: ("", idag, "") → VAD_SAKNAS; ("Målade fasaden", idag+1, "") → DATUM_I_FRAMTIDEN; ("Målade fasaden", idag, "abc") → BELOPP_OGILTIGT; ("Målade fasaden", idag, "-5") → BELOPP_OGILTIGT; ("Målade fasaden", idag, "") → null; ("Målade fasaden", idag, "12500") → null.
- **Beloppstolkning**: `tolkaBeloppTillOre(text: String): Long?` ger null för tomt eller ogiltigt, annars öre. Godta komma och punkt som decimaltecken och högst två decimaler. Exempel: "125" → 12500; "125,50" → 12550; "125.5" → 12550; "" → null; "12,345" → null.
- **Årssumma**: `summaPerAr(poster)` summerar belopp i öre per år och ignorerar poster utan belopp. Exempel: två poster 2025 med 10000 och null, en 2024 med 5000 → {2025: 10000, 2024: 5000}.
- **Strängar för loggen och postformuläret** (svenska och engelska) med test som `StringsNycklarTest`.
- **Formulärraderna** och typväljaren som composables, enligt Del A:s tillstånd. Tryckytor minst 48 dp.

## Klart när

- `gradle testDebugUnitTest` och `gradle assembleDebug` är gröna för alla moduler, och `senaste-main` får en ny `husloggen-<sha>.apk`.
- Den sammanslagna manifestet för `:husloggen` saknar fortfarande `INTERNET`, och inga nya behörigheter eller beroenden har tillkommit.
- Databasschemat är oförändrat (schema 1).
- Man kan skapa en post, se den i listan under rätt år, öppna den, ändra den och ta bort den.
- Uppgifterna för del B ligger i `uppgifter/ny/` via PR 1.
- Ingen fil i `garantivalvet/` är ändrad.

## Gör inte

- Ändra något i `garantivalvet/` eller databasschemat.
- Lägga till beroenden.
- Gratisgräns, Pro, kvittofoto, bilder, export, påminnelser eller sammanställning för skatt (senare uppdrag).
- Ändra `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md` eller `release.json`.
