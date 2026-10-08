# Idéurval

Upprättat 2026-10-08 av Claude. Valet godkändes av Mattias 2026-10-08.

## Kriterier

Poäng 1–5 per kriterium, multiplicerat med vikten. Max 50.

| Kriterium | Vikt | Vad som ger 5 |
|---|---|---|
| Byggbar med lokal modell | 3 | Få skärmar, ren CRUD, inget nätverk, inga ovanliga API:er |
| Låg konkurrens | 2 | Få bra appar för just detta, särskilt på svenska |
| Betalvilja | 2 | Löser ett problem som kostar användaren pengar eller tid |
| Upptäckbarhet | 1 | Tydliga sökord som folk faktiskt skriver i Play |
| Återanvändning i portföljen | 1 | Mönstret (lista + datum + påminnelse + export) kan bli nästa app |
| Låg policyrisk | 1 | Inga känsliga behörigheter, ingen hälsodata, inte barn som målgrupp |

Underlag: små appar tjänar i regel mest på freemium med engångsköp eller prenumeration; reklam ger en liten app i storleksordningen några hundralappar i månaden och kräver tusentals dagliga användare. Betalda appar utan publik säljer dåligt. Därför bedöms alla idéer som freemium med ett engångsköp (Pro), utan reklam.

## Idéer

| # | Idé | Bygg | Konk. | Betal | Upptäck | Återanv. | Risk | Summa |
|---|---|---|---|---|---|---|---|---|
| A | **Garantivalvet** — kvitton, garantier och reklamationsfrister med påminnelser | 4 | 4 | 3 | 3 | 5 | 5 | **39** |
| B | Mätarställning — el, vatten, fjärrvärme; förbrukning per period | 5 | 3 | 2 | 3 | 4 | 5 | 37 |
| G | Husets underhållslogg — filter, sotning, OVK, takrännor; återkommande påminnelser | 4 | 3 | 3 | 3 | 5 | 5 | 37 |
| C | Laddloggen — elbilsladdning hemma och publikt, kostnad, kWh/100 km | 4 | 3 | 3 | 3 | 3 | 5 | 35 |
| F | Flextid — flex- och komptid efter svenska avtalsregler | 4 | 3 | 3 | 2 | 3 | 4 | 33 |
| J | Packlistor — återanvändbara listor för resor | 5 | 1 | 1 | 2 | 4 | 5 | 30 |
| D | Frysinventering med bäst före | 4 | 1 | 2 | 2 | 4 | 5 | 29 |
| E | Glosförhör för skolbarn | 4 | 2 | 2 | 3 | 3 | 2 | 28 |
| H | Elpris per timme | 3 | 1 | 1 | 3 | 2 | 3 | 21 |
| I | Sopkalender | 2 | 3 | 1 | 3 | 1 | 3 | 20 |

Avförda direkt: H och I kräver nätverk och extern data som ändras, vilket bryter mot direktivet om offline och gör underhållet löpande. E riktar sig till barn och drar in Googles familjepolicy.

## Val: A, Garantivalvet

**Varför:**
- Konkret pengavärde. Den som har kvittot och vet att reklamationsfristen inte har gått ut får pengar tillbaka. Det gör ett engångsköp lätt att motivera.
- Svensk vinkel. Generella garanti-appar finns, men få som räknar med konsumentköplagens reklamationsrätt i tre år. Det ger sökord som "reklamation", "kvitto" och "garanti" på svenska med svagare konkurrens än på engelska.
- Byggbar i små steg: Room-tabell, lista, formulär, foto via systemets väljare, en daglig kontroll i WorkManager och export till PDF med Androids inbyggda `PdfDocument`.
- Mönstret återanvänds. B och G är samma skelett (post + datum + påminnelse + export) och blir app 2 och 3.

**Svagheter att känna till:**
- Användaren måste komma ihåg att fotografera kvittot vid köpet. Appen måste göra det snabbt, annars används den inte.
- Det juridiska innehållet måste vara korrekt och formulerat som information, inte rådgivning. Reglerna gäller Sverige; i andra länder visar appen bara garantitiden.
- Fristerna (reklamation inom tre år från mottagandet, och hur länge ett fel antas ha funnits vid köpet) ska kontrolleras mot lagtexten i konsumentköplagen (2022:260) på riksdagen.se innan uppdraget för regelmotorn skrivs. Siffrorna i appen hämtas från ett enda ställe i koden, med lagrumshänvisning.

## Omfång för version 1.0

Gratis:
- Lägg till köp: vad, var, datum, pris (valfritt), garantitid i månader, foto av kvittot, anteckning.
- Lista sorterad efter närmaste frist, med status: garanti gäller, reklamationsrätt gäller, utgånget.
- Påminnelse 30 dagar innan en garanti eller reklamationsfrist går ut (dagligt jobb, notis).
- Visa kvittot i helskärm och dela bilden.
- Högst 15 köp.

Pro (engångsköp, förslag 49 kr):
- Obegränsat antal köp.
- Reklamationsunderlag: PDF med kvittot och köpuppgifterna, färdig att mejla till butiken.
- Säkerhetskopia till och från fil som användaren väljer.

Inte i 1.0: molnsynk, OCR av kvitton, delning mellan användare, widgets.

## Portföljordning

1. A Garantivalvet
2. G Husets underhållslogg (samma skelett, återkommande påminnelser)
3. B Mätarställning (samma skelett, grafer)

Ordningen omprövas efter 90 dagar med A i produktion.
