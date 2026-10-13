# Data safety — Garantivalvet (Play Console)

Svar att fylla i i Google Play Console, med motivering utifrån sammanslaget manifest för `:garantivalvet`, modulberoenden (`:karna` m.m.) och appens faktiska beteende. Mattias kopierar och justerar vid behov om formuläret ändras.

## Samlad bild

| Källa | Vad den innebär |
|--------|------------------|
| `garantivalvet/src/main/AndroidManifest.xml` | `POST_NOTIFICATIONS` för lokala påminnelser. `INTERNET` och `ACCESS_NETWORK_STATE` **tas bort** (`tools:node="remove"`). |
| `:karna` / Play Billing | Google Play Billing Library använder Play Store på enheten för engångsköpet Pro. Appen samlar inte in användardata för att skicka till egen server (ingen server finns). |
| Appdata | Köp, kvitton och inställningar lagras lokalt (Room, filer, DataStore). Export/säkerhetskopia sker **endast** när användaren själv sparar en fil. |

---

## 1. Samlar appen in eller delar användar- eller enhetsdata?

**Svar: Nej** (ingen insamling eller delning av personuppgifter till utvecklaren).

**Motivering:** Det finns ingen egen backend, ingen analys-SDK och ingen annons-SDK. Allt användaren matar in (köp, valfria fält, kvittobilder) stannar på enheten tills användaren exporterar eller raderar. Utvecklaren får ingen kopia automatiskt.

**Undantag att inte blanda ihop:** Play Console kan ställa följdfrågor om **köp i app** via Google Play. Det är Googles betalflöde, inte att TM Connect tar emot personuppgifter. Se avsnitt om Billing nedan.

---

## 2. Kryptering

**Svar:** Appen använder standard Android-lagring för lokal databas och filer. Säkerhetskopian som användaren kan spara är **inte** extra krypterad av appen (framgår av appens texter).

**Motivering:** Ingen egen nätverksöverföring av användarinnehåll. Eventuell disk-kryptering på enheten hanteras av operativsystemet, inte som separat deklaration i appen.

---

## 3. Kontotyper

**Svar:** Ingen inloggning, inget utvecklarkonto i appen.

**Motivering:** Ingen autentisering implementerad.

---

## 4. Data som användaren kan mata in (lokal lagring)

Följande lagras **på enheten** för appens funktion. Det ska **inte** markeras som insamlat/delat med utvecklaren om formuläret skiljer på “data the developer collects” vs “on-device only” — välj formulärets alternativ som motsvarar *ingen insamling av utvecklaren*.

| Data | Syfte (lokal) | Delas med utvecklaren |
|------|----------------|------------------------|
| Köpinformation (namn på sak, datum, pris, garantimånader, anteckningar) | Lista, påminnelser, PDF, export | Nej |
| Valfri butiks-/plats-text (“var köpt”) | Visning i appen | Nej |
| Kvitto-/foton | Kopplade till köp, visas i appen och kan ingå i export | Nej |
| Pro-status (köpt eller inte) | Låser upp funktioner | Nej (lagras lokalt; verifiering via Play) |

**Motivering:** Room (`kop`-tabell), filer via `KvittoLager`, DataStore för inställningar och Pro-flagga.

**AI-hjälp (delning):** I Reklamera kan användaren dela en färdig fråga (köpets fakta: vara, butik, datum, pris, felbeskrivning, krav) via Android-delningsmenyn (`ACTION_SEND`, text) till en AI-app som användaren själv väljer. Appen har ingen nätverkskod och skickar ingenting själv; det är användarens val och sker i den andra appen. Namn, adress och kvitto ingår aldrig. Rör inte svaren i avsnitt 1.

---

## 5. Behörigheter

| Behörighet | Varför | Data safety-koppling |
|------------|--------|----------------------|
| `POST_NOTIFICATIONS` | Lokala notiser om garanti/reklamation enligt användarens val | Notiser genereras på enheten; inget skickas till utvecklaren |
| `com.android.vending.BILLING` | Finns i **sammanslaget manifest** (`processDebugMainManifest`); kommer från Google Play Billing Library (`billing-ktx` i `:karna`), inte från appmodulens egen manifestfil | Play Store på enheten hanterar köp; utvecklaren tar inte emot betalningsuppgifter. Se avsnitt 6 |
| (ingen `INTERNET` i slutlig manifest) | Medvetet borttagen | Stödjer påståendet att appen inte använder nätverk för egen datainsamling |

Kvittotexten läses av **på enheten** med ML Kit Text Recognition (medföljande modell, inget nätverk, `INTERNET` borttagen och låst av `IngenNatverkTest`). Bilden och den avlästa texten lämnar aldrig telefonen, och texten sparas inte; den fyller bara formulärfält som användaren kontrollerar. Inget deklareras som insamlat eller delat.

Automatisk säkerhetskopia är **av som standard**. Slår användaren på den väljer hen en mapp med systemets filväljare (`ACTION_OPEN_DOCUMENT_TREE`); appen får då skriva i just den mappen och ingenting annat, utan någon lagringsbehörighet i manifestet. Appen skickar inget själv och har inget nätverk; ligger mappen hos användarens egen molntjänst är det den tjänstens app som synkar. Kopian är en ZIP med samma innehåll som den manuella säkerhetskopian och är inte krypterad. De 5 senaste automatiska kopiorna behålls.

Kamera och lagring används via **systemintents** (fotoväljare/kamera) enligt projektregler, inte som breda lagringsbehörigheter i manifest.

---

## 6. Google Play Billing (Pro, engångsköp)

**Vad biblioteket gör:** `billing-ktx` i `:karna` ansluter till **Google Play** för att visa köpflöde, hämta produktinformation och bekräfta engångsköp. Köptoken och produkt-id hanteras av Play; appen sparar bara en lokal **Pro ja/nej**-flagga efter lyckat köp.

**Play Console — typisk tolkning:**

- Det är **inte** att utvecklaren säljer användardata.
- Markera **inte** att appen “samlar in” betalningsuppgifter: kort och betalning hanteras av Google Play.
- Om formuläret kräver deklaration av **köp i app** / **financial info**: välj att sådan information **inte** samlas in av utvecklaren, eller följ Googles guide för “Purchases handled by Google Play” om den visas som eget steg.
- **Delning:** Data delas i betalningsflödet med **Google** som betalningsleverantör, inte med TM Connects server (finns ingen).

**Motivering:** `PlayProKalla` pratar med `BillingClient`; ingen TM Connect-backend. Manifest saknar `INTERNET` — kommunikation sker via Play Store-appen på enheten.

---

## 7. Analys, reklam, kraschrapportering

| Tjänst | I appen? | Data safety |
|--------|----------|-------------|
| Firebase / Analytics | Nej | — |
| Annonser | Nej | — |
| Egna krasch-SDK:er | Nej | Krascher via Play Console enligt CLAUDE.md |

---

## 8. Barn och känsliga kategorier

**Svar:** Appen riktar sig inte till barn. Ingen hälsodata, ingen platsinsamling, inga konton.

**Motivering:** Produktregler i CLAUDE.md punkt 6.

---

## 9. Butiksgrafik — högupplöst ikon 512 × 512

**Status i repot:** Det fanns ingen färdig PNG 512 × 512 i `butik/` före detta uppdrag (endast vektor/adaptive launcher i `res/`).

**Lösning:** `ikon-512.png` genereras i JVM-test (Roborazzi) från launcher-foreground och temafärg, samma pipeline som skärmbilder. Filen ligger i `garantivalvet/butik/ikon-512.png`.

---

## 10. Checklista innan publicering

- [ ] Data safety-svar stämmer med **sammanslaget manifest** (kör `./gradlew :garantivalvet:processDebugMainManifest` vid tvivel).
- [ ] Butikstexter i `sv/` och `en/` innehåller inga URL:er (kontrolleras av `ButikTexterTest`).
- [ ] Skärmbilder och grafik i `butik/` motsvarar faktisk UI (Roborazzi).
