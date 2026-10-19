# Uppdrag: Styrelserummet, webbtjänsten för K3-komponentlistan

Läs CLAUDE.md, ARBETSFLODE.md och uppgifter/MALL.md först och följ dem. Ändra inget utöver uppdraget.

Bygger på `2026-10-19-styrelserummet-k3-karna.md` och `2026-10-19-styrelserummet-underlag.md`. Börja först när båda är mergade på main. Är den andra inte klar: vänta.

## Bakgrund

Föreningens styrelse ska själv kunna ladda upp underhållsplan och årsredovisning, se utkastet, rätta raderna och ladda ned listan, utan att någon i bolaget pratar med dem. Landningssidan `webb/k3/sida.html` (utseende, texter, pris 990 kr per förening, betalning först när utkastet är sett) är förebilden för formspråk och ton.

## Beslut (Claude, 2026-10-19)

- Ny modul `:styrelserummet-server`: Kotlin/JVM med Ktor (servermotor CIO). Ktor och `ktor-server-*` godkända som beroenden i denna modul. Använder `:styrelserummet` för all logik. Ingen databas: ett jobb är en mapp med filer under en katalog som anges av `STYRELSERUMMET_DATA`.
- Gränssnittet är statiska filer (HTML, CSS, vanlig JavaScript utan ramverk) som servern levererar. Samma tokens och typsnitt som `webb/k3/sida.html`, mörkt läge, tangentbordsbart, kontrast minst 4,5:1, svenska först.
- Flöde: ladda upp en eller flera PDF:er, ange föreningens namn och räkenskapsårets slut, vänta (status visas), se tabellen med komponenter. Varje rad kan ändras (värde, år, nyttjandeperiod, grupp) och tabellen räknas om direkt via servern. Rader med `LAG` eller `UPPSKATTNING` är markerade och förklarade. Avstämningen mot byggnadens värde visas överst.
- Utdata: CSV (semikolon) och en utskriftsvänlig sida som blir PDF via webbläsarens utskrift. Båda bär "UTKAST. Beslutas av förening och revisor." Excel kan komma senare.
- Uppladdade filer och resultat raderas automatiskt 30 dagar efter sista aktivitet; användaren kan radera direkt med en knapp. Inget konto: jobbet nås via en lång slumpad länk (minst 128 bitar) som visas en gång och som användaren sparar. Ingen e-post skickas i den här versionen.
- Gränser: högst 10 filer och 25 MB per jobb, högst 5 jobb per IP och timme. Fel visas på klarspråk.
- Betalning och konton ingår inte här. Utkastet är gratis att se tills Mattias har ett betalkonto (Mattias-steg); då kommer ett eget uppdrag. Sidan säger inget om betalning som inte stämmer.
- Hosting: servern paketeras som Docker-avbild (`Dockerfile`) och startar med `STYRELSERUMMET_DATA`, `ANTHROPIC_API_KEY`, `PORT`. Vilken värd som används beslutar Claude när Mattias har skapat kontot; lokalt körs den med `gradle :styrelserummet-server:run`.
- Integritet: ingen spårning, inga cookies utöver det tekniskt nödvändiga, inga tredjepartsskript eller externa typsnitt. Texten som skickas till Anthropic är maskad enligt föregående uppdrag. Cursor lägger en kort sida `/integritet` utifrån detta, som Claude sedan granskar.

## Del A (Cursor)

1. Modul, `Dockerfile`, serverns rutter (`POST /api/jobb`, `GET /api/jobb/{id}`, `PUT /api/jobb/{id}/rader`, `GET /api/jobb/{id}.csv`, `DELETE /api/jobb/{id}`), jobbkö med en bearbetning i taget, rensning efter 30 dagar.
2. Gränssnittet: uppladdning, status, redigerbar tabell, utdata, `/integritet`.
3. Test av rutterna med Ktors testmotor och falsk `Tolkare`. Test på gränserna och på raderingen.
4. En kort `styrelserummet/DRIFT.md`: miljövariabler, hur man startar lokalt, vad som ska göras vid första driftsättning (för Mattias).
5. Uppgifter för den lokala modellen där det går (till exempel länkgenerering, gränskontroller, rensningsregeln som ren funktion), annars gör Cursor det själv.

## Klart när

- Alla moduler är gröna i GitHub Actions.
- Med falsk tolkare kan man lokalt ladda upp en PDF, se och ändra tabellen, ladda ned CSV och radera jobbet.
- `DRIFT.md` räcker för att Mattias ska kunna driftsätta utan fler frågor.

## Gör inte

- Lägga till betalning, konton, e-post, analys eller tredjepartsskript.
- Spara nyckel eller riktiga handlingar i repot.
- Ändra `webb/k3/`, andra moduler, `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md`.
