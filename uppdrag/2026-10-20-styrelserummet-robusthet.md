# Uppdrag: Styrelserummet, robusthet mot riktiga handlingar

Läs CLAUDE.md, ARBETSFLODE.md och uppgifter/MALL.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Kärnan, underlaget och servern är mergade och testade mot påhittade exempel. Riktiga underhållsplaner och årsredovisningar är stökigare: belopp i tusentals kronor, parenteser för negativa tal, tabeller med två år, hårda mellanslag, personnummer i andra format, och en AI som lindar sitt JSON-svar i kodstaket. Det här uppdraget gör läsningen och indatahanteringen tåligare. Det är **uppgiftsarbete för den lokala modellen**: små rena funktioner med tester.

## Beslut (Claude, 2026-10-20)

- **Cursor skriver ingen implementation i det här uppdraget.** Cursor delar upp i uppgifter (minst 12, högst 18) enligt MALL.md och CLAUDE.md punkt 4 och lämnar all kod åt den lokala modellen. Cursor tar bara det som uppgiftsformatet inte klarar (byggfiler, en ändring i fler än 3 filer) och säger då varför i PR-beskrivningen.
- Uppgifterna ska vara **oberoende av varandra** där det går (`beror-på: ingen`), så att ett underkänt försök inte blockerar kön.
- Inga nya beroenden. Inga ändringar i datamodellen (`Komponent`, `Forening`).
- Varje uppgift har ett eget test med exempel från "Underlag" nedan, och ska inte byta beteende för redan fungerande fall; befintliga tester ska fortsätta gå igenom.

## Underlag (Cursor delar upp efter dessa områden)

1. **Belopp ur årsredovisningen** (`underlag/HittaByggnadensVarde.kt`, `parseBelopp`): "Byggnader och mark" med belopp i tkr eller mnkr ("12 345 tkr", "12,3 mnkr"); hårda mellanslag (U+00A0) och smala mellanslag som tusentalsavgränsare; punkt som tusentalsavgränsare ("12.345.678"); negativa tal med minus eller parentes ("(1 234)"); en rad med två kolumner (innevarande år och föregående år) där **innevarande år** (den första) ska väljas; rubrikrad med årtal ska inte tolkas som belopp. En uppgift per fall eller par av fall.
2. **Maskning** (`underlag/MaskaPersonuppgifter.kt`): personnummer med tolv siffror utan bindestreck, samordningsnummer (dag 61-91), personnummer med plus (över 100 år); telefon `070 123 45 67`, `0701-23 45 67`, `0046 70 123 45 67`; e-post med versaler och bindestreck i domänen. Falska träffar som **inte** ska maskas: organisationsnummer `769612-3456`, bankgiro `123-4567`, belopp `1 234 567`, årtal, datum `2025-12-31`, fastighetsbeteckningar. Varje fall är en uppgift.
3. **Tolka svar** (`underlag/TolkaForslag.kt`): JSON inlindad i kodstaket (```json ... ```), text före eller efter JSON-objektet, avslutande kommatecken, `null` på fält som ska vara tal, tal som kommit som text ("1 200 000" eller "1200000") ska bli heltal. Ogiltigt ska fortfarande ge `LAG` och en anmärkning, aldrig undantag.
4. **Anmärkningar** (`underlag/Anmarkningar.kt`): ny regel per uppgift: komponent med anskaffningsår före byggnadsåret; komponent med anskaffningsår efter räkenskapsårets slut; summan av komponenternas anskaffningsvärde över 150 procent eller under 50 procent av byggnadens redovisade värde (separat tydlig text); två komponenter med samma namn; komponent där nyttjandeperioden redan gått ut men bokfört värde är över restvärdet.
5. **Indata i servern** (`styrelserummet-server/.../server/`): `sakertFilnamn(namn: String): String` (tar bort sökvägsdelar, `..`, kontrolltecken, begränsar längd, tomt blir `fil.pdf`); `arPdf(forstaByte: ByteArray): Boolean` (börjar med `%PDF-`); `valideraForeningsnamn(namn: String): String?` (felmeddelande eller null; tomt, längre än 120 tecken och kontrolltecken ger fel); `valideraArsslut(text: String, nuAr: Int): Int?` (heltal mellan 2000 och nuAr + 1). Därefter en uppgift som **kopplar in** dem i `Application.kt` med test i `ApiTest.kt` (en ändring i få rader, hör hemma hos den lokala modellen om den ryms).

## Klart när

- Alla uppgifter är skrivna i `uppgifter/ny/` med exempel och test, och PR:en med dem är mergad.
- `gradle :styrelserummet:test :styrelserummet-server:test` och övriga moduler är gröna när uppgifterna har gjorts.

## Gör inte

- Skriva implementationen själv.
- Lägga till beroenden eller ändra `Komponent`, `Forening` och andra modeller.
- Ändra `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md`, `webb/`, övriga moduler.
