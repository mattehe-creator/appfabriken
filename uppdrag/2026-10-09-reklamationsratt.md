# Uppdrag: reklamationsrätt enligt konsumentköplagen (Garantivalvet)

Läs CLAUDE.md, ARBETSFLODE.md och uppgifter/MALL.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Påminnelser är klart. Nästa del av version 1.0 (STATUS.md, IDEURVAL.md) är reklamationsrätten: appen ska, utöver butikens garanti, visa den lagstadgade tiden för att reklamera fel, och påminna 30 dagar innan den går ut. Det är appens säljargument: den som har kvittot och vet att fristen inte gått ut kan få pengar tillbaka även när garantin är slut.

Fristerna är kontrollerade mot lagtexten på riksdagen.se (konsumentköplagen 2022:260, 2026-10-09). Använd exakt dessa, och bara dessa:

| Regel | Lagrum | Värde |
|-------|--------|-------|
| Näringsidkaren svarar för fel som visar sig inom | 4 kap. 14 § | 3 år från avlämnandet |
| Fel som visar sig inom detta antas ha funnits vid köpet (bevisbördan hos säljaren) | 4 kap. 17 § | 2 år från avlämnandet |
| Reklamation inom denna tid från att felet märktes anses alltid i rätt tid | 5 kap. 2 § | 2 månader |
| Lagen gäller avtal som ingåtts | ikraftträdande | 1 maj 2022 eller senare |

Appen räknar från **köpdatum** (avlämnandet kan ligga senare, men köpdatum är det användaren har på kvittot). För köp före 1 maj 2022 gäller en äldre lag (1990:932) med andra regler, så appen visar ingen reklamationsfrist för dem. Allt som visas är vägledning, inte juridisk rådgivning, och texten ska säga det.

## Uppdraget görs i två PR:er (ARBETSFLODE.md, "Uppdrag i två steg")

**PR 1, bara uppgiftsfiler.** Dela upp del B i uppgifter i `uppgifter/ny/` enligt `uppgifter/MALL.md` och CLAUDE.md punkt 4. Uppgifter som bygger på din kod i del A får raden `beror-på: cursor:2026-10-09-reklamationsratt`. Uppgifter som inte behöver den (strängarna) har inget sådant beroende. Numrera uppgifterna från 12. Öppna PR 1 först.

**PR 2, del A.** Öppna den när PR 1 är öppnad.

## Del A (Cursor)

1. **`regler/Konsumentkoplagen.kt`**, ett enda ställe för siffrorna, med lagrumshänvisning i KDoc. Innehåll: konstanterna ovan, `reklamationSlut(kopdatum): LocalDate?` (tre år fram, `null` för köp före 2022-05-01), `presumtionSlut(kopdatum): LocalDate?` (två år, samma regel) och en status `ReklamationStatus` (`FEL_ANTAS_FUNNITS`, `GALLER`, `UTGANGEN`, `EJ_TILLAMPLIG`). Enhetstester med dessa exempel:
   - Köp 2024-03-15: `reklamationSlut` = 2027-03-15, `presumtionSlut` = 2026-03-15.
   - Köp 2024-02-29: `reklamationSlut` = 2027-02-28.
   - Slutdagen räknas som sista dagen (samma regel som garantin): idag = slutdatum → inte utgången, dagen efter → utgången.
   - Köp 2024-03-15, idag 2025-06-01 → `FEL_ANTAS_FUNNITS`; idag 2026-06-01 → `GALLER`; idag 2027-03-16 → `UTGANGEN`.
   - Köp 2022-04-30 → `null` och `EJ_TILLAMPLIG`; köp 2022-05-01 → tillämplig.
2. **Påminnelse:** `FristTyp.REKLAMATION`. `PaminnelseWorker` bygger två frister per köp (garanti och reklamation, reklamation bara om `reklamationSlut` inte är null). Nyckeln innehåller typen, så de aviseras var för sig. Notisen får egen text för reklamation, med strängar på svenska och engelska i `strings.xml`. Befintliga tester för påminnelser ska fortsätta gå igenom, och lägg till ett test där båda fristerna för samma köp ligger inom 30 dagar.
3. **Databasen ändras inte.** Reklamationsfristen räknas fram ur `kopdatum`, den sparas aldrig.

## Del B (uppgifter för den lokala modellen)

Förslag, som du får justera (varje uppgift högst 3 filer):
- **Strängar** (svenska och engelska): status "Reklamationsrätt gäller", raden "Reklamationsrätt till <datum>", rubrik och förklaring ("Du kan reklamera fel som visar sig inom tre år från köpet. Fel som visar sig inom två år antas ha funnits vid köpet. Räknas från köpdatum och gäller varor köpta av en näringsidkare."), en rad om att det är vägledning och inte juridisk rådgivning, och att köp före 1 maj 2022 omfattas av äldre regler. Test som `StringsNycklarTest`.
- **`regler/KopStatus.kt`:** samlad status per köp: `GARANTI_GALLER`, `GARANTI_SNART`, `REKLAMATION_GALLER`, `UTGANGEN`. Exempel: köp 2024-01-10 med 24 månaders garanti, idag 2025-06-01 → `GARANTI_GALLER`; idag 2025-12-20 → `GARANTI_SNART`; idag 2026-02-01 (garantin slut 2026-01-10, reklamation till 2027-01-10) → `REKLAMATION_GALLER`; idag 2027-01-11 → `UTGANGEN`. Köp 2022-04-30 med utgången garanti → `UTGANGEN`. Befintliga `garantiStatus` och dess tester ändras inte.
- **Sortering:** ny funktion `sorteraEfterNarmasteFrist` i `regler/KopSortering.kt`. Aktiva köp (någon frist inte passerad) sorteras efter närmaste frist som inte passerat (garantislut, annars reklamationsslut), utgångna sist med senast utgångna först. `sorteraEfterGaranti` och dess tester ändras inte.
- **Listan:** raden visar den samlade statusen (färg och text), och listan använder den nya sorteringen.
- **Detaljvyn:** raden "Reklamationsrätt till <datum>" och förklaringen, dold för köp utan reklamationsfrist.

## Klart när

- `gradle testDebugUnitTest` och `gradle assembleDebug` är gröna.
- Testerna täcker exemplen ovan.
- Inga nya behörigheter, inga nya beroenden, inget nätverk.
- Uppgifterna för del B ligger i `uppgifter/ny/` via PR 1.

## Gör inte

- Ändra databasschemat eller lägg till kolumner. Ingen migration.
- Visa påståenden som låter som juridisk rådgivning, eller ange andra frister än de i tabellen.
- Beräkna reklamationsfrist för köp före 2022-05-01.
- Ändra `:karna`, `.github/workflows/`, `verktyg/`, `STATUS.md`, `BESLUTSLOGG.md` eller `IDEURVAL.md`.
