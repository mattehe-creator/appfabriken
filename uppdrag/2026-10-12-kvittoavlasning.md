# Uppdrag: läs av kvittot och föreslå köpuppgifter

> **KLART 2026-10-12: gör ingenting.** Claude byggde hela funktionen själv i #117 (mergad, commit `ddce0ce`) på Mattias begäran. Koden finns redan på `main`: `regler/KvittoTolkare.kt`, `ocr/`, formuläret och `IngenNatverkTest`. Workflowet skickade ändå den här filen till Cursor vid mergen. Öppna ingen PR och skapa inga uppgifter. Behövs det en ändring blir det ett nytt uppdrag.

Läs CLAUDE.md, ARBETSFLODE.md och uppgifter/MALL.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Idag sparas kvittofotot bara som en bild; användaren skriver in butik, datum och pris själv. Appen ska läsa texten på kvittot på telefonen och föreslå värden i formuläret, så att det går snabbt att lägga till ett köp (IDEURVAL.md: "appen måste göra det snabbt, annars används den inte"). Avläsningen är gratis, inte Pro, eftersom den är det som gör gratisversionen användbar.

Allt sker på telefonen. Bilden och texten lämnar aldrig enheten (CLAUDE.md punkt 5 och 6).

## Genomförande

Undantag på Mattias begäran 2026-10-12: Claude kodar funktionen direkt i stället för att dela upp den (BESLUTSLOGG.md). Uppdraget gäller i övrigt som skrivet. Priset hanteras i ören (`prisOre`), butik går till fältet "Var köpt", och formulärlogiken ligger i den rena funktionen `fyllTommaFalt` så att den kan testas.

## Uppgift

**Del A (Cursor, en PR):** textigenkänningen.

- Lägg till ML Kit Text Recognition med den **medföljande** modellen (`com.google.mlkit:text-recognition`, latinska tecken), inte varianten som laddar ner modellen via Google Play-tjänster. Claude godkänner detta enda nya beroende.
- ML Kit kan dra in `INTERNET` och en telemetrikanal via sina transportberoenden. Det får inte hända (CLAUDE.md punkt 5). Kontrollera den sammanslagna manifestet (`processDebugMainManifest`/`processReleaseMainManifest`). Finns `INTERNET` eller annan ny behörighet, ta bort den med `tools:node="remove"` i appens manifest. Lägg ett enhetstest som läser det sammanslagna manifestet och faller om `INTERNET` finns.
- Går det inte att få bort nätverksbehörigheten och telemetrin utan att biblioteket slutar fungera: stanna, beskriv felet i PR-beskrivningen och föreslå Tesseract (`tesseract4android`, svensk och engelsk traineddata i assets) som alternativ. Välj inte alternativet själv.
- Nytt gränssnitt `KvittoLasare` med `suspend fun lasText(bild: Uri): String?` och en ML Kit-implementation i `garantivalvet/.../ocr/`. Returnerar `null` vid fel eller tom text; kastar aldrig. Appen får inte krascha utan avläsning.
- Kontrollera APK-storlek före/efter och skriv siffrorna i PR-beskrivningen.

**Del B (lokal modell, uppgifter som Cursor delar upp):**

1. Ren Kotlin, ingen Android: `KvittoTolkare.tolka(text: String, idag: LocalDate): KvittoForslag` där `KvittoForslag(butik: String?, datum: LocalDate?, prisKr: …?)`. Priset i samma enhet som formulärets prisfält (läs `KopFormularSkarm.kt` och `Kop.kt`). Regler:
   - **Datum:** `2026-08-01`, `01.08.2026`, `1/8/26`, `2026-08-01 14:32`. Datum i framtiden eller äldre än 10 år ignoreras.
   - **Pris:** beloppet på raden med `summa`, `totalt`, `att betala`, `total` eller `belopp` (skiftlägesokänsligt). Både `1 299,00` och `349.90 SEK` ska ge rätt värde. Finns inget sådant nyckelord ges inget pris. Gissa aldrig "största beloppet".
   - **Butik:** första icke-tomma raden med minst tre bokstäver som inte är ett datum, ett belopp eller ett nyckelord.
   - Hittas inget sätts fältet till `null`.
2. Formuläret: efter att kvittot fotats eller valts körs avläsningen i bakgrunden. Förslagen fyller **bara tomma fält**, skriver aldrig över det användaren redan skrivit, och fälten som fyllts i markeras med en liten text "Förslag från kvittot" så att användaren ser att de ska kontrolleras. Visa en diskret förloppsindikator under avläsningen. Om inget hittas sker inget, utan felmeddelande.
3. Strängar på svenska och engelska i `strings.xml`.

## Exempel (för uppgifterna)

- `"ELGIGANTEN\nOrg.nr 556...\n2026-08-01 14:32\nSumma att betala 1 299,00"` → butik `ELGIGANTEN`, datum 2026-08-01, pris 1299.
- `"Kjell & Company\n01.08.2026\nTotal 349.90 SEK"` → butik `Kjell & Company`, datum 2026-08-01, pris 349,90.
- `"Kvitto\nMoms 25 % 52,00\nDatum 3/9/27"` (idag 2026-10-12) → inget datum (framtid), inget pris, butik `null`.
- Formulär med redan ifyllt pris 500: avläsningen hittar 1299 → priset förblir 500.

## Klart när

- `gradle testDebugUnitTest` och `gradle assembleDebug` är gröna för alla moduler.
- `KvittoTolkare` har tester för alla exempel ovan och fler datum- och beloppsformat.
- Det sammanslagna manifestet saknar `INTERNET`; testet för det finns och är grönt.
- Cursor har provat avläsningen på minst två riktiga kvittobilder (påhittad data i repot, inga riktiga kvitton) och beskrivit utfallet i PR:en.
- `butik/data-safety.md` uppdaterad: avläsning sker på enheten, ingen data samlas in eller delas.

## Gör inte

- Lägga till `INTERNET` eller annan ny behörighet, annat nätverk, analys eller moln-OCR.
- Spara avläst text i databasen eller ändra databasschemat (förslagen används bara för att fylla formuläret).
- Låsa avläsningen bakom Pro.
- Ändra `:karna`.
- Checka in riktiga kvitton.
