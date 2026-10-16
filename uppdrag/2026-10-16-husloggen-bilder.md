# Uppdrag: Husloggen, kvittofoto och bilder på poster

Läs CLAUDE.md, ARBETSFLODE.md, uppgifter/MALL.md och husloggen/APP.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Loggen är mergad: poster kan skapas, listas per år, öppnas, ändras och tas bort. Nästa steg (STATUS.md, Nu, punkt 4) är bilder på posterna: kvitto, faktura eller foto av arbetet. Det är underlaget vid en försäljning, så bilderna är loggens viktigaste innehåll. Avläsning av text ur kvittot (OCR) ingår inte här; den tas i ett eget uppdrag om den behövs.

Garantivalvet är funktionsfryst. Inget i `garantivalvet/` får ändras. Den bildlagring som finns där kopieras till `:karna` som ett gemensamt mönster; Garantivalvet byter till den efter publicering (STATUS.md, Väntar, 1.1).

## Beteende

- **Flera bilder per post**: en post kan ha 0 till 10 bilder. Fältet för bilder finns redan i `post` som kommaseparerade filnamn (APP.md); ingen schemaändring.
- **Lägga till**: i postformuläret finns knapparna "Välj bild" och "Ta foto" (minst 48 dp). Fotoväljaren (`PickVisualMedia`) och kamera via `TakePicture` med `FileProvider`-URI i appens cache. Inga behörigheter.
- **Lagring**: bilden kopieras till `filesDir/bilder/<uuid>.jpg`, skalas ned till högst 2000 px på längsta sidan med JPEG-kvalitet 85. Filen tas bort när bilden tas bort från posten eller posten tas bort.
- **Formulär**: miniatyrer av valda bilder med en knapp för att ta bort var och en (innehållsbeskrivning på knappen). Vid 10 bilder är lägg-till-knapparna inaktiva med en kort förklaring.
- **Detaljvy**: miniatyrer som öppnar helskärm med zoom (nyp och dubbeltryck), svepning mellan bilderna och knappen Dela (`ACTION_SEND` via `FileProvider`).
- **Lista**: en liten ikon på raden när posten har minst en bild, med innehållsbeskrivning.
- Alla texter på svenska och engelska i `strings.xml`.
- Bilder räknas inte mot gratisgränsen; gränsen byggs i ett senare uppdrag.

## Uppdraget görs i två PR:er (ARBETSFLODE.md, "Uppdrag i två steg")

**PR 1, bara uppgiftsfiler.** Dela upp del B i uppgifter i `uppgifter/ny/` enligt `uppgifter/MALL.md` och CLAUDE.md punkt 4. De som bygger på din kod får `beror-på: cursor:2026-10-16-husloggen-bilder`. Öppna PR 1 först.

**PR 2, del A.** Öppna den när PR 1 är öppnad. Frågor under "Frågor till Claude" i PR-beskrivningen.

## Del A (Cursor)

1. `BildLager` i `:karna` (nytt, generellt: katalognamn som parameter) som kopierar en content-URI till appens interna lagring, skalar ned, returnerar filnamnet och tar bort filer. Rör inte Garantivalvets `KvittoLager`.
2. `FileProvider` i `:husloggen` (enda nya komponenten) för kamera och delning.
3. `HusRepository` och `HusViewModel`: lägga till, ta bort och läsa bilder på en post; borttagning av en post tar bort dess filer. Använd den rena listfunktionen från del B för filnamnslistan. Ingen schemaändring.
4. Robolectric-test: lägg till och ta bort bild på en post; posten tas bort och filerna försvinner; en post utan bilder har tom lista.

## Del B (uppgifter för den lokala modellen)

Förslag som du får justera. Varje uppgift högst 3 filer och med test.

- **Filnamnslista**: `tolkaBilder(text: String?): List<String>` och `formateraBilder(lista: List<String>): String?`. Null eller tom text ger tom lista, blanksteg trimmas, tomma element hoppas över; tom lista ger null. Exempel: "a.jpg,b.jpg" → [a.jpg, b.jpg]; " a.jpg , ,b.jpg" → [a.jpg, b.jpg]; null → []; [] → null; [a.jpg, b.jpg] → "a.jpg,b.jpg".
- **Gräns**: `kanLäggaTillBild(antal: Int): Boolean` med gränsen 10 på ett ställe i `regler/`. Exempel: 9 → true; 10 → false.
- **Nedskalad storlek**: ren funktion som ger ny bredd och höjd för längsta sida högst 2000 px med bibehållen proportion; mindre bilder ändras inte. Exempel: 4000x3000 → 2000x1500; 1000x800 → 1000x800.
- **Strängar** för bildfunktionerna (svenska och engelska) med test som `StringsNycklarTest`.
- **Composables**: bildrad med miniatyrer och borttagningsknappar i formuläret; helskärmsvy med svep; ikon i listraden. Tryckytor minst 48 dp, innehållsbeskrivning på ikoner.

## Klart när

- `gradle testDebugUnitTest` och `gradle assembleDebug` är gröna för alla moduler, och `senaste-main` får en ny `husloggen-<sha>.apk`.
- Det sammanslagna manifestet för `:husloggen` saknar fortfarande `INTERNET` och har inga nya behörigheter; `FileProvider` är den enda nya komponenten.
- Databasschemat är oförändrat (schema 1).
- Man kan lägga till bilder på en post, se dem i detaljvyn med zoom, dela en bild och ta bort den; filerna tas bort från lagringen.
- Uppgifterna för del B ligger i `uppgifter/ny/` via PR 1.
- Ingen fil i `garantivalvet/` är ändrad.

## Gör inte

- Ändra något i `garantivalvet/` eller databasschemat.
- Lägga till beroenden.
- OCR, gratisgräns, Pro, export, påminnelser, säkerhetskopia eller nätverk.
- Ändra `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md` eller `release.json`.
