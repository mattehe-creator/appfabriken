# Uppdrag: gratisgräns och Pro via Google Play Billing (Garantivalvet, `:karna`)

Läs CLAUDE.md, ARBETSFLODE.md och uppgifter/MALL.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Reklamationsrätten är klar. Nästa del av version 1.0 (STATUS.md, IDEURVAL.md) är affärsmodellen: gratisversionen är en hel app med högst 15 köp, och engångsköpet Pro låser upp obegränsat antal köp (export kommer i nästa uppdrag och ska kunna använda samma Pro-status). Pro-köpet ligger i `:karna`, eftersom alla appar i portföljen ska använda det (CLAUDE.md punkt 5). Ändringar i `:karna` och Billing gör du själv.

## Beslut som redan är fattade (BESLUTSLOGG.md 2026-10-11)

- Beroende: `com.android.billingclient:billing-ktx`, senaste stabila version (minst 8.0.0), bara i `:karna`. Ange exakt version i PR-beskrivningen.
- Behörighet: `com.android.vending.BILLING` (kommer från biblioteket) godkänns. Tar biblioteket med `INTERNET` eller `ACCESS_NETWORK_STATE` i den sammanslagna manifestet för `:garantivalvet`, ta bort dem med `tools:node="remove"` i appens manifest. Lista den sammanslagna manifestets behörigheter i PR-beskrivningen.
- Produkt-id: `pro`, typ engångsköp (INAPP). Id:t läses från appmodulen, inte hårdkodat i `:karna`.
- Gratisgränsen 15 ligger på ett enda ställe i `:garantivalvet` (`regler/`), inte i `:karna`.
- Inget databasschema ändras. Pro-status cachas i DataStore i `:karna` och kontrolleras mot Play vid start (`queryPurchasesAsync`). Ett köp kvitteras (`acknowledge`) och räknas som Pro bara när det är `PURCHASED`; `PENDING` ger inte Pro.
- Ingen prenumeration, ingen annan betalväg, ingen egen server.

## Uppdraget görs i två PR:er (ARBETSFLODE.md, "Uppdrag i två steg")

**PR 1, bara uppgiftsfiler.** Dela upp del B i uppgifter i `uppgifter/ny/` enligt `uppgifter/MALL.md` och CLAUDE.md punkt 4. Uppgifter som bygger på din kod i del A får `beror-på: cursor:2026-10-11-pro-gratisgrans`. Numrera från 18. Öppna PR 1 först.

**PR 2, del A.** Öppna den när PR 1 är öppnad. Skriv frågor under "Frågor till Claude" i PR-beskrivningen.

## Del A (Cursor)

1. **`:karna`, paket `se.tmconnect.karna.pro`:**
   - `interface ProKalla { val arPro: StateFlow<Boolean>; suspend fun kontrollera(); fun kop(activity: Activity) }`, och en testbar fake för enhetstester.
   - `PlayProKalla` som implementerar den med Billing (anslutning med återförsök, `queryPurchasesAsync` vid start, `PurchasesUpdatedListener`, kvittering). Fel och avbrutet köp ändrar inte statusen och kraschar inte appen.
   - Ren funktion för köplogiken som går att enhetstesta utan Play, t.ex. `arProEfter(kop: List<KopInfo>, produktId)`. Exempel: tom lista → false; `pro` PURCHASED → true; `pro` PENDING → false; annat produkt-id PURCHASED → false.
   - Cache i DataStore, så att Pro syns direkt vid start utan nätverk mot Play.
2. **`:garantivalvet`:** koppla in `PlayProKalla` i `MainActivity`/ViewModel-fabriken så att del B kan läsa `arPro` och anropa `kop`. Lägg `GRATIS_MAX_KOP = 15` och en ren funktion `kanLaggaTill(antal: Int, arPro: Boolean): Boolean` i `regler/`, med test: (14, false) → true, (15, false) → false, (15, true) → true, (500, true) → true.
3. Beroende och manifest enligt besluten ovan.

## Del B (uppgifter för den lokala modellen)

Förslag, som du får justera (varje uppgift högst 3 filer):
- **Strängar** (svenska och engelska): "Pro", "Lås upp Pro", förklaring ("Gratisversionen sparar upp till 15 köp. Pro är ett engångsköp utan prenumeration och låser upp obegränsat antal köp."), "Du har Pro", "Köpet kunde inte genomföras. Försök igen.", "Gränsen är nådd: 15 köp". Test som `StringsNycklarTest`.
- **Gräns i formuläret/listan:** när `kanLaggaTill` är false öppnar knappen för nytt köp en dialog om Pro i stället för formuläret. Befintliga köp kan alltid visas, ändras och tas bort, även över gränsen (till exempel efter återställning).
- **Pro-dialog:** förklaringen, knappen "Lås upp Pro" som anropar `kop`, och "Inte nu". Tryckytor minst 48 dp.
- **Pro-rad i listan eller menyn:** visar "Du har Pro" eller "Lås upp Pro".

## Klart när

- `gradle testDebugUnitTest` och `gradle assembleDebug` är gröna för alla moduler.
- Testerna täcker exemplen ovan.
- Den sammanslagna manifestet för `:garantivalvet` har inte `INTERNET`.
- Uppgifterna för del B ligger i `uppgifter/ny/` via PR 1.

## Gör inte

- Ändra databasschemat.
- Lägga till andra beroenden än `billing-ktx`, eller analys-, annons- eller Firebase-SDK:er.
- Låsa befintliga köp, påminnelser eller reklamationsrätt bakom Pro.
- Lägga in pris i appen (priset visas av Play).
- Ändra `.github/workflows/`, `verktyg/`, `STATUS.md`, `BESLUTSLOGG.md` eller `IDEURVAL.md`.
