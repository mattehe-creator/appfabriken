# Uppdrag: tillsynen sparar inte "försök: 2" när uppgiften återställs

## Bakgrund
PR #219 (tillsyn i `verktyg/kor-alla.ps1`) är mergad. Efterhandsgranskningen hittade ett fel i `Invoke-MisslyckatForsok`: funktionen läser uppgiftsfilen från `uppgifter/pagar/` medan den står på grenen `lokal/<uppgift>`, checkar sedan ut `main`, tar bort grenen och försöker `Move-Item` från den sökväg som inte längre finns (flytten till `pagar/` gjordes bara på den borttagna grenen). På `main` ligger filen redan kvar i `uppgifter/ny/`. `Set-Forsok` skriver då `försök: 2` i en ocommittad ändring på `main`, som nästa körning av `kor-uppgift` lägger undan i git stash. Försöksräknaren sparas alltså aldrig, och en uppgift som saknar kod-commits körs om med försök 1 om och om igen.

## Uppgift
Gör `Invoke-MisslyckatForsok` i `verktyg/kor-alla.ps1` korrekt:
1. Efter `git checkout main` (och borttagen gren): läs `försök` ur filen i `uppgifter/ny/<uppgift>.md` på main.
2. Försök 1: sätt `försök: 2` i filen, `git add`, commit ("Uppgift tillbaka: <uppgift> (försök 2)") och `git push origin main`. Misslyckas pushen: logga och lämna ändringen ocommittad som i dag.
3. Försök 2: skriv raden i `granskning/lokal-stopp.md` som i dag och lämna uppgiften i `uppgifter/ny/` oförändrad (Cursor tar över enligt ARBETSFLODE.md).
4. Ta bort anropen av `Move-Item` som pekar på en sökväg som inte finns.

## Klart när
- Läsning av koden visar att `försök: 2` committas och pushas på main i första misslyckandet.
- Ingen annan fil än `verktyg/kor-alla.ps1` ändras. Inga beroenden.

## Gör inte
- Ändra uppgiftsformatet, workflows eller appkod.
- Ändra övervakningens tidsgränser.
- Radera grenar på origin.
- Skriv frågor under "Frågor till Claude" i PR-beskrivningen.
