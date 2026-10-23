# Uppdrag: Flextid, automatisk säkerhetskopia

## Bakgrund

Flextid (app 3) har manuell säkerhetskopia och återställning som JSON med versionsnummer (flextid/APP.md). Husloggen har dessutom automatisk säkerhetskopia (uppdrag 2026-10-16-husloggen-sakerhetskopia), där mönstret lyfts till `:karna`. Den som stämplar varje dag tappar sin saldohistorik om telefonen går förlorad, så Flextid ska få samma automatiska kopia.

## Uppgift

Cursor delar upp arbetet i små uppgifter för den lokala modellen (uppgifter/MALL.md, högst 3 filer och cirka 150 rader per uppgift) och gör själv det som är för tungt (WorkManager, filväljare, kopplingen mot `:karna`).

1. Kontrollera först om Flextid redan har automatisk säkerhetskopia. Finns den, skriv det under "Frågor till Claude" i PR-beskrivningen och gör inget mer.
2. Återanvänd det som Husloggen lyfte till `:karna` (planerare, mapp vald via systemets filväljare, rotation av äldre kopior). Lägg inget eget i Flextid som redan finns i `:karna`.
3. Inställning under Inställningar: av/på, vald mapp, intervall (dagligen eller veckovis), senaste lyckade kopia. Standard: av.
4. Strängar på svenska och engelska i `strings.xml`. Ingen ny behörighet, inget nätverk, inget nytt beroende, ingen ändring av databasschemat.

## Klart när

- Enhetstester för planeringen (när nästa kopia förfaller, rotation av äldre kopior) med konkreta datum.
- Återställning från en automatisk kopia fungerar med samma JSON som manuell export.
- `gradle :flextid:test` och bygget är gröna, och testerna för övriga appmoduler är gröna om `:karna` rörs.

## Gör inte

- Ändra databasschemat, Garantivalvet eller Husloggen.
- Ändra `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md`.
- Kräva svar från Mattias. Frågor skrivs under "Frågor till Claude" i PR-beskrivningen.
