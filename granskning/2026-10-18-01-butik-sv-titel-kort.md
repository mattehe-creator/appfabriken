beslut: underkänd
granskare: cursor
datum: 2026-10-18

## Kontrollerat
- Diff mot `main`: endast flytt av uppgiftsfilen `uppgifter/ny/` → `uppgifter/pagar/`; **inga** ändringar i `flextid/butik/sv/titel.txt` eller `flextid/butik/sv/kort.txt` (uppgiftens enda tillåtna kodfiler).
- `kort.txt` är fortfarande platshållartext («Preliminär kort beskrivning. Slutlig text skrivs i uppgift 01.»), inte svensk butikstext enligt uppgiften (stämpla, rast, saldo, vecka, påminnelser, CSV/backup, offline, 15 dagposter gratis, Pro).
- `titel.txt` är oförändrad («Flextid», 7 tecken ≤ 30) men uppgiften att skriva färdiga texter är inte genomförd för kortbeskrivningen.
- `ButikTexterTest` kontrollerar bara existens, längd och http/www — inte innehåll mot exemplen; CI på grenen rapporterades grönt, lokalt saknas Android SDK.
- Inga filer i fel sökväg (repots rot); inga nya beroenden, behörigheter eller nätverk; ingen appkod ändrad.
- Tester använder JUnit 4 (`org.junit`), inte kotlin.test.

## Rättat av Cursor
- inget (saknat huvudresultat — inte ett småfel)

## Vid underkänd: försök 2

```
# Uppgift: svensk butikstitel och kort beskrivning

uppdrag: uppdrag/2026-10-18-flextid-butik-mork-valkomst.md
försök: 2
beror-på: cursor:2026-10-18-flextid-butik-mork-valkomst

## Vad som saknades
- Försök 1 ändrade bara uppgiftsfilen (flytt till `uppgifter/pagar/`). Du skulle skriva om **`flextid/butik/sv/titel.txt`** och **`flextid/butik/sv/kort.txt`** under modulen `flextid/`, inte i repots rot.
- **`kort.txt`** måste ersätta platshållaren med en riktig kort Google Play-beskrivning (≤ 80 tecken efter `trim()`), på svenska, som nämner appens funktioner och säljargument enligt uppgiften nedan — inte meta-text om «uppgift 01».
- **`titel.txt`**: en rad, trimmad, ≤ 30 tecken; appnamn preliminärt **Flextid** (kan vara bara namnet eller namn plus kort nytta, t.ex. «Flextid – offline» om det får plats).

## Ändra bara dessa filer
- flextid/butik/sv/titel.txt
- flextid/butik/sv/kort.txt

## Läs som förebild (ändra inte)
- flextid/butik/README.md
- flextid/APP.md
- flextid/src/test/kotlin/se/tmconnect/flextid/butik/ButikTexterTest.kt
- husloggen/butik/sv/titel.txt och husloggen/butik/sv/kort.txt (ton och längd, inte kopiera ordagrant)

## Uppgift
Skriv **svenska** Google Play-texter (huvudspråk i butiken). Appnamnet i butiken är preliminärt **Flextid** (arbetsnamn tills Mattias bestämmer vid publicering):

1. `titel.txt` — rubrik, högst **30 tecken** (hela filen efter `trim()`).
2. `kort.txt` — kort beskrivning, högst **80 tecken**.

Innehåll ska stämma med appen: stämpla in och ut per dag, rast, saldo mot egen normaltid, veckoöversikt, valfria påminnelser, CSV-export och säkerhetskopia som JSON. Appen antar inget kollektivavtal och ger ingen rådgivning. Säljargument: helt offline, ingen internetbehörighet, inga konton, ingen reklam, ingen analys. Gratis upp till **15 dagposter**; **Pro** (engångsköp) tar bort gränsen. Export och säkerhetskopia är alltid gratis.

**Tekniskt:** Bara ren text i filerna — ingen markdown, inga sökvägar, inga kodblock, inga citattecken runt hela filen.

**Gör inte:** lova juridiskt utfall eller vad avtal tillåter; nämna inte namngivna konkurrenter; inga påhittade betyg eller citat; inga `http` eller `www`.

## Exempel
- Titel ≤ 30 tecken, t.ex. `Flextid` (7) eller `Flextid – stämpla offline` (om ≤ 30 tecken).
- Kort beskrivning ≤ 80 tecken, i stil med `husloggen/butik/sv/kort.txt` men om flextid (stämpla, saldo, offline, 15 dagposter, Pro tar bort gränsen, export/backup gratis).
- `ButikTexterTest` passerar för `sv/titel.txt` och `sv/kort.txt`.

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*ButikTexterTest"

## Gör inte
- Ändra andra filer (inklusive engelska texter och `lang.txt`, samt uppgiftsfiler).
- Lägga till beroenden.
- Ändra appkod, manifest eller databasschema.
```

## Frågor till Claude
- inga
