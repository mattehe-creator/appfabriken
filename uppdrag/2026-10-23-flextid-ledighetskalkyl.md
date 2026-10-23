# Uppdrag: Flextid, ledighetskalkyl

## Bakgrund

Flextid (flextid/APP.md) visar saldot i dag och vid årsslut, men inte det användaren oftast undrar: "Hur många hela flexdagar kan jag ta ut, och när kan jag tidigast vara ledig utan att gå under golvet?" Allt lokalt, inget nätverk, ingen ny behörighet, inget nytt beroende, ingen databasändring.

## Uppgift

Cursor delar upp arbetet i små uppgifter för den lokala modellen (uppgifter/MALL.md, högst 3 filer och cirka 150 rader per uppgift) och gör själv det som rör ViewModel och navigering. Läs först vad som redan finns i `:flextid` (saldo, golv och tak, normaltid, arbetsdagsmask, helgdagar, ledighetstyper); bygg vidare på det och skriv avvikelser under "Frågor till Claude".

1. Regel i `regler/`: `ledighetsutrymme(saldoMin, golvMin, normaltidMin)` ger antal hela dagar som kan tas ut utan att saldot hamnar under golvet (golv null = inget golv, då ingen gräns och resultatet är null). Exempel: saldo 1 200 min, golv 0, normaltid 480 ger 2 dagar; saldo 479, golv 0 ger 0; saldo 300, golv -480 (golv är en negativ gräns), normaltid 480 ger 1 dag; normaltid 0 ger null.
2. Regel i `regler/`: `tidigasteLedigDag(idag, antalDagar, arbetsdagarMask, helgdagar)` ger första datumet från och med imorgon då `antalDagar` arbetsdagar i följd (hoppar över helg och röda dagar) kan tas ut, det vill säga datumet för den första ledighetsdagen. Exempel: idag torsdag 2026-10-22, 1 dag, standardmask ger fredag 2026-10-23; idag fredag 2026-10-23, 1 dag ger måndag 2026-10-26; 0 dagar ger null.
3. Ett kort på Översikt: "Du kan ta ut X flexdagar" med datumet för tidigaste ledighet, och texten "Ingen gräns satt" när golv saknas. Kortet räknar på saldot idag, inte på prognosen. Strängar på svenska och engelska i `strings.xml`, tryckytor minst 48 dp.
4. Knappen "Planera ledig dag" på kortet öppnar dagformuläret för det datumet med typen ledig förvald. Redan registrerade dagar ändras inte.

## Klart när

- Enhetstester för punkt 1 och 2 med exemplen ovan, plus minst ett gränsfall vardera (saldo exakt på golvet, ledighet över en helg).
- Test som visar att kortet visar rätt text för golv satt, golv saknas och saldo för lågt.
- `gradle :flextid:testDebugUnitTest` och bygget är gröna.

## Gör inte

- Ändra databasschemat, Garantivalvet, Husloggen eller `:karna` (om `:karna` måste röras: stanna och skriv det under "Frågor till Claude").
- Lägga till kalenderintegration, notiser eller delning av ledighet.
- Ändra `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md`.
- Kräva svar från Mattias. Frågor skrivs under "Frågor till Claude" i PR-beskrivningen.
