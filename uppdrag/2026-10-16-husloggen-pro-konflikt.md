# Uppdrag: Husloggen Pro och gratisgräns, lös konflikt (del A, omtag)

## Bakgrund
PR #199 (cursor/2026-10-16-husloggen-pro-gratisgrans-del-a, sha be09e16) är granskad som grön men kan inte mergas: påminnelser del A (PR #197) är nu mergad till main och ändrade samma filer (HusViewModel.kt, HusViewModelFactory, MainActivity.kt, HusViewModelBilderTest.kt, build.gradle.kts). Uppdatera-gren ger konflikt.

## Uppgift
Gör om uppdraget uppdrag/2026-10-16-husloggen-pro-gratisgrans.md (del A) ovanpå nuvarande main. Behåll konstruktörsparametrarna för påminnelserna (paminnelseInstallningar, appContext) och lägg Pro-kopplingen till. Öppna en PR (gärna en enda). Delen med uppgifter (PR #200) ligger kvar och ska peka på den nya grenen.

## Klart när
- Bygget och alla tester i alla moduler är gröna.
- PR:en är mergebar mot main utan konflikt.

## Gör inte
- Ändra påminnelsekoden utöver vad konflikten kräver.
- Öppna flera PR:er med samma commit.
- Skriv frågor under "Frågor till Claude" i PR-beskrivningen.
