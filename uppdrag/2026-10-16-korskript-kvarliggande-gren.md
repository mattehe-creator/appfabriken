# Uppdrag: körskriptet klarar kvarliggande lokala grenar

## Bakgrund
Körskriptet `verktyg/kor-uppgift.ps1` kör `git checkout -q -b lokal/<uppgift>`. Finns grenen redan lokalt (rest från ett avbrutet eller underkänt försök, medan GitHub-grenen är raderad) ger det felkod 128 och kön stannar. Uppstod 2026-10-16 på uppgift 29. Mattias ska inte behöva rensa grenar för hand.

## Uppgift
Gör `verktyg/kor-uppgift.ps1` (och vid behov `kor-alla.ps1`) robust:
1. Kör `git fetch --prune origin` och utgå alltid från färsk `origin/main`.
2. Skapa uppgiftsgrenen med `git checkout -B lokal/<uppgift> origin/main` så att en kvarliggande lokal gren skrivs över. Innan dess: finns grenen lokalt med commits som inte finns på origin, spara den som `rescue/<uppgift>-<tidsstämpel>` (`git branch`), så inget arbete försvinner.
3. Skriv ut git-felets text i loggen i stället för att dölja den med `-q`, och markera uppgiften som fastnad (flytta den inte till `pagar/` om grenen inte kunde skapas).
4. Alla git-kommandon körs i repots rot, oavsett varifrån skriptet startas (`Set-Location` till skriptets föräldramapp).

## Klart när
- Skriptet startar en uppgift utan fel när `lokal/<uppgift>` redan finns lokalt.
- Skriptet fungerar när det startas från en annan mapp än repots rot.
- Ingen annan fil än skripten i `verktyg/` ändras. Lägg inte till beroenden.

## Gör inte
- Radera grenar på origin.
- Ändra uppgiftsformatet eller andra workflows.
- Skriv frågor under "Frågor till Claude" i PR-beskrivningen.
