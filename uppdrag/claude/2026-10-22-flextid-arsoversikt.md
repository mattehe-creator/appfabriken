# Uppdrag: Årsöversikt i Flextid

## Bakgrund
Användare vill se hur flexsaldot har utvecklats under året, månad för månad, och kunna lämna det till chefen eller lönekontoret.

## Uppgift
Ren logik i `flextid/regler/`: en rad per månad med arbetad tid, utfall och saldo vid månadens slut, samt CSV av raderna. Claude kopplar in i appen.

## Klart när
- `arsOversikt` och `arsOversiktCsv` finns med tester enligt uppgifterna.

## Gör inte
- Ändra UI, databas eller beroenden.
