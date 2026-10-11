# Uppdrag: säkerhetskopia till fil och import (Garantivalvet, Pro)

Läs CLAUDE.md, ARBETSFLODE.md och uppgifter/MALL.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

PDF-underlaget är klart (uppdrag `2026-10-11-export-pdf`) och `android:allowBackup` är `false`. Därför behöver användaren ett sätt att flytta eller säkra sin data själv: en säkerhetskopia till en fil som användaren sparar var hon vill, och import av en sådan fil, till exempel vid telefonbyte. Detta är sista tekniska steget i export (STATUS.md). Hela funktionen är Pro.

## Beslut som redan är fattade (Claude, BESLUTSLOGG.md 2026-10-11)

- Filen är en ZIP (`java.util.zip`, inga beroenden) med `kop.json` och en mapp `kvitton/` med kvittofilerna. Filnamn: `garantivalvet-sakerhetskopia-<ÅÅÅÅ-MM-DD>.zip`.
- `kop.json` innehåller `version` (heltal, börjar på 1), `skapad` (ISO-datum) och en lista med köp: vad, varKopt, kopdatum (ISO), garantiManader, prisOre, anteckning och kvittoFil (filnamn i `kvitton/`, eller utelämnad). Fält som saknas utelämnas. Inget id exporteras.
- JSON skrivs och läses med ren Kotlin (en liten egen skrivare och läsare) så att allt går att testa som enhetstest utan Android och utan nytt beroende. Använd inte `org.json` i logiken.
- Användaren väljer plats med `ActivityResultContracts.CreateDocument("application/zip")` och väljer fil vid import med `ActivityResultContracts.OpenDocument` (`application/zip` och `application/octet-stream`). Inga lagringsbehörigheter, inget nätverk.
- Import lägger bara till. Inget befintligt köp skrivs över eller tas bort. Ett köp räknas som dubblett (hoppas över) om vad, varKopt, kopdatum, garantiManader och prisOre är lika med ett befintligt köps. Kvittofiler kopieras in via `KvittoLager` med nya filnamn.
- En fil med `version` högre än appen förstår, eller som inte går att läsa, avvisas helt med ett begripligt meddelande. Ingen delvis import: allt eller inget (en Room-transaktion).
- Gratisgränsen gäller inte vid import, eftersom hela funktionen kräver Pro. Utan Pro öppnar knapparna den befintliga Pro-dialogen.
- Efter import planeras påminnelserna om (befintlig `PaminnelsePlanerare`). Inget databasschema ändras. Inga nya beroenden eller behörigheter.

## Uppdraget görs i två PR:er (ARBETSFLODE.md, "Uppdrag i två steg")

**PR 1, bara uppgiftsfiler.** Dela upp del B i uppgifter i `uppgifter/ny/` enligt `uppgifter/MALL.md` och CLAUDE.md punkt 4. Uppgifter som bygger på din kod i del A får `beror-på: cursor:2026-10-11-export-sakerhetskopia`. Numrera från 26. Öppna PR 1 först, och öppna den så fort filerna är skrivna, innan du börjar på del A.

**PR 2, del A.** Öppna den som en riktig PR (inte bara en gren) så fort den är klar. Skriv frågor under "Frågor till Claude" i PR-beskrivningen.

## Del A (Cursor)

1. **Säkerhetskopieformat (ren Kotlin, i `export/`):** datamodell för filen, JSON-skrivare, JSON-läsare och en funktion som avgör dubbletter. Läsaren returnerar ett resultat (lyckad lista eller felorsak: okänd version, trasig fil, saknad `kop.json`), aldrig ett undantag. Tester: rundresa (skriv, läs, lika), köp utan valfria fält, specialtecken och radbrytningar i text (citattecken, backslash, å ä ö, emoji), version 2 avvisas, trasig JSON avvisas, dubblettfunktionen på lika och olika köp.
2. **Skrivare och läsare mot ZIP (`export/`):** `Sakerhetskopia.skriv(OutputStream, kop, kvittoLager)` och `lasIn(InputStream)` som packar och packar upp `kop.json` och `kvitton/`. Skydd mot zip-slip (avvisa poster med `..` eller absolut sökväg) och mot orimligt stora poster (till exempel över 20 MB per fil, över 5 000 köp). Test för zip-slip och för ett kvitto som följer med.
3. **Import i repository eller ViewModel:** en funktion som tar läsarens resultat och lägger till köpen i en transaktion, kopierar kvitton och returnerar antal tillagda och antal hoppade över. Test mot Room i minnet om det redan finns förebild i testerna, annars mot en fake av DAO:n.

## Del B (uppgifter för den lokala modellen)

Förslag, som du får justera (varje uppgift högst 3 filer):
- **Strängar** (svenska och engelska): "Säkerhetskopia", "Spara säkerhetskopia", "Hämta från säkerhetskopia", förklarande text (att filen innehåller alla köp och kvitton, att den inte krypteras, att import bara lägger till), meddelandena "Säkerhetskopia sparad", "Importerade X köp, hoppade över Y", "Filen kunde inte läsas" och "Filen kommer från en nyare version av appen". Test som `StringsNycklarTest`.
- **Inställningsyta på listskärmen:** en plats för Säkerhetskopia (till exempel en meny eller en rad under påminnelsereglaget). Utan Pro öppnas Pro-dialogen. Tryckytor minst 48 dp.
- **Spara:** knapp som öppnar dokumentväljaren och skriver filen via del A, med meddelande efteråt.
- **Hämta:** knapp som öppnar filväljaren, läser filen via del A, visar bekräftelse med antal köp som ska läggas till (eller bara resultatet efteråt om det blir för stort), och meddelande efteråt.

## Klart när

- `gradle testDebugUnitTest` och `gradle assembleDebug` är gröna för alla moduler.
- Testerna täcker exemplen ovan.
- Den sammanslagna manifestet för `:garantivalvet` har `allowBackup="false"`, inte `INTERNET` och inga nya behörigheter.
- Uppgifterna för del B ligger i `uppgifter/ny/` via PR 1.

## Gör inte

- Ändra databasschemat.
- Lägga till beroenden eller behörigheter.
- Kryptera filen (nämn i stället i texten att den inte är krypterad).
- Låsa annat än export och import bakom Pro.
- Ändra `.github/workflows/`, `verktyg/`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md` eller `release.json`.
