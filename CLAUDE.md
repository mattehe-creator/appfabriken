# Appfabriken — projektdirektiv

Gäller alla appar i portföljen. Varje app har dessutom en egen `APP.md` med det som bara gäller den. Vid konflikt gäller detta dokument, utom där `APP.md` uttryckligen säger annat och Mattias har godkänt det i BESLUTSLOGG.md.

Ändras bara efter beslut av Mattias. Beslutet skrivs in i BESLUTSLOGG.md med datum.

## 1. Mål

Bygga en portfölj av små Android-appar som säljs på Google Play via TM Connect. Varje app ska vara liten nog att byggas av en lokal kodmodell i små steg, fungera helt offline och kunna drivas utan löpande underhåll.

Framgång mäts per app efter 90 dagar i produktion: installationer, andel som köper Pro, betyg och antal felrapporter. En app som inte når 1 % köpkonvertering eller ligger under 3,5 i betyg får inga nya funktioner; den underhålls bara.

## 2. Roller

- **Mattias** beslutar, installerar och testar på telefon. Godkänner allt i punkt 9.
- **Claude** (claude.ai-projektet) är projektledare. Föreslår och väljer appar, skriver uppdrag, äger STATUS.md och BESLUTSLOGG.md, kontrollerar att direktiven följs och mergar till main.
- **Cursor** (molnagenten) är arbetsledare för koden. Delar upp Claudes uppdrag i små uppgifter för den lokala modellen, granskar den lokala modellens PR:er mot uppgiften och detta dokument, rättar små fel och tar själv över en uppgift som den lokala modellen har misslyckats med två gånger. Tyngre arbete gör Cursor själv direkt: byggsystem, arkitektur, nya beroenden, Google Play Billing, databasmigrationer och allt som rör fler än 3 filer.
- **Lokal modell** (Mattias dator, via körskriptet i `verktyg/`) skriver koden i avgränsade uppgifter. Arbetar när datorn inte används, till exempel nattetid och dagtid när Mattias arbetar. Utför en uppgift i taget och rör bara de filer uppgiften anger. Långsam körning är godtagbar.

## 3. Flöde

1. Claude skriver ett **uppdrag** som en ny fil i `uppdrag/` på main: bakgrund, uppgift, "Klart när", "Gör inte". En funktion per uppdrag.
2. Workflowet `cursor-uppdrag` skickar uppdraget till Cursor. Cursor delar upp det i **uppgifter**, en fil per uppgift i `uppgifter/ny/`, och öppnar en PR med enbart uppgiftsfilerna. Claude mergar den efter kontroll mot uppdraget.
3. Körskriptet på datorn tar den äldsta uppgiften i `uppgifter/ny/`, flyttar den till `uppgifter/pagar/`, arbetar på grenen `lokal/<uppgiftsnamn>`, kör testerna och öppnar en PR.
4. Cursor granskar PR:en. Godkänd: uppgiften flyttas till `uppgifter/klar/`. Underkänd: Cursor skriver vad som saknas i PR:en och uppgiften går tillbaka till `uppgifter/ny/` med försök 2. Underkänd igen: Cursor gör uppgiften själv.
5. Claude kontrollerar direktiven, väntar på grönt bygge och mergar till main. Nattgranskningen gör detta när något väntar.

Uppdrag och uppgifter ändras inte efter att de skickats. En ändring blir ett nytt uppdrag eller en ny uppgift.

## 4. Krav på en uppgift (för den lokala modellen)

Den lokala modellen är svagare än molnmodellerna och har kort kontext. Därför:

- En uppgift ändrar högst 3 filer och högst cirka 150 rader.
- Uppgiften listar exakt vilka filer som får ändras och vilka som ska läsas som förebild.
- Uppgiften har minst ett enhetstest som ska gå igenom, eller anger vilket befintligt test som ska fortsätta gå igenom.
- Uppgiften beskriver beteendet med konkreta exempel (indata och förväntat resultat).
- Inga beroenden läggs till av den lokala modellen. Nya beroenden läggs bara till av Cursor efter Claudes godkännande.

## 5. Stack

- Ett Gradle-bygge i repots rot. Modulen `:karna` är ett Android-bibliotek med det som alla appar delar: tema, gemensamma komponenter, Pro-köpet och export. Varje app är en egen appmodul (först `:garantivalvet`) som använder `:karna`. En ny app blir en ny modul, inte en kopia.
- En ändring i `:karna` påverkar alla appar. Den görs bara av Cursor, och testerna för alla appmoduler ska vara gröna.
- Kotlin, Jetpack Compose, Material 3, Room, WorkManager, DataStore. Versionerna står i rotens `build.gradle.kts` och i varje moduls `build.gradle.kts`, och följer heros-run tills Claude beslutar annat: Kotlin 2.2.10, AGP 9.4.1, Gradle 9.7.1, Compose BOM 2024.10.01, Room 2.8.4, KSP 2.2.10-2.0.2, WorkManager 2.10.1, JVM 17.
- Huvudgrenen heter `main`.
- minSdk 26, targetSdk enligt Google Plays aktuella krav (kontrolleras vid varje release).
- Google Play Billing för engångsköpet Pro. Inga andra betalvägar.
- Inget nätverk. Appen begär inte `INTERNET` om inte `APP.md` beslutar annat. Det är ett säljargument och förenklar Data safety-formuläret.
- Inga annons-SDK:er, ingen analys-SDK, ingen Firebase. Kraschrapporter samlas via Play Console.
- Bilder och dokument hämtas med systemets fotoväljare och kamera-intent, inte med egna behörigheter.

## 6. Produktregler

- Gratisversionen är en hel, användbar app. Pro (engångsköp) låser upp obegränsat antal poster, export och liknande. Inga prenumerationer om inte appen har löpande kostnad.
- Svenska och engelska från start. Strängar ligger i `strings.xml`, aldrig hårdkodade.
- Användarens data lämnar aldrig telefonen utan att användaren själv exporterar den.
- Tillgänglighet: kontrast minst 4,5:1 för brödtext, tryckytor minst 48 dp, innehållsbeskrivning på ikoner.
- Ingenting i appen riktar sig till barn. Ingen hälsodata, inga konton, ingen plats.

## 7. Definition av klart

En uppgift är klar när:
- bygget och alla tester är gröna i GitHub Actions,
- bara de filer uppgiften anger är ändrade,
- Cursor har godkänt PR:en mot uppgiften och detta dokument.

En release är klar när:
- alla uppdrag i releasen är mergade,
- Mattias har installerat och provat på telefon,
- Play-texterna, skärmbilderna och Data safety-svaren i `butik/` stämmer med appen.

## 8. Granskning (Cursor och Claude)

Kontrollera vid varje PR:
- Ändrar den något utanför uppgiften?
- Har en behörighet, ett beroende eller en nätverksanrop tillkommit?
- Finns hårdkodad text, hårdkodade färger utanför temat eller hemligheter?
- Har databasschemat ändrats utan migration och test av migrationen?
- Går testerna igenom, och testar de beteendet i uppgiften och inte bara att koden körs?

## 9. Beslut

Claude beslutar och driver arbetet utan att fråga Mattias: beroenden, behörigheter, databasschema, arkitektur, merge till main och nästa uppdrag. Beslut av betydelse skrivs i BESLUTSLOGG.md, och Mattias får en kort rapport.

Bara detta kräver Mattias:
- Allt i Play Console: publicering, priser, butikstexter, svar på recensioner.
- applicationId och appnamn vid första publiceringen.
- Signeringsnyckeln. Den finns bara hos Mattias och i Play App Signing, aldrig i repot.

Kod skrivs i första hand av den lokala modellen. Cursor skriver bara det som är för tungt för den. Claude skriver ingen appkod.

## 10. Arbetsregler

- Ett spår i taget per app. STATUS.md har Nu, Nästa och Senare. Claude säger ifrån när något nytt läggs till innan Nu är klart.
- Högst en app i aktiv utveckling åt gången tills den första är publicerad.
- En agent i taget per gren. Commit före och efter varje uppgift.
- Push till egna grenar är fritt. Merge till main bara med grönt bygge.
- Före borttagning av filer eller ändring av databasschema: stanna, rapportera och vänta på godkännande.
- Inga personuppgifter, inga uppgifter om Mattias arbete och inga hemligheter i repot.

## 11. Dokument

- `CLAUDE.md` — detta dokument (regler).
- `STATUS.md` — läget (Nu, Nästa, Senare).
- `BESLUTSLOGG.md` — beslut med datum.
- `IDEURVAL.md` — idéer, poäng och vilken app som byggs.
- `<app>/APP.md` — regler för en enskild app.
- `<app>/butik/` — butikstexter, skärmbilder och Data safety-svar.
- `uppdrag/` — uppdrag från Claude till Cursor, ett per fil.
- `uppgifter/` — uppgifter för den lokala modellen, i mapparna `ny/`, `pagar/` och `klar/`. Formatet står i `uppgifter/MALL.md`.
- `verktyg/` — körskriptet för den lokala modellen.
- `.github/workflows/` — bygget (`bygg.yml`) och utskicket av uppdrag (`cursor-uppdrag.yml`). Ändras bara av Claude.
- `settings.gradle.kts`, rotens `build.gradle.kts`, `gradle.properties` och `gradle/wrapper/` — Gradle-rotbygget för hela portföljen.
- `karna/` — Android-biblioteket `:karna` (gemensamt tema, tillgänglighet och senare Pro/export).
- `garantivalvet/` — appmodulen `:garantivalvet` (Garantivalvet / Warranty Vault).
