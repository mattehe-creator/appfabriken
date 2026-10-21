# Uppgift: Dubbla namn och utgången nyttjandeperiod

uppdrag: uppdrag/2026-10-20-styrelserummet-robusthet-handlingar.md
försök: 2
beror-på: 2026-10-20-styrelserummet-robusthet-16-anmarkning-efter-arsslut-f2.md

## Vad som saknades
- Grenen innehöll bara commit «Uppgift startad» (flytt till `pagar/`). `Anmarkningar.kt` och `AnmarkningarTest.kt` ändrades inte.
- Ingen anmärkning för dubbla komponentnamn eller för utgången nyttjandeperiod med bokfört värde över restvärdet.
- Testet `anmarkningDubblaNamnOchNyttjande` saknas.

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/Anmarkningar.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/AnmarkningarTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/BokfortVarde.kt

## Uppgift
Utöka `anmarkningarFor(utkast, tabell, arsslut)` (signaturen finns redan med `arsslut`):

1. **Dubbla namn:** Innan eller efter befintliga komponentloopar: gruppera `utkast.komponenter` efter `namn` (case-sensitive). För varje namn som förekommer **två gånger eller fler**, lägg till **en** svensk anmärkning som tydligt gäller dubbla namn och nämner namnet (samma stil som övriga strängar i filen, t.ex. `Komponenten "Tak" …`). `"Tak"` och `"tak"` ska **inte** räknas som samma namn.
2. **Utgången nyttjandeperiod:** I komponentloopen (eller motsvarande): om `komponent.anskaffningsar + komponent.nyttjandeperiodAr <= arsslut` **och** `bokfortVarde(komponent, arsslut) > komponent.restvarde`, lägg till en anmärkning per berörd komponent som förklarar att nyttjandeperioden gått ut men bokfört värde fortfarande överstiger restvärdet. Importera och använd `bokfortVarde` från `se.tmconnect.styrelserummet.regler`.

Lämna befintliga regler oförändrade. Avsluta med `distinct()` som idag.

## Exempel
- Två komponenter med namnet `"Tak"` (valfria grupper/värden) → minst en anmärkning om dubbla namn som nämner `Tak`.
- En komponent där nyttjandeperioden enligt formeln gått ut vid `arsslut = 2025` och `bokfortVarde(komponent, 2025) > komponent.restvarde` (t.ex. enligt uppgiftens scenario: period slut 2020, bokfört värde 100_000 kr, restvärde 0) → anmärkning om utgången period med kvarvarande bokfört värde.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*AnmarkningarTest.anmarkningDubblaNamnOchNyttjande"

Lägg till `@Test fun anmarkningDubblaNamnOchNyttjande()` i `AnmarkningarTest` (JUnit 4, `org.junit`):

1. Bygg ett `Utkast` med **två** `Komponent`-rader med exakt samma `namn = "Tak"` (övriga fält giltiga). Anropa `anmarkningarFor(..., emptyList(), arsslut = 2025)`. Assert att listan innehåller en anmärkning om dubbla namn med `Tak`.
2. Bygg ett `Utkast` med **en** komponent vars fält ger `anskaffningsar + nyttjandeperiodAr <= 2025` och `bokfortVarde(komponent, 2025) > restvarde` (räkna gärna ut med `bokfortVarde` i testet). Assert att anmärkningen nämner komponentens namn och att nyttjandeperioden gått ut / bokfört värde över restvärdet (delsträngar räcker om de matchar din formulering i `Anmarkningar.kt`).
3. Kontrollfall: två komponenter med namn `"Tak"` respektive `"tak"` ska **inte** ge dubbla-namn-anmärkning.

Befintliga tester i `AnmarkningarTest` ska fortsatt vara gröna.

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
