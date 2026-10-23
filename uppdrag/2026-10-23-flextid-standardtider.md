# Uppdrag: Flextid, standardtider och "fyll i vanlig dag"

## Bakgrund

Flextid (flextid/APP.md) kräver i dag att användaren anger start, slut och rast för varje dag. De flesta har nästan samma tider varje arbetsdag. Nästa steg är att användaren kan ange vanliga tider en gång och registrera en dag med ett tryck. Allt lokalt, inget nätverk, ingen ny behörighet, inget nytt beroende.

## Uppgift

Cursor delar upp arbetet i små uppgifter för den lokala modellen (uppgifter/MALL.md, högst 3 filer och cirka 150 rader per uppgift) och gör själv det som rör databas, DataStore och ViewModel. Börja med att läsa vad som redan finns i `:flextid`; finns en del redan, bygg vidare på den och skriv det under "Frågor till Claude".

1. Inställningar får vanliga tider: starttid, sluttid och rast i minuter (standard 08:00, 16:30, 30). Lagras i DataStore (som övriga inställningar), ingen databasändring.
2. Regel i `regler/` som bygger ett dagsförslag för ett datum: använder vanliga tider på arbetsdagar enligt bitmasken, ger inget förslag på dagar som inte är arbetsdagar eller på svenska helgdagar, och ger inget förslag om slut är före eller lika med start. Exempel: onsdag, 08:00, 16:30, rast 30 ger 08:00-16:30 och rast 30 (480 min arbetat); lördag med standardmask ger inget förslag; slut 07:00 med start 08:00 ger inget förslag.
3. Formuläret för ny dag förifylls med förslaget för valt datum, och användaren kan ändra som förut. Redan registrerade dagar förifylls aldrig om.
4. Knappen "Fyll i vanlig dag" på dagens vy sparar förslaget för idag via repository (samma validering och gratisgräns som formuläret; texten vid gräns återanvänds `pro_grans_nadd`). Finns dagen redan ändras den inte.
5. Strängar i `strings.xml` på svenska och engelska. Tryckytor minst 48 dp, innehållsbeskrivning på ikoner.

## Klart när

- Enhetstester för punkt 2 med exemplen ovan och minst ett fall för helgdag.
- Test för punkt 4: ny dag sparas, befintlig dag lämnas orörd, gratisgränsen respekteras.
- `gradle :flextid:testDebugUnitTest` och bygget är gröna.

## Gör inte

- Ändra databasschemat, Garantivalvet, Husloggen eller `:karna` (om `:karna` måste röras: stanna och skriv det under "Frågor till Claude").
- Lägga till kollektivavtal, flera tidsprofiler per veckodag eller widget.
- Ändra `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md`.
- Kräva svar från Mattias. Frågor skrivs under "Frågor till Claude" i PR-beskrivningen.
