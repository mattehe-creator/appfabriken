beslut: underkänd
granskare: cursor
datum: 2026-10-12

## Kontrollerat
- Filer utanför uppgiften ändrade: `garantivalvet/butik/sv/lang.txt` (lång beskrivning tillhör uppgift 32; förbjudet i uppgiften).
- `garantivalvet/butik/sv/titel.txt` är korrupt (innehåller `` ``` `` och raden `garantivalvet\butik\sv\kort.txt`), 68 tecken — `ButikTexterTest` faller på rad 26 (max 30).
- `garantivalvet/butik/sv/kort.txt` finns och är under 80 tecken men uppgiften kräver att båda texterna skrivs enligt säljargumenten; titeln måste rättas och `lang.txt` återställas.
- Inga appkod-, manifest- eller schemaändringar; inga nya beroenden, behörigheter eller nätverk.
- `ButikTexterTest` använder JUnit 4; testet jämför filinnehåll mot teckengränser (inte bara att koden körs).
- CI (`bygg`): `:garantivalvet:testDebugUnitTest` — `ButikTexterTest > butikstexterFinnsOchHallerGranser` FAILED.

## Rättat av Cursor
- inget (stora fel: fel fil ändrad, titel trasig)

## Vid underkänd: försök 2

```
# Uppgift: svensk butikstitel och kort beskrivning

uppdrag: uppdrag/2026-10-12-butiksmaterial.md
försök: 2
beror-på: ingen

## Vad som saknades
- `garantivalvet/butik/sv/titel.txt` får bara innehålla själva titeln (en rad, trimmad), högst 30 tecken. Försök 1 lämnade skräp (backticks och en Windows-sökväg till `kort.txt`) i filen, vilket gjorde att `ButikTexterTest` failade.
- `garantivalvet/butik/sv/lang.txt` ändrades trots att uppgiften uttryckligen säger att lång beskrivning skrivs i uppgift 32. Återställ `lang.txt` till innehållet på `main` (platshållartexten).
- Skriv om `kort.txt` om du behöver få med fler säljargument inom 80 tecken; ändra inte `en/` eller `lang.txt`.

## Ändra bara dessa filer
- garantivalvet/butik/sv/titel.txt
- garantivalvet/butik/sv/kort.txt

## Läs som förebild (ändra inte)
- garantivalvet/butik/README.md
- garantivalvet/src/test/kotlin/se/tmconnect/garantivalvet/butik/ButikTexterTest.kt
- garantivalvet/butik/en/titel.txt och garantivalvet/butik/en/kort.txt (ton och längd, inte översättning i denna uppgift)
- CLAUDE.md punkt 6

## Uppgift
Skriv **svenska** Google Play-texter (huvudspråk i butiken):

1. `titel.txt` — appnamn/rubrik, högst **30 tecken** (räkna tecken i hela filen efter `trim()`).
2. `kort.txt` — kort beskrivning, högst **80 tecken**.

Innehåll ska stämma med appen och får använda dessa säljargument: helt offline, ingen internetbehörighet, inga konton, ingen reklam, ingen analys, gratis upp till 15 köp, Pro (engångsköp) ger obegränsat antal köp, PDF-underlag och säkerhetskopia.

**Gör inte:** lova juridiskt utfall; nämna inte namngivna konkurrenter; inga påhittade betyg eller användarcitat; inga `http` eller `www`.

**Tekniskt:** Spara filerna under `garantivalvet/butik/sv/`, inte i repots rot. Bara text i filerna — ingen markdown, inga sökvägar, inga kodblock.

## Exempel
- Titel ≤ 30 tecken, t.ex. `Garantivalvet` (13) eller `Garantivalvet – offline` (23 med en-dash).
- Kort beskrivning ≤ 80 tecken, i stil med engelska `en/kort.txt` men med svenska säljargument (offline, 15 köp gratis, Pro).
- `ButikTexterTest` passerar för `sv/titel.txt` och `sv/kort.txt`.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*ButikTexterTest"

## Gör inte
- Ändra andra filer (inklusive `sv/lang.txt`, engelska texter och `lang.txt` på main).
- Lägga till beroenden.
- Ändra appkod, manifest eller databasschema.
```

## Frågor till Claude
- inga
