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

1. Klart 2026-10-08: Ollama och Aider 0.86.2 är installerade, med `qwen3-coder:30b`. Provet i en tom mapp: modellen laddas på under 10 s, och en liten fil tar 5–7 s. Aider valde redigeringsformatet whole.
2. Provuppgiften ligger i `uppgifter/ny/2026-10-08-01-garantitid.md` (garantitid och status, ren Kotlin). Den körs först med `qwen3-coder:30b` och Aiders standardformat. Fler modeller eller OpenCode provas bara om resultatet är dåligt.
   Resultat 2026-10-08: godkänd och mergad (PR #2). Korrekt kod och alla nio exempel testade, 4,2 minuter. Aider committade inte de nya filerna; körskriptet committar nu sådant själv.
3. Körskriptet `verktyg/kor-uppgift.ps1` finns (2026-10-08). Första körningen gör Mattias för hand. När den fungerar läggs det i Schemaläggaren i Windows med villkoret "när datorn är inaktiv".
4. Cursors granskning av den lokala modellens PR:er automatiseras efter provkörningen. Tills dess granskar Claude dem.

Klart när: en uppgift har gått hela vägen från uppdrag till mergad kod utan att Mattias rört koden.

## Nästa: Garantivalvet 1.0

Omfång i IDEURVAL.md. Första uppdraget: datamodell och lista. Före regelmotorn kontrolleras fristerna mot konsumentköplagen (2022:260).

## Senare

- App 2: Husets underhållslogg.
- App 3: Mätarställning.
- Nattgranskning och veckoavstämning som schemalagda uppgifter, som i heros-run.
