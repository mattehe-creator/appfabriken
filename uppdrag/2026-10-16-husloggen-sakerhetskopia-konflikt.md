# Uppdrag: Husloggen säkerhetskopia, lös konflikt (del A, omtag)

## Bakgrund
PR #202 (cursor/2026-10-16-husloggen-sakerhetskopia, sha f0e1a0e) har grönt bygge men kan inte mergas: Pro/gratisgräns (#199, #203-#207) och påminnelsereglaget är nu mergade till main och ändrade samma filer (MainActivity.kt, HusViewModel.kt, HusSkarmar.kt, HusRepository.kt, strings.xml i husloggen). Uppdatera-gren ger merge-konflikt.

## Uppgift
Gör om uppdraget uppdrag/2026-10-16-husloggen-sakerhetskopia.md (del A) ovanpå nuvarande main. Behåll Pro-kopplingen, gratisgränsen och påminnelsekoden som de är på main, och lägg säkerhetskopia, import och automatisk kopia till. Öppna en PR (gärna en enda). Uppgifterna (del B) ska peka på den nya grenen.

## Klart när
- Bygget och alla tester i alla moduler är gröna.
- PR:en är mergebar mot main utan konflikt.
- Garantivalvet är orört.

## Gör inte
- Ändra Pro-, gräns- eller påminnelsekoden utöver vad konflikten kräver.
- Öppna flera PR:er med samma commit.
- Skriv frågor under "Frågor till Claude" i PR-beskrivningen.
