# Uppgift: Helhetstest för lyckat flöde

uppdrag: uppdrag/2026-10-21-styrelserummet-felhantering.md
försök: 1
beror-på: 2026-10-21-styrelserummet-felhantering-07-falsk-tolkare-spion-f2.md

## Ändra bara dessa filer
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/ApiTest.kt

## Läs som förebild (ändra inte)
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/CsvExport.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/MaskaPersonuppgifter.kt

## Uppgift
Lägg till testet `helhetFlodeMedTreKomponenterOchMaskning` i `ApiTest`:

1. Skapa en liten PDF i testet med text som innehåller ett personnummer (t.ex. `19800101-1234`) och underhållspost(er).
2. Konfigurera `FalskTolkare` att returnera giltigt JSON med **tre** komponenter (TAK, FASAD, FÖNSTER eller liknande) med anskaffningsvärden som summerar till ett känt belopp.
3. `POST /api/jobb` med plan-PDF och årsredovisnings-PDF (byggnads värde så avstämning kan slås), `synkronJobb = true`.
4. Efter `GET /api/jobb/{id}`: status `KLAR`, utkast/CSV märkt med utkast (`UTKAST`), **tre** komponentrader i svaret.
5. Assert att `FalskTolkare.senasteUtdrag` **inte** innehåller personnumret (maskning före AI).
6. `GET /api/jobb/{id}.csv`: kontrollera kolumnrubriker enligt `tillCsv` (Komponent, Grupp, …) och att summan av anskaffningsvärden i CSV stämmer med förväntat belopp.

## Exempel
- Tre komponeter à 500000, 300000, 200000 → CSV innehåller tre datarader plus rubrik och utkastrad; summa 1 000 000.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*ApiTest.helhetFlodeMedTreKomponenterOchMaskning"

## Gör inte
- Ändra produktionskod.
- Lägga till beroenden.
