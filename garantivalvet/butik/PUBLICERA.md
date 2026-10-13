# Publicera Garantivalvet i Google Play

Steg för Mattias. Det som kräver dig enligt CLAUDE.md punkt 9: allt i Play Console, applicationId och appnamn, och signeringsnyckeln.

## 1. Innan du bygger

- [ ] **applicationId och appnamn** är beslutade. Nu står `se.tmconnect.garantivalvet` och "Garantivalvet" / "Warranty Vault". Går inte att ändra efter första publiceringen.
- [ ] Du har provat senaste bygget på telefon (se `STATUS.md`, steg 3).
- [ ] Integritetspolicyn ligger på en publik sida (`butik/integritetspolicy-sv.md`, `-en.md`) och du har länken.
- [ ] `versionCode` och `versionName` i `garantivalvet/build.gradle.kts` är satta (nu 1 och 0.1.0). Varje nytt uppladdat bygge måste ha ett högre `versionCode`.

## 2. Bygg release

1. GitHub, fliken Actions, workflowet **Release**, Run workflow på `main`.
2. Workflowet kör testerna, bygger en krympt release, kontrollerar att APK:n inte begär `INTERNET` och lägger resultatet i releasen **release-test**:
   - `garantivalvet-release-test-<sha>.apk` — installera och prova på telefonen. Den är signerad med felsökningsnyckeln så att den går att installera över felsökningsbygget. Köpet av Pro går inte att prova här, bara i ett testspår i Play.
   - `garantivalvet-<sha>-osignerad.aab` — filen som går till Play.
   - `mapping-<sha>.txt` — ladda upp i Play Console (App-paketutforskaren, Avkoda) så blir kraschrapporter läsbara.
3. **Prova release-test-APK:n noga innan du går vidare** (kamera, avläsning, rotera, delning, säkerhetskopia, PDF, påminnelser, AI-delning). Koden är krympt, så fel som inte syns i felsökningsbygget kan dyka upp här.

## 3. Signera AAB:n

Play-nyckeln (uppladdningsnyckeln) finns bara hos dig, aldrig i repot eller i GitHub. Första gången skapar du en uppladdningsnyckel och aktiverar Play App Signing i Play Console. Signera sedan AAB:n på din dator:

```bash
keytool -genkeypair -v -keystore upload.jks -alias upload -keyalg RSA -keysize 2048 -validity 10000   # bara första gången
jarsigner -keystore upload.jks garantivalvet-<sha>-osignerad.aab upload
```

Spara `upload.jks` och lösenordet säkert (lösenordshanterare plus en kopia på annat ställe). Förlorar du den kan du begära en återställning av uppladdningsnyckeln hos Google, men det tar tid.

## 4. Play Console

1. Skapa appen: namn, språk (svenska), app, gratis.
2. **Butiksinformation**: texter och skärmbilder från `butik/sv/` och `butik/en/` (titel, kort beskrivning, lång beskrivning), `butik/skarmbilder/`, `butik/funktionsgrafik-*.png`, `butik/ikon-512.png`.
3. **Appinnehåll**: integritetspolicy (länken), Data safety enligt `butik/data-safety.md`, målgrupp (inte barn), annonser: nej, kategori (Verktyg eller Ekonomi).
4. **Engångsprodukt** med id `pro`, pris 49 kr.
5. **Testspår**: börja med intern testning, ladda upp den signerade AAB:n, lägg till dig själv som testare och prova köpet av Pro (kräver en riktig installation från Play, inte sidladdad APK).
6. Skicka till produktion när testspåret fungerar. Nya personkonton kan ha krav på ett slutet test (till exempel antal testare under en period); Play Console visar vad som gäller för ditt konto.

## 5. Krav som Claude bevakar

- `targetSdk` följer Google Plays krav (36 från och med 31 augusti 2026, enligt Googles sida om målnivå). Kontrolleras vid varje release.
- Appen begär inte `INTERNET` (kontrolleras av `IngenNatverkTest` och av workflowet Release).
- Inga annons-SDK:er, ingen analys.
