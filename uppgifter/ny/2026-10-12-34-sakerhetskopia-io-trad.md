# Uppgift: säkerhetskopians filarbete utanför huvudtråden

uppdrag: uppdrag/2026-10-11-export-sakerhetskopia.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopViewModel.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/export/Sakerhetskopia.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/data/KopRepository.kt

## Uppgift
`sparaSakerhetskopia` och `lasInSakerhetskopia` i `KopViewModel` startas med `viewModelScope.launch`, som kör på huvudtråden. Där skrivs och läses hela ZIP-filen med alla kvittobilder. Med många kvitton fryser appen (risk för "appen svarar inte").

1. Lägg till `import kotlinx.coroutines.Dispatchers` och `import kotlinx.coroutines.withContext`.
2. I `sparaSakerhetskopia`: lägg `openOutputStream` och `Sakerhetskopia.skriv(...)` i `withContext(Dispatchers.IO) { ... }`. `allaKopForExport()` får ligga kvar före.
3. I `lasInSakerhetskopia`: lägg `openInputStream` och `Sakerhetskopia.lasIn(stream)` i `withContext(Dispatchers.IO) { ... }` och låt blocket returnera resultatet. Gör sedan `when` på resultatet utanför blocket, som i dag.
4. I `importeraSakerhetskopia`: lägg anropet `repository.importeraSakerhetskopia(...)` i `withContext(Dispatchers.IO) { ... }`.
5. Allt som sätter `_sakerhetskopiaMeddelande.value` och anropet `PaminnelsePlanerare.synka` ligger kvar utanför `withContext`.
6. Felhanteringen (`try`/`catch` och vilka meddelanden som visas) ändras inte.

## Exempel
- Före: `outputStream.use { Sakerhetskopia.skriv(it, kop, kvittoLager) }` direkt i `launch`. Efter: samma rad inuti `withContext(Dispatchers.IO) { ... }`.
- En trasig fil ger fortfarande meddelandet `KundeInteLasas`, en nyare version fortfarande `NyareVersion`.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest

## Gör inte
- Ändra andra filer eller andra funktioner i `KopViewModel`.
- Lägga till beroenden (`kotlinx.coroutines` finns redan).
- Ändra strängar, databasschemat eller felmeddelandena.
