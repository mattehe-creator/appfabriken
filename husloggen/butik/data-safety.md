# Data safety — Husloggen (Play Console)

Svar att fylla i i Google Play Console, med motivering utifrån manifestet för `:husloggen`, modulberoenden (`:karna`) och appens beteende. Skrivet av Claude 2026-10-21. Appnamnet i butiken är preliminärt.

## Samlad bild

| Källa | Vad den innebär |
|-------|-----------------|
| `husloggen/src/main/AndroidManifest.xml` | `POST_NOTIFICATIONS` för lokala påminnelser. `INTERNET` och `ACCESS_NETWORK_STATE` **tas bort** (`tools:node="remove"`). |
| `:karna` / Play Billing | Google Play Billing Library använder Play Store på enheten för engångsköpet Pro. Ingen egen server finns. |
| Appdata | Bostäder, uppgifter, loggposter, bilder, dokument, mätare och inställningar lagras lokalt (Room, DataStore, appens privata filer). Export och säkerhetskopia sker **endast** när användaren själv sparar en fil eller väljer en mapp för automatisk säkerhetskopia. |

---

## 1. Samlar appen in eller delar användar- eller enhetsdata?

**Svar: Nej.**

**Motivering:** Ingen backend, ingen analys-SDK, ingen annons-SDK och ingen internetbehörighet. Allt användaren matar in stannar på enheten tills användaren exporterar eller raderar appen. Utvecklaren får ingen kopia.

Köpet av Pro går genom Google Play. Det är Googles betalflöde och räknas inte som att TM Connect samlar in data (avsnitt 6).

---

## 2. Kryptering

**Svar:** Ej tillämpligt för överföring (ingen överföring). Lokal lagring skyddas av Androids standardkryptering av enheten. Säkerhetskopian som användaren sparar är inte extra krypterad av appen.

---

## 3. Konton

**Svar:** Inget konto, ingen inloggning.

---

## 4. Data som lagras på enheten

Markeras **inte** som insamlad, eftersom den aldrig lämnar enheten till utvecklaren.

| Data | Syfte (lokalt) | Delas med utvecklaren |
|------|----------------|------------------------|
| Bostäder (namn, valfri adress, anteckning) | Hålla isär flera bostäder | Nej |
| Uppgifter (namn, intervall, datum) | Påminnelser och nästa datum | Nej |
| Loggposter (datum, belopp, typ, rot/rut-avdrag, anteckning) | Logg, sammanställning, PDF, CSV | Nej |
| Bilder och kvitton | Visas på posten | Nej |
| Dokument (manualer, ritningar m.m.) | Visas i appen | Nej |
| Mätare och avläsningar | Förbrukning | Nej |
| Planerade kostnader | Översikt framåt | Nej |
| Inställningar (påminnelser, säkerhetskopia, tema) | Appens funktion | Nej |
| Pro-status | Tar bort gränserna | Nej (verifieras via Play) |

---

## 5. Behörigheter

| Behörighet | Varför | Data safety |
|------------|--------|-------------|
| `POST_NOTIFICATIONS` | Lokala påminnelser före nästa datum | Skapas på enheten |
| `com.android.vending.BILLING` | Från Play Billing Library i `:karna` | Avsnitt 6 |
| `WAKE_LOCK`, `RECEIVE_BOOT_COMPLETED`, `FOREGROUND_SERVICE` | WorkManager (påminnelser, automatisk säkerhetskopia), via sammanslaget manifest | Schemaläggning på enheten |
| (ingen `INTERNET`) | Medvetet borttagen | Appen kan inte skicka något själv |

Ingen kamera- eller lagringsbehörighet: bilder via systemets fotoväljare och kamera, filer via systemets filväljare (Storage Access Framework). Ingen platsbehörighet.

---

## 6. Google Play Billing (Pro)

Produkt-id `pro`, engångsköp. Appen sparar bara en lokal flagga efter lyckat köp. Betalningsuppgifter hanteras av Google Play och når aldrig TM Connect. Markera inte betalningsuppgifter som insamlade av utvecklaren.

---

## 7. Barn, hälsa, plats

Appen riktar sig inte till barn. Ingen hälsodata, ingen plats, inga konton. Målgrupp: vuxna bostadsägare.

---

## 8. Radering

Användaren tar bort poster i appen eller avinstallerar appen. Ingen kopia finns hos utvecklaren. Play kräver en länk för radering bara för appar med konton; Husloggen har inga.

---

## 9. Sammanfattning till formuläret

| Fråga | Svar |
|-------|------|
| Samlar eller delar appen användardata? | **Nej** |
| Kryptering vid överföring | Ej tillämpligt |
| Konto krävs | **Nej** |
| Riktad till barn | **Nej** |
| Integritetspolicy | `https://mattehe-creator.github.io/appfabriken/husloggen/integritet/` (när GitHub Pages är påslaget) |

Uppdatera filen om manifest, beroenden eller beteende ändras.
