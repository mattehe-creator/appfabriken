# Uppdrag: körskriptet, JAVA_HOME i kor-alla och okänd felkälla

## Bakgrund
PR 234 (full återställning) är mergad. Första stoppet efter den (`granskning/lokal-stopp-logg.md`, 2026-10-17 16:46) visar två fel:

1. `Full återställning` kör `gradlew --stop` i `kor-alla`, men där är `JAVA_HOME` inte satt ("JAVA_HOME is not set and no 'java' command could be found", därefter "JAVA_HOME is set to an invalid directory: \"= 1>&2"). `kor-uppgift` sätter JAVA_HOME från Android Studios JDK (`C:\Program Files\Android\Android Studio\jbr`), men `kor-alla` gör det inte. Alltså stoppas ingen Gradle-daemon vid återställningen, och återställningen är inte full. Felet fanns redan i `Stop-Gradle` före PR 234.
2. `kor-uppgift` avslutade med exit 1 efter 30 sekunder, men `senaste-fel.json` saknade orsak, så källan blev "okänd". Alltså kom exit 1 från en väg som inte går via `Avbryt`, till exempel ett oväntat undantag under `$ErrorActionPreference = "Stop"`. Loggraderna från `aider-senaste.log` i stoppfilen är från en tidigare körning (BUILD SUCCESSFUL), så de förklarar ingenting.

## Uppgift
Ändra bara `verktyg/kor-alla.ps1` och `verktyg/kor-uppgift.ps1`:
1. **JAVA_HOME i kor-alla:** sätt `JAVA_HOME` överst i `kor-alla` med samma logik som i `kor-uppgift` (befintligt värde, annars Android Studios `jbr`; saknas båda: logga det tydligt men fortsätt). Rätta även anropet så att `gradlew --stop` körs utan konstig citering eller omdirigering (`"= 1>&2` tyder på att en `2>&1`-sträng hamnat i miljövariabeln). Logga om `--stop` lyckades.
2. **Fånga alla undantag i kor-uppgift:** lägg en `trap` eller ett yttre `try/catch` som vid varje oväntat fel skriver `Skriv-Felsignal` med källa "skript" och undantagets meddelande och rad, innan `exit 1`. Ingen väg med exit 1 får lämna källan "okänd".
3. **Rätt loggunderlag i stoppfilen:** `Skriv-LokalStopp` ska ta de sista 40 raderna av den senaste `kor-<tidsstämpel>.log` (kor-uppgifts transcript) i `%LOCALAPPDATA%\appfabriken\logg\`, utöver `kor-alla.log`, och bara ta med `aider-senaste.log` om den ändrats efter att körningen startade. Maska som idag.
4. **Städa `senaste-fel.json`** vid start av varje `kor-uppgift`, så att ett gammalt fel inte tolkas som nytt.

## Klart när
- Provkörning av `kor-alla` utan `JAVA_HOME` i miljön: `gradlew --stop` lyckas och loggas.
- Ett medvetet undantag i `kor-uppgift` (till exempel en felaktig uppgiftsfil) ger en felkälla som inte är "okänd" och med undantagets text i `lokal-stopp-logg.md`.
- Ingen annan fil ändras. Inga beroenden.

## Gör inte
- Ändra uppgiftsformatet, workflows eller appkod.
- Radera grenar på origin.
