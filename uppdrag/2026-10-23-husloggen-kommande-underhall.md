# Uppdrag: Husloggen, plan för kommande underhåll och kostnader

Läs CLAUDE.md, ARBETSFLODE.md, uppgifter/MALL.md och husloggen/APP.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Husloggen har uppgifter med intervall, logg, mätare med pris, uppskattad kostnad kommande tolv månader (PR #553) och sammanställning per år. Det som saknas är en samlad vy över vad som väntar de kommande åren: vilka uppgifter som förfaller när, och vad de ungefär kostar. Underlaget finns redan i uppgifternas nästa datum, intervall och senaste kostnad per uppgift.

## Uppgift

Cursor delar upp arbetet i små uppgifter för den lokala modellen (uppgifter/MALL.md, högst 3 filer och cirka 150 rader per uppgift) och gör själv det som är för tungt (skärm, navigering, databas).

1. Ren funktion `kommandeUnderhall(uppgifter, loggposter, idag, antalAr)`: för varje uppgift med intervall, räkna ut alla förfallodatum från nästa datum till och med `idag` plus `antalAr` år (standard 5). Uppskattad kostnad per tillfälle är senaste kända kostnad för uppgiften, annars okänd (null, aldrig gissad). Resultat grupperat per år med summa per år och en markering när någon post saknar kostnad.
2. Skärm "Kommande år" nåbar från Översikt: lista per år med uppgift, datum och uppskattad kostnad, årssumma högst upp, text som säger att summan är en uppskattning och att poster utan kostnad inte ingår.
3. CSV-export av samma lista via den delning som sammanställningen redan använder.
4. Strängar i `strings.xml` på svenska och engelska. Inga nya behörigheter, inget nytt beroende, ingen ändring av databasschemat.

## Klart när

- Enhetstester för `kommandeUnderhall`: månadsintervall, årsintervall, uppgift utan kostnad (null och markering), uppgift utan intervall (ingår inte), skottdag, `antalAr` = 1.
- `gradle :husloggen:test` och bygget är gröna.
- Skärmen går att nå och visar påhittade värden korrekt.

## Gör inte

- Ändra databasschemat, `:karna` eller Garantivalvet.
- Ändra `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md`.
- Kräva svar från Mattias. Frågor skrivs under "Frågor till Claude" i PR-beskrivningen.
