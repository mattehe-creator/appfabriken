# Provkörning av Styrelserummet på din egen dator

Mål: ladda upp en riktig (eller påhittad) underhållsplan och årsredovisning och se utkastet, utan värd och utan domän. Allt körs lokalt. Behöver du inte ha tid för det idag: hoppa över, ingenting annat väntar på det.

## Det du behöver

1. **Java 17 eller nyare** (samma som Android-bygget; har du redan byggt appen lokalt finns det).
2. **En Anthropic API-nyckel** från console.anthropic.com. Lägg en låg utgiftsgräns (till exempel 100 kr) i konsolen. Nyckeln skrivs aldrig i en fil i repot.
3. **Repot** (`mattehe-creator/appfabriken`) uppdaterat till senaste `main`.
4. **PDF-filer med markerbar text.** Skannade filer läses inte. Prova gärna först med påhittade filer (se längst ned) och sedan en riktig underhållsplan.

## Starta (PowerShell, i repots rotmapp)

```powershell
$env:STYRELSERUMMET_DATA = "$env:TEMP\styrelserummet-data"
New-Item -ItemType Directory -Force $env:STYRELSERUMMET_DATA | Out-Null
$env:ANTHROPIC_API_KEY = Read-Host "Klistra in nyckeln"   # visas på skärmen; stäng fönstret efteråt
.\gradlew.bat :styrelserummet-server:run
```

Öppna sedan `http://localhost:8080/`. Servern skriver varningar i fönstret om något saknas (nyckel, katalog, port). Stäng med `Ctrl+C`. Stäng PowerShell-fönstret när du är klar, så försvinner nyckeln ur miljön.

## Vad du ska titta efter

- **Läser den rätt?** Stämmer komponenterna med planen: namn, år, belopp? Rader märkta *uppskattning* eller *låg säkerhet* är sådant tjänsten gissat; kolla dem först.
- **Stämmer summan?** Avstämningen mot byggnadens värde ska vara nära. En stor differens betyder oftast att en post saknas eller att en tusental-enhet lästs fel.
- **Går det att rätta?** Ändra en rad och se att summor och avskrivningar räknas om.
- **Är texten begriplig för en styrelse?** Det är den viktigaste frågan.
- **Fick du ett fel?** Skriv ned felmeddelandet och vad du laddade upp (inte själva filen).

## Vad som lämnar datorn

Bara maskad text (personnummer, e-post och telefonnummer ersätts) skickas till Anthropics API för tolkningen. PDF-filerna lämnar inte datorn. Handlingarna i en riktig förening kan ändå innehålla annat som du inte vill dela: ta bort sidor du inte vill skicka, eller börja med en påhittad fil.

## Efteråt

Skicka tillbaka (i chatten) vad som blev bra och vad som blev fel, gärna utan filinnehåll. Jag gör uppdrag av det. Radera provdatat: `Remove-Item -Recurse $env:STYRELSERUMMET_DATA`.

## Driftsättning

När du vill lägga tjänsten hos en värd: se `DRIFT.md` och frågorna i `FRAGOR.md` (bolagsuppgifter, värd, nyckel, betalkonto).
