# Data safety — Flextid (Play Console)

Svar att fylla i i Google Play Console, med motivering utifrån sammanslaget manifest för `:flextid`, modulberoenden (`:karna` m.m.) och appens faktiska beteende. Mattias kopierar och justerar vid behov om formuläret ändras. Appnamn i butiken är preliminärt (arbetsnamn Flextid).

## Samlad bild

| Källa | Vad den innebär |
|--------|------------------|
| `flextid/src/main/AndroidManifest.xml` | `POST_NOTIFICATIONS` för lokala påminnelser om att stämpla in eller ut. `INTERNET` och `ACCESS_NETWORK_STATE` **tas bort** (`tools:node="remove"`). |
| `:karna` / Play Billing | Google Play Billing Library använder Play Store på enheten för engångsköpet Pro. Appen samlar inte in användardata för att skicka till egen server (ingen server finns). |
| Appdata | Dagposter, inställningar (normaltid, arbetsdagar, saldo m.m.) och UI-val lagras lokalt (Room, DataStore). Export och säkerhetskopia sker **endast** när användaren själv sparar en fil. |

---

## 1. Samlar appen in eller delar användar- eller enhetsdata?

**Svar: Nej** (ingen insamling eller delning av personuppgifter till utvecklaren).

**Motivering:** Det finns ingen egen backend, ingen analys-SDK och ingen annons-SDK. Allt användaren matar in (tider, dagtyper, anteckningar, egna regler) stannar på enheten tills användaren exporterar CSV/JSON eller raderar appen. Utvecklaren får ingen kopia automatiskt.

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
| Dagposter (datum, start/slut, rast, dagtyp, anteckning) | Flextidsregistrering, vecka, saldo | Nej |
| Inställningar (normaltid, arbetsdagar, ingångssaldo, standardrast) | Beräkning och visning | Nej |
| Påminnelser på/av | Lokala notiser enligt användarens val | Nej |
| Pro-status (köpt eller inte) | Tar bort gränsen på 15 dagposter | Nej (lagras lokalt; verifiering via Play) |
| Tema och UI-val (t.ex. exportmånad) | Utseende och export | Nej |

**Motivering:** Room (`dag`, `installning`), DataStore för UI och påminnelser, Pro-flagga via `:karna`.

---

## 5. Behörigheter

| Behörighet | Varför | Data safety-koppling |
|------------|--------|----------------------|
| `POST_NOTIFICATIONS` | Lokala notiser om att stämpla in (från kl. 08:00 på arbetsdagar) respektive stämpla ut (efter normaltid) enligt användarens val | Notiser genereras på enheten; inget skickas till utvecklaren |
| `com.android.vending.BILLING` | Finns i **sammanslaget manifest**; kommer från Google Play Billing Library (`billing-ktx` i `:karna`) | Play Store på enheten hanterar köp; utvecklaren tar inte emot betalningsuppgifter. Se avsnitt 6 |
| `WAKE_LOCK`, `RECEIVE_BOOT_COMPLETED`, `FOREGROUND_SERVICE` | WorkManager (påminnelser) — **sammanslaget release-manifest** | Schemaläggning på enheten; ingen nätverks- eller molninsamling |
| (ingen `INTERNET` i slutlig manifest) | Medvetet borttagen | Stödjer påståendet att appen inte använder nätverk för egen datainsamling |

Ingen kamera, ingen bred lagringsbehörighet: export/import sker via **systemets filväljare** (Storage Access Framework).

---

## 6. Google Play Billing (Pro, engångsköp)

**Vad biblioteket gör:** `billing-ktx` i `:karna` ansluter till **Google Play** för engångsköp Pro (produkt-id `pro` i Flextid). Appen sparar bara en lokal **Pro ja/nej**-flagga efter lyckat köp. Pro tar bort gränsen på 15 dagposter; export, CSV och säkerhetskopia är gratis.

**Play Console — typisk tolkning:**

- Det är **inte** att utvecklaren säljer användardata.
- Markera **inte** att appen “samlar in” betalningsuppgifter: kort och betalning hanteras av Google Play.
- **Delning:** Data delas i betalningsflödet med **Google** som betalningsleverantör, inte med TM Connects server (finns ingen).

---

## 7. Barn, hälsa, plats

**Svar:** Appen riktar sig inte till barn. Ingen hälsodata, ingen plats, inga konton.

**Motivering:** Innehållet är vuxen flextidsregistrering utan medicinska påståenden.

---

## 8. Radering

**Svar:** Användaren raderar data genom att ta bort dagposter i appen, återställa från säkerhetskopia eller avinstallera appen.

**Motivering:** Ingen molnsynk; ingen kopia hos utvecklaren att radera.

---

## 9. Sammanfattning till formuläret

| Fråga | Rekommenderat svar |
|-------|---------------------|
| Samlar/delar utvecklaren personuppgifter? | **Nej** (allt är lokalt tills användaren exporterar själv) |
| Kryptering i transit för egen backend | **Ej tillämpligt** (ingen egen server) |
| Konto krävs | **Nej** |
| Barn riktade | **Nej** |

Uppdatera denna fil om manifest, beroenden eller beteende ändras i en framtida release.
