# Status

Uppdaterad 2026-10-08.

## Nu: Grundplattan

Målet är att flödet Claude → Cursor → lokal modell fungerar på en riktig uppgift innan Garantivalvet byggs. Tre spår körs parallellt eftersom de väntar på olika saker.

### Spår 1. Företag och Play-konto (Mattias)

Mattias sköter företaget, D-U-N-S-numret och Play Console som organisationskonto. Claude följer inte upp det. Play Console sköts bara av Mattias (direktivet punkt 9).

### Spår 2. Repo och flöde (Claude och Mattias)

1. Klart 2026-10-08: repot `appfabriken` finns, med direktiv, workflows och uppgiftsformatet.
2. Mattias lägger in hemligheten `CURSOR_API_KEY` i repot (samma som i heros-run). Valfritt: `DEBUG_KEYSTORE_BASE64`, så att nya APK:er går att installera över de gamla.
3. Cursor bygger grundplattan enligt `uppdrag/2026-10-08-grundplatta.md`: rotbygget, biblioteket `:karna` och ett skal av `:garantivalvet`.

### Spår 3. Lokal modell (Mattias dator)

Ett 3080 Ti har 12 GB VRAM, vilket ligger under det som brukar anges som bekvämt för agentkodning (16–24 GB). En MoE-modell som Qwen3-Coder 30B-A3B går att köra med experterna delvis i RAM, men långsammare. Därför byggs flödet för små uppgifter, och Cursor tar över efter två misslyckanden.

1. Installera Ollama och Aider. Aider gör en commit per ändring och kan köras med en färdig instruktion utan att någon sitter vid datorn.
2. Provkör två eller tre modeller på samma provuppgift i `:garantivalvet` och mät: blev testet grönt, hur lång tid tog det. Claude skriver provuppgiften.
3. Claude skriver körskriptet `verktyg/kor-uppgift.ps1`: hämta äldsta uppgift, skapa gren, kör Aider, kör testerna, pusha och öppna PR. Mattias startar det via Schemaläggaren i Windows med villkoret "när datorn är inaktiv", och det stoppas när datorn används igen.

Klart när: en uppgift har gått hela vägen från uppdrag till mergad kod utan att Mattias rört koden.

## Nästa: Garantivalvet 1.0

Omfång i IDEURVAL.md. Första uppdraget: datamodell och lista. Före regelmotorn kontrolleras fristerna mot konsumentköplagen (2022:260).

## Senare

- App 2: Husets underhållslogg.
- App 3: Mätarställning.
- Nattgranskning och veckoavstämning som schemalagda uppgifter, som i heros-run.
