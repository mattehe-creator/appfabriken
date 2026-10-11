# Uppdrag: reklamationsunderlag som PDF (Garantivalvet, Pro)

Läs CLAUDE.md, ARBETSFLODE.md och uppgifter/MALL.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Gratisgränsen och Pro är klara. Nästa steg i version 1.0 (STATUS.md) är export. Det görs i två uppdrag: först detta, reklamationsunderlag som PDF för ett köp; sedan säkerhetskopia till fil. Underlaget ska kunna lämnas till en butik vid reklamation eller garantiärende.

## Beslut som redan är fattade (BESLUTSLOGG.md 2026-10-11)

- PDF skapas med Androids inbyggda `android.graphics.pdf.PdfDocument`. Inga nya beroenden.
- Användaren väljer var filen sparas med systemets dokumentväljare (`ActivityResultContracts.CreateDocument("application/pdf")`), och kan dessutom dela den via `Intent.ACTION_SEND` med `FileProvider` (finns redan). Inga lagringsbehörigheter.
- Export är Pro. Utan Pro öppnar exportknappen den befintliga Pro-dialogen. Visning, ändring, påminnelser och reklamationsrätt förblir gratis.
- `android:allowBackup` sätts till `false` i `garantivalvet/src/main/AndroidManifest.xml` (data lämnar inte telefonen utan att användaren exporterar, CLAUDE.md punkt 6). Säkerhetskopian till fil i nästa uppdrag ersätter den.
- Inget databasschema ändras. Ingen nätverksåtkomst.

## Uppdraget görs i två PR:er (ARBETSFLODE.md, "Uppdrag i två steg")

**PR 1, bara uppgiftsfiler.** Dela upp del B i uppgifter i `uppgifter/ny/` enligt `uppgifter/MALL.md` och CLAUDE.md punkt 4. Uppgifter som bygger på din kod i del A får `beror-på: cursor:2026-10-11-export-pdf`. Numrera från 22. Öppna PR 1 först.

**PR 2, del A.** Öppna den när PR 1 är öppnad. Skriv frågor under "Frågor till Claude" i PR-beskrivningen.

## Del A (Cursor)

1. **Innehållsmodell (ren Kotlin, i `regler/`):** `data class Underlag(rader: List<Pair<String, String>>, ...)` eller liknande, och en ren funktion som bygger underlaget ur ett `Kop` och dagens datum, med etiketter som strängresurs-id eller lambda så att funktionen går att testa utan Android. Innehåll: vad, var köpt, köpdatum, pris, garantitid och garantislut, reklamationsrätt (slutdatum enligt `reklamationSlut`, presumtion enligt `presumtionSlut`, status enligt `reklamationStatus`), anteckning, och datum då underlaget skapades. Fält som saknas utelämnas. Test med minst tre exempel: köp med alla fält; köp utan pris, butik och anteckning (raderna saknas); köp före 2022-05-01 (reklamationsraderna följer befintliga regler).
2. **PDF-skrivare (`export/`):** skriver underlaget till en `OutputStream` som A4 med appens rubrik, raderna och kvittofotot (om det finns) nedskalat till sidans bredd på samma eller nästa sida. Fel ger ett resultat som UI kan visa, ingen krasch. Filnamnsfunktion (ren): `reklamation-<vad>-<köpdatum>.pdf` med otillåtna tecken ersatta, med test.
3. **Manifest:** `allowBackup="false"`. Lista den sammanslagna manifestets behörigheter i PR-beskrivningen; inga nya får tillkomma.

## Del B (uppgifter för den lokala modellen)

Förslag, som du får justera (varje uppgift högst 3 filer):
- **Strängar** (svenska och engelska) för etiketterna i underlaget, knapparna "Exportera PDF" och "Dela", och meddelandena "PDF sparad" och "PDF kunde inte skapas". Test som `StringsNycklarTest`.
- **Exportknapp i detaljvyn:** med Pro öppnar den dokumentväljaren och skriver PDF:en via del A; utan Pro visas Pro-dialogen. Tryckyta minst 48 dp.
- **Dela:** knapp som skapar PDF:en i appens cache och delar den via `FileProvider`.
- **Meddelande** efter lyckad eller misslyckad export (Snackbar), text via `stringResource`.

## Klart när

- `gradle testDebugUnitTest` och `gradle assembleDebug` är gröna för alla moduler.
- Testerna täcker exemplen ovan.
- Den sammanslagna manifestet för `:garantivalvet` har `allowBackup="false"`, inte `INTERNET` och inga nya behörigheter.
- Uppgifterna för del B ligger i `uppgifter/ny/` via PR 1.

## Gör inte

- Ändra databasschemat.
- Lägga till beroenden.
- Låsa annat än export bakom Pro.
- Göra säkerhetskopia eller import (nästa uppdrag).
- Ändra `.github/workflows/`, `verktyg/`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md` eller `release.json`.
