# Uppdrag: Husloggen, säkerhetskopia, import och automatisk säkerhetskopia

Läs CLAUDE.md, ARBETSFLODE.md, uppgifter/MALL.md och husloggen/APP.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

STATUS.md, Nu, punkt 8. Loggen är underlag när bostaden säljs, så användaren måste kunna flytta och säkra sin data själv: säkerhetskopia till en fil, import av en sådan fil och en automatisk säkerhetskopia. Garantivalvet har redan detta (uppdragen `2026-10-11-export-sakerhetskopia` och den automatiska säkerhetskopian i BESLUTSLOGG.md). Garantivalvet är funktionsfryst och får inte ändras. Därför **lyfter Cursor de generella delarna till `:karna`** som nya klasser (BESLUTSLOGG.md 2026-10-16), och Husloggen använder dem. Tillfällig dubblering mot Garantivalvet är bättre än att röra den.

Uppdraget skickas medvetet i förväg, medan bilder och påminnelser körs (Mattias 2026-10-16: kön ska aldrig vara tom).

## Beslut som redan är fattade (börja med att läsa Garantivalvets uppdrag och kod som förebild)

- Filen är en ZIP (`java.util.zip`, inga beroenden) med `logg.json` och mappen `bilder/` med bildfilerna. Namn: `husloggen-sakerhetskopia-<ÅÅÅÅ-MM-DD>.zip`. JSON skrivs och läses med ren Kotlin så att allt går att enhetstesta.
- `logg.json` innehåller `version` (börjar på 1), `skapad`, alla **uppgifter** (namn, intervall i månader, startdatum, senast gjord, anteckning) och alla **poster** (datum, vad, typ, belopp i öre, utförare, anteckning, kopplad uppgift via index i filen, bildfilnamn). Inget databas-id exporteras.
- Import lägger bara till. Inget befintligt skrivs över eller tas bort. Dubblett (hoppas över): lika fält enligt en ren funktion med test. Bildfiler kopieras in via `BildLager` med nya filnamn. En fil med högre `version` eller en trasig fil avvisas helt (allt eller inget, en Room-transaktion).
- Användaren väljer plats med `CreateDocument("application/zip")` och fil med `OpenDocument`. Inga lagringsbehörigheter, inget nätverk.
- Hela säkerhetskopian (fil och import) är **gratis** i Husloggen. Gränsen 15 poster gäller inte vid import, eftersom export och säkerhetskopia alltid är gratis (CLAUDE.md punkt 6).
- Efter import planeras påminnelserna om, och återställda poster räknas inte bort av gränsen.
- **Automatisk säkerhetskopia:** följ Garantivalvets lösning (läs dess uppdrag och kod först) och lyft den till `:karna`. Tar den med någon ny behörighet, nytt beroende eller `INTERNET`, gör inte den delen: skriv i stället frågan under "Frågor till Claude" och leverera filen och importen utan den. CLAUDE.md punkt 5: Husloggen begär inte `INTERNET`.

## Uppdraget görs i två PR:er (ARBETSFLODE.md, "Uppdrag i två steg")

**PR 1, bara uppgiftsfiler.** Dela upp del B i uppgifter i `uppgifter/ny/` enligt `uppgifter/MALL.md` och CLAUDE.md punkt 4. Uppgifter som bygger på din kod får `berör-på` med uppgiftsfilen eller `cursor:2026-10-16-husloggen-sakerhetskopia`. Öppna PR 1 först.

**PR 2, del A.** Öppna den när PR 1 är öppnad. Frågor under "Frågor till Claude" i PR-beskrivningen.

## Del A (Cursor)

1. **Format (ren Kotlin):** datamodell, JSON-skrivare och -läsare, dubblettfunktion. Läsaren returnerar ett resultat (lyckad eller felorsak), aldrig ett undantag. Tester: rundresa, poster utan valfria fält, specialtecken (citattecken, backslash, å ä ö, emoji), version 2 avvisas, trasig JSON avvisas, koppling mellan post och uppgift bevaras, dubbletter.
2. **ZIP:** skriv och läs `logg.json` och `bilder/` med skydd mot zip-slip och orimligt stora poster. Test för zip-slip och för att en bild följer med.
3. **Import:** en transaktion som lägger till uppgifter och poster, kopierar bilder och returnerar antal tillagda och överhoppade. Test mot Room i minnet.
4. **Lyft till `:karna`** det som är generellt (ZIP-hjälp, skydd mot zip-slip, den automatiska säkerhetskopians kontroll och schema) som nya klasser. Rör inte Garantivalvets motsvarigheter.

## Del B (uppgifter för den lokala modellen)

Förslag som du får justera. Högst 3 filer per uppgift och med test.

- **Strängar** (svenska och engelska): "Säkerhetskopia", "Spara säkerhetskopia", "Hämta från säkerhetskopia", förklaring (filen innehåller alla uppgifter, poster och bilder, den krypteras inte, import lägger bara till), "Säkerhetskopia sparad", "Importerade X poster, hoppade över Y", "Filen kunde inte läsas", "Filen kommer från en nyare version av appen". Test som `StringsNycklarTest`.
- **Inställningsyta:** en plats för Säkerhetskopia (meny eller rad). Tryckytor minst 48 dp.
- **Spara** och **Hämta**: knappar som öppnar dokumentväljaren och anropar del A, med meddelande efteråt.

## Klart när

- `gradle testDebugUnitTest` och `gradle assembleDebug` är gröna för alla moduler.
- Testerna täcker exemplen ovan.
- Sammanslagna manifestet för `:husloggen` saknar `INTERNET`, och den enda tillkommande behörigheten, om någon, är godkänd i PR-beskrivningen.
- Databasschemat är oförändrat (schema 1).
- Uppgifterna för del B ligger i `uppgifter/ny/` via PR 1.
- Ingen fil i `garantivalvet/` är ändrad.

## Gör inte

- Ändra databasschemat eller något i `garantivalvet/`.
- Lägga till beroenden eller behörigheter utan att fråga under "Frågor till Claude".
- Kryptera filen (nämn i stället i texten att den inte är krypterad).
- Låsa säkerhetskopia eller export bakom Pro.
- Ändra `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md` eller `release.json`.
