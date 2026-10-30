# Frågor till Mattias

Claude skriver här när något kräver Mattias (se ARBETSFLODE.md). Mattias svarar i chatten.

## Öppna

- (2026-10-29) **Fredag, i den här ordningen** (det som tar publicering och intäkter framåt; resten nedan kan vänta):
  1. Prova Garantivalvet och Husloggen på telefonen, cirka 30 minuter (APK från releasen `senaste-main`). Utan det publiceras ingenting. Skriv vad som är fel, så blir det uppgifter över helgen.
  2. Slå på GitHub Pages (steg 1 i frågan 2026-10-21 nedan, en minut). Play kräver en publik länk till integritetspolicyn.
  3. Skicka en kontaktadress för policyerna (steg 2 i frågan 2026-10-21).
  4. Play Console: kontot, internt testspår och produkten `pro` (frågan 2026-10-11).
  5. Flextid-gränsen: 15 poster, 60 dagar eller senaste 30 dagarna (frågan 2026-10-24).
  Styrelserummet (värd, nyckel, bolagsuppgifter) och Formspree väntar tills 1-4 är gjorda.

- (2026-10-24) Flextid och gratisgränsen 15 poster: i Flextid är en post en dag, så gratisversionen räcker i tre arbetsveckor. Förslag: gränsen i Flextid räknas som 60 dagar (ungefär tre månader) i stället, eller att bara de senaste 30 dagarna går att ändra utan Pro. Säg vilket, eller att 15 ska gälla.

- (2026-10-23) Valfritt, och inget annat väntar på det: du kan provköra Styrelserummet på din egen dator med en riktig API-nyckel (låg utgiftsgräns) och en påhittad eller riktig underhållsplan. Steg för steg i `styrelserummet/PROVKORNING.md`, cirka 15 minuter. Det är det snabbaste sättet att se om AI:ns läsning av handlingar håller, innan något läggs ute. Gratisverktygen (avgift, fondplan, nyckeltal, protokoll, räntor, stämmotidplan) behöver ingen nyckel.
- (2026-10-21) Tre saker, i den ordningen, var för sig några minuter:
  1. Slå på GitHub Pages: repot → Settings → Pages → Source: **GitHub Actions**. Utan det har landningssidan `webb/k3/` aldrig legat ute, och integritetspolicyerna kan inte länkas från Play. Adresserna blir `https://mattehe-creator.github.io/appfabriken/k3/` och `.../<app>/integritet/` (svenska) och `.../<app>/privacy/` (engelska).
  2. En kontaktadress för policyerna (gärna en adress för TM Connect, inte din privata). Jag lägger den i `webb/kontakt.txt`, då uppdateras alla sex sidorna.
  3. Landningssidan samlar inga anmälningar förrän den har en mottagare. Skapa ett gratis konto på formspree.io med TM Connects adress, skapa ett formulär och skicka mig formulärets adress (`https://formspree.io/f/...`). Jag lägger in den.

- (2026-10-20) Styrelserummet är byggt och mergat (kärna, underlag, server, UI). Inget behövs av dig i natt. Före driftsättning behöver jag av dig: (1) exakt bolagsnamn, organisationsnummer och en kontakt-e-post för integritetssidan (jag har skrivit "TM Connect" utan bolagsform tills du svarar), (2) att du skapar ett konto hos en värd när jag har valt (jag föreslår då en), (3) en Anthropic API-nyckel som hemlighet hos värden, (4) om du vill ha betalning: ett betalkonto. Det går att prova servern lokalt först: `styrelserummet/DRIFT.md`.
- (2026-10-13) Play kräver en publik länk till integritetspolicyn. Texterna ligger färdiga i `garantivalvet/butik/integritetspolicy-sv.md` och `-en.md`, med en plats för kontaktadress. Jag föreslår att du lägger dem som en sida på TM Connects webbplats (eller GitHub Pages om du vill). Skicka mig länken och kontaktadressen så lägger jag in dem i butiksunderlaget. Texterna i `butik/` (butiksbeskrivning, kort text) är uppdaterade för nya funktioner och är ditt beslut innan publicering.
- (2026-10-12) Ny design och skärmbilder är klara på main. Installera APK:n från releasen `senaste-master` och prova igen (steg 3 i STATUS.md): lista, formulär, detaljsida, PDF och säkerhetskopia med "Testläge: Pro" under kugghjulet. Svara med vad som fungerar och vad som inte gör det.
- (2026-10-11) Vid första publiceringen av Garantivalvet: skapa engångsprodukten med id `pro` i Play Console och sätt priset (förslag 49 kr). Köpet går inte att prova på telefon förrän appen ligger i ett testspår (intern testning) i Play Console. Inget behövs nu.

## Besvarade

- (2026-10-17) Husloggen, rot/rut vid försäljning: regeln bekräftad (netto utgift). Se `husloggen/APP.md` och BESLUTSLOGG.md 2026-10-17. Implementerat i uppdrag rot/rut-avdrag.
- (2026-10-12) Prov på telefon: PDF och Dela fungerar inte, utseendet för dåligt, startsidan visar fel saker först. Blev uppdrag `2026-10-12-provfel-pdf-pro` och `2026-10-12-ny-design`.
