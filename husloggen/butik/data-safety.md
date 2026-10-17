# Data safety — Husloggen (Play Console)

Svar att fylla i i Google Play Console, med motivering utifrån sammanslaget manifest för `:husloggen`, modulberoenden (`:karna` m.m.) och appens faktiska beteende. Mattias kopierar och justerar vid behov om formuläret ändras. Appnamn i butiken är preliminärt (arbetsnamn Husloggen).

## Samlad bild

| Källa | Vad den innebär |
|--------|------------------|
| `husloggen/src/main/AndroidManifest.xml` | `POST_NOTIFICATIONS` för lokala påminnelser om underhållsuppgifter. `INTERNET` och `ACCESS_NETWORK_STATE` **tas bort** (`tools:node="remove"`). |
| `:karna` / Play Billing | Google Play Billing Library använder Play Store på enheten för engångsköpet Pro. Appen samlar inte in användardata för att skicka till egen server (ingen server finns). |
| Appdata | Uppgifter, loggposter, bilder och inställningar lagras lokalt (Room, filer, DataStore). Export och säkerhetskopia sker **endast** när användaren själv sparar en fil. |

---

## 1. Samlar appen in eller delar användar- eller enhetsdata?

**Svar: Nej** (ingen insamling eller delning av personuppgifter till utvecklaren).

**Motivering:** Det finns ingen egen backend, ingen analys-SDK och ingen annons-SDK. Allt användaren matar in (uppgifter, loggposter, valfria fält, kvittobilder) stannar på enheten tills användaren exporterar eller raderar. Utvecklaren får ingen kopia automatiskt.

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
| Underhållsuppgifter (namn, intervall, datum, påminnelser, anteckningar) | Att göra-lista, påminnelser | Nej |
| Loggposter (beskrivning, datum, typ, belopp, utförare, anteckningar) | Logg, export, sammanställning | Nej |
| Kvittobilder | Kopplade till poster, visas i appen och kan ingå i export/säkerhetskopia | Nej |
| Pro-status (köpt eller inte) | Tar bort gränsen på 15 poster | Nej (lagras lokalt; verifiering via Play) |

**Motivering:** Room (`uppgift`, `post`), bildfiler via appens bildlager, DataStore för inställningar och Pro-flagga.

---

## 5. Behörigheter

| Behörighet | Varför | Data safety-koppling |
|------------|--------|----------------------|
| `POST_NOTIFICATIONS` | Lokala notiser om förfallna underhållsuppgifter enligt användarens val | Notiser genereras på enheten; inget skickas till utvecklaren |
| `com.android.vending.BILLING` | Finns i **sammanslaget manifest**; kommer från Google Play Billing Library (`billing-ktx` i `:karna`) | Play Store på enheten hanterar köp; utvecklaren tar inte emot betalningsuppgifter. Se avsnitt 6 |
| `WAKE_LOCK`, `RECEIVE_BOOT_COMPLETED`, `FOREGROUND_SERVICE` | WorkManager (påminnelser, automatisk säkerhetskopia) — **sammanslaget release-manifest** | Schemaläggning på enheten; ingen nätverks- eller molninsamling |
| (ingen `INTERNET` i slutlig manifest) | Medvetet borttagen | Stödjer påståendet att appen inte använder nätverk för egen datainsamling |

Kamera och lagring används via **systemintents** (fotoväljare/kamera) enligt projektregler, inte som breda lagringsbehörigheter i manifest.

Automatisk säkerhetskopia är **av som standard**. Slår användaren på den väljer hen en mapp med systemets filväljare; appen skickar inget själv och har inget nätverk. Kopian är en ZIP med samma innehåll som den manuella säkerhetskopian och är inte krypterad.

---

## 6. Google Play Billing (Pro, engångsköp)

**Vad biblioteket gör:** `billing-ktx` i `:karna` ansluter till **Google Play** för engångsköp Pro. Appen sparar bara en lokal **Pro ja/nej**-flagga efter lyckat köp. Pro tar bort gränsen på 15 loggposter; uppgifter, påminnelser, export och säkerhetskopia är gratis.

**Play Console — typisk tolkning:**

- Det är **inte** att utvecklaren säljer användardata.
- Markera **inte** att appen “samlar in” betalningsuppgifter: kort och betalning hanteras av Google Play.
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

**Motivering:** Produktregler i CLAUDE.md punkt 6 och `husloggen/APP.md`.

---

## 9. Butiksgrafik

Skärmbilder (1080 × 1920), funktionsgrafik (1024 × 500) och ikon (512 × 512) genereras i JVM-test (Roborazzi) och ligger under `husloggen/butik/`. Se `README.md` för återskapande.

---

## 10. Checklista innan publicering

- [ ] Data safety-svar stämmer med **sammanslaget manifest** (kör `./gradlew :husloggen:processDebugMainManifest` vid tvivel).
- [ ] Butikstexter i `sv/` och `en/` innehåller inga URL:er (kontrolleras av `ButikTexterTest`).
- [ ] Skärmbilder och grafik i `butik/` motsvarar faktisk UI (Roborazzi).
