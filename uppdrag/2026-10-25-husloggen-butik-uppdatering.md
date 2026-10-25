# Uppdrag: Husloggen, butiksmaterialet ska stämma med appen

Läs CLAUDE.md (punkt 6, 7 och 8), ARBETSFLODE.md, uppgifter/MALL.md, husloggen/APP.md och husloggen/butik/README.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

En release är klar först när Play-texterna, skärmbilderna och Data safety-svaren i `husloggen/butik/` stämmer med appen (CLAUDE.md punkt 7). Sedan butiksmaterialet skrevs (2026-10-21) har appen fått flera bostäder med flytt och radering, Kommande år, widget med kommande underhåll, mätare med årsprognos, dokument, planerade kostnader, sök och filter i loggen samt tillgänglighetsrättelser. Texterna kan ha hamnat efter.

## Uppgift

Cursor delar upp arbetet i små uppgifter för den lokala modellen (uppgifter/MALL.md, högst 3 filer och cirka 150 rader per uppgift) och gör själv det som är för tungt.

1. Jämför funktionerna i appen (APP.md, strängresurserna och skärmarna) med butikstexterna i `husloggen/butik/sv/` och `husloggen/butik/en/`. Lägg till det som saknas och ta bort det som inte längre stämmer. Kort och sakligt, inga löften om det som inte finns, ingen rådgivning om skatt (information, med hänvisning till Skatteverket).
2. Kontrollera `husloggen/butik/data-safety.md` mot manifestet: behörigheter, om `INTERNET` begärs, vad som lämnar telefonen (inget utan användarens export eller säkerhetskopia) och widgeten. Rätta avvikelser.
3. Kontrollera `husloggen/butik/integritetspolicy-sv.md` och `integritetspolicy-en.md` mot samma underlag. Rätta avvikelser.
4. Kontrollera att `husloggen/butik/PUBLICERA.md` nämner Pro-priset 49 kr, gratisgränsen 15 poster och att uppgifter och påminnelser är obegränsade, och att texten stämmer med appens beteende.
5. Skärmbilder: lista i PR-beskrivningen vilka skärmar i `husloggen/butik/skarmbilder/` som inte längre ser ut som appen (till exempel Översikt, bostadsväljaren, Kommande år). Förnya dem bara om workflowet `skarmbilder.yml` gör det utan ändring; annars skriv under "Frågor till Claude" vilka som behöver göras om.

## Klart när

- Butikstexterna sv och en nämner samma funktioner och stämmer med appen.
- Data safety och integritetspolicyn stämmer med manifestet.
- PR-beskrivningen listar vad som ändrades och vilka skärmbilder som är föråldrade.
- Bygget och testerna är gröna.

## Gör inte

- Ändra appkod, strängresurser i appen, databasschemat, `:karna` eller andra appar.
- Lägga till beroenden eller behörigheter.
- Ändra `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md`.
- Kräva svar från Mattias. Frågor skrivs under "Frågor till Claude" i PR-beskrivningen.
