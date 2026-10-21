# Uppdrag: Flextid — gå hem-tid och semesterår

Skrivet av Claude 2026-10-21. Uppgifterna skrivs direkt av Claude i `uppgifter/ny/`. Claude kopplar in dem i skärmarna efteråt.

## Bakgrund

Det användare av flextidsappar oftast vill veta under dagen är när de kan gå hem: när normaltiden är fylld, och när saldot blir noll. Det andra är hur många semesterdagar som är tagna och kvar under semesteråret. Båda är rena beräkningar på det som redan finns i appen.

## Uppgift

Rena funktioner i `flextid/src/main/kotlin/se/tmconnect/flextid/regler/`, med enhetstester:

1. `gaHemTid`: start + rast + normaltid.
2. `gaHemTidForNollsaldo`: som ovan, justerat med saldot.
3. Semesterår: vilket semesterår ett datum hör till (startmånad inställbar, standard april enligt semesterlagen).
4. Räkna dagar av en viss typ inom ett intervall.
5. Semestersammanfattning: uttagna och kvar.

## Klart när

Alla uppgifter mergade med gröna tester.

## Gör inte

- Inga avtalsregler, ingen rådgivning. Antalet semesterdagar per år anger användaren (standard 25).
- Inget nätverk, inga nya beroenden, inget databasschema.
