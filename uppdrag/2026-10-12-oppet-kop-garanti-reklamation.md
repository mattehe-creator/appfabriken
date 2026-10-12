# Uppdrag: öppet köp, varutyp och skillnaden mellan garanti och reklamation (Garantivalvet)

Läs CLAUDE.md, ARBETSFLODE.md och uppgifter/MALL.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Mattias provade kvittoavläsningen (#117, #119, #121) och vill ha tre saker i 1.0 före Play:

1. **Öppet köp.** Många kvitton säger det själva (Biltema: "ÖPPET KÖP I 30 DAGAR MOT UPPVISANDE AV KVITTO"). Appen ska läsa av antalet dagar och visa en nedräkning.
2. **Varutyp.** Öppet köp varierar mellan varor. Appen ska kunna fråga vilken typ av vara det är.
3. **Garanti och reklamation är olika saker** och ska visas var för sig:
   - **Öppet köp** är butikens frivilliga erbjudande, inte lag. Villkoren står på kvittot eller hos butiken och skiljer mellan butiker och varor.
   - **Garanti** är frivillig (tillverkare eller butik) och har den tid användaren anger.
   - **Reklamationsrätt** är lagstadgad (konsumentköplagen, redan i appen via `Konsumentkoplagen.kt`).

Idag blandas garanti och reklamation ihop i en enda status. Allt som visas är vägledning, inte juridisk rådgivning, och ska säga det. Appen får **inte** hitta på butikers villkor: öppet köp visas bara när kvittot säger det eller användaren själv anger det.

## Databas (kräver godkännande enligt CLAUDE.md punkt 10: Mattias har godkänt 2026-10-12)

Version 3, två nya nullbara kolumner i `kop`:
- `oppet_kop_dagar INTEGER` (null = inget öppet köp)
- `varutyp TEXT` (null = ej angiven; en av `ELEKTRONIK`, `KLADER_SKOR`, `HEM_BYGG`, `MAT_DRYCK`, `OVRIGT`)

`GarantiMigrations.kt` får `MIGRATION_2_3` (`ALTER TABLE kop ADD COLUMN ...`), schemafilen `schemas/3.json` genereras, och `GarantiDatabasMigrationTest` får ett test som migrerar en version 2-databas med data och kontrollerar att raderna finns kvar med båda fälten `null`.

## Uppgift

**Del A (Cursor, tung del, PR 2):**
1. Databasändringen ovan, `Kop`, `KopRepository`.
2. Säkerhetskopian: `kop.json` får de två fälten (valfria). Filer utan fälten ska läsas som förut (`null`); ingen versionshöjning behövs om gamla filer fortsatt går att läsa, annars höj till 2 och läs 1 och 2. Test för båda riktningarna.
3. `regler/OppetKop.kt`: `oppetKopSlut(kopdatum, dagar): LocalDate` (köpdagen räknas som dag 0, slutdagen är sista dagen) och `oppetKopStatus(...)`: `GALLER`, `SLUTAR_SNART` (3 dagar eller mindre kvar), `UTGANGEN`. Exempel: köp 2026-09-01, 30 dagar → slut 2026-10-01; idag 2026-10-01 → `SLUTAR_SNART`; idag 2026-10-02 → `UTGANGEN`; idag 2026-09-10 → `GALLER`.
4. Påminnelse: `FristTyp.OPPET_KOP`, notis tre dagar före slut (egen text, sv/en). Befintliga påminnelsetester ska fortsätta gå igenom.
5. Statusen per köp blir tre oberoende delar (öppet köp, garanti, reklamation) i stället för en blandad. `KopStatus` och listans chip ändras så att listan visar den mest brådskande aktiva friststypen i klartext ("Öppet köp till 1 okt", "Garanti till …", "Reklamationsrätt till …"). Befintliga tester som ändras ska ändras med avsikt; skriv i PR:en vilka.
6. PDF-underlaget (`Underlag.kt`, `PdfUnderlagSkrivare.kt`) visar de tre delarna under egna rubriker, med en rad om vilken som är lag och vilka som är frivilliga.

**Del B (lokal modell, uppgifter via PR 1, numrera från 48):**
- **Tolkaren:** `tolkaOppetKop(text): Int?` i `regler/KvittoTolkare.kt` eller egen fil. Känner igen "öppet köp i 30 dagar", "30 dagars öppet köp", "öppet köp 14 dagar", "Öppet köp: 30 dgr" (skiftlägesokänsligt, `ö`/`o`). Exempel: `"ÖPPET KÖP I 30 DAGAR MOT UPPVISANDE AV KVITTO"` → 30; `"60 dagars öppet köp"` → 60; `"Öppet köp"` utan antal → `null`; `"öppet köp i 400 dagar"` → `null` (över 365 är orimligt). `KvittoForslag` och `fyllTommaFalt` får `oppetKopDagar` (fylls bara om fältet är tomt).
- **Formuläret:** fält "Öppet köp (dagar)" med chips Inget / 14 / 30 / Annat, förifyllt och märkt "Förslag från kvittot – kontrollera" när kvittot sa det. Fråga "Vilken sorts vara?" (chips för varutyperna, valfri, kan hoppas över) och en hjälptext: "Öppet köp är butikens frivilliga erbjudande och skiljer mellan butiker och varor. Kontrollera på kvittot." Garantifältet byter rubrik till "Garanti (frivillig)" med hjälptext om att reklamationsrätten enligt lag gäller oavsett.
- **Detaljvyn:** tre separata kort, "Öppet köp" (bara om det finns), "Garanti" och "Reklamationsrätt (lag)", var och en med egen slutdag och förklaring. Reklamationskortet säger tydligt att det gäller oavsett garanti.
- **Strängar** sv/en för allt ovan (`StringsNycklarTest` ska gå igenom).
- Varutypen **styr inget** i den här versionen; den sparas, visas och tas med i underlaget. Styrning av öppet köp per varutyp kräver att Mattias anger vilka regler som ska gälla och kommer som eget uppdrag.

## Klart när

- `gradle testDebugUnitTest` och `gradle assembleDebug` är gröna för alla moduler.
- Migrationen 2→3 är testad och schemafilen `3.json` finns.
- Exemplen ovan är tester.
- Inga nya behörigheter, inga nya beroenden, inget nätverk.
- Uppgifterna för del B ligger i `uppgifter/ny/` via PR 1.

## Gör inte

- Ange butikers öppet köp-villkor, andra frister än de i `Konsumentkoplagen.kt`, eller något som låter som juridisk rådgivning.
- Låta varutypen ändra några frister.
- Ändra `:karna`, `.github/workflows/`, `verktyg/`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md` eller `release.json`.
