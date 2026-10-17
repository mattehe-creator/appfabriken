# Play Console: svar att klistra in, steg för steg

För Mattias (CLAUDE.md punkt 9: allt i Play Console är ditt). Texter och bilder ligger i `butik/`; stegen för bygget står i `PUBLICERA.md`. Här står vad du svarar i varje formulär.

## 0. Konto och betalning (företagskonto)

- Organisationskonto: Play Console kräver ett D-U-N-S-nummer och verifiering av företaget. Det kan ta några dagar, så börja först.
- **Betalprofil (handlarkonto):** krävs för att kunna sälja Pro (engångsköp). Fyll i bankuppgifter och skatteuppgifter i Play Console under "Betalningsprofil". Utan den går köpet inte att skapa.
- Som organisationskonto gäller normalt inte kravet på slutet test med 12 testare i 14 dagar (det gäller nya personkonton). Play Console visar på sidan "Släpp / Testa och släpp" vad som gäller för just ditt konto.

## 1. Skapa appen

- Appnamn: **Garantivalvet** (svenska, standardspråk). Lägg till engelska: **Warranty Vault**.
- Typ: App. Kostnad: **Gratis** (Pro är ett köp i appen).
- Bekräfta deklarationerna (riktlinjer, exportlagar).

## 2. Butiksinformation (Utveckla > Butiksnärvaro > Huvudbutiksinformation)

| Fält | Svenska | Engelska |
|---|---|---|
| Appnamn | `sv/titel.txt` | `en/titel.txt` |
| Kort beskrivning | `sv/kort.txt` | `en/kort.txt` |
| Fullständig beskrivning | `sv/lang.txt` | `en/lang.txt` |

- Ikon: `ikon-512.png`. Funktionsgrafik: `funktionsgrafik-sv.png` / `-en.png` (1024 × 500).
- Telefonskärmbilder: `skarmbilder/sv/` och `skarmbilder/en/` (minst 2, helst 4–8; välj lista, detalj, formulär, Reklamera, inställningar och välkomstskärmen).
- Kategori: **Verktyg**. Tagg: gärna "Ekonomi" eller "Produktivitet" om Play erbjuder det.
- Kontaktuppgifter: e-postadress (visas offentligt), valfritt webbadress.
- Butiksinformation ska följa `ButikTexterTest` (längd, inga länkar).

## 3. Appinnehåll (Policy > Appinnehåll)

| Formulär | Svar |
|---|---|
| Integritetspolicy | Länken till sidan med texten från `integritetspolicy-sv.md` (och engelsk version). Kontaktadressen i texten ska vara ifylld. |
| Annonser | **Nej**, appen innehåller inga annonser. |
| Appåtkomst | Alla funktioner är tillgängliga utan inloggning eller särskild åtkomst. |
| Målgrupp | **18 år och äldre** (appen riktar sig inte till barn). |
| Innehållsbetyg (IARC) | Kategori: verktyg/övrigt. Alla frågor om våld, sex, språk, hasardspel, droger och användarskapat delat innehåll: **Nej**. Appen delar ingen användardata och har ingen kommunikation mellan användare. |
| Nyhetsapp | Nej. |
| COVID-19-app | Nej. |
| Hälso- och sjukvård | Nej, ingen hälsodata. |
| Finansiella funktioner | Nej (appen är ingen bank-, lån- eller kryptotjänst; Pro är ett köp via Google Play). |
| Statliga appar | Nej. |
| Annons-ID (Advertising ID) | **Appen använder inte annons-ID.** Kontrollera att manifestet inte har `AD_ID` (sammanslaget manifest listas i `data-safety.md`; Cursors kontroll från 2026-10-13 visade ingen). |
| Dataskydd (Data safety) | Följ `data-safety.md` avsnitt 1–8: **ingen** data samlas in eller delas av utvecklaren; ingen kryptering deklareras utöver systemet; ingen datakategori markeras. |

## 4. Priser och distribution

- Länder: **Sverige** (juridisk information i appen gäller köp i Sverige; texterna säger det). Lägg till fler länder senare om du vill.
- Köp i appen: skapa en **engångsprodukt** (hanterad produkt) med produkt-id **`pro`**, namn "Garantivalvet Pro", pris **49 kr**, aktiv. Id:t måste vara exakt `pro` (står i `ProKonfig`).

## 5. Första uppladdningen

1. Följ `PUBLICERA.md` (Release-workflowet, signera AAB:n med din uppladdningsnyckel).
2. Aktivera **Play App Signing** när du laddar upp första gången (rekommenderas).
3. Börja i **Intern testning**: lägg dig själv (och gärna en eller två till) som testare, installera från Play och prova **köpet av Pro** (går bara att prova där).
4. När det fungerar: skapa en **produktionsversion**. Första granskningen hos Google kan ta några dagar.

## 6. Efter publicering

- Svara på recensioner själv (CLAUDE.md punkt 9). Följ betyg och konvertering enligt punkt 1. Apparna underhålls och förbättras vidare även när siffrorna är låga.
- Ladda upp `mapping-<sha>.txt` för varje release så att kraschrapporter blir läsbara.
