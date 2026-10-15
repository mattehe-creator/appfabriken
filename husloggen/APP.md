# Husloggen (arbetsnamn)

Regler för appen utöver CLAUDE.md. Val och omfång i IDEURVAL.md (2026-10-16). Modul `:husloggen`, paket `se.tmconnect.husloggen`. Appnamn och applicationId beslutar Mattias vid första publiceringen (CLAUDE.md punkt 9).

## Vad appen gör

Håller ordning på husets underhåll: återkommande uppgifter med påminnelser, och en logg över utfört arbete och utgifter med kvitton. Loggen är underlag när bostaden säljs.

## Begrepp

- **Uppgift**: något som återkommer, med intervall i månader (1–120). Nästa datum är senast gjord plus intervallet, eller startdatumet om uppgiften aldrig gjorts.
- **Post**: en rad i loggen, det vill säga utfört arbete eller en utgift. Typ: underhåll, reparation, eller ny-, till- eller ombyggnad. En post kan höra till en uppgift.

## Regler

- Gratisgränsen är 15 poster. Uppgifter och påminnelser är obegränsade. "Markera som gjord" flyttar alltid nästa datum; när gränsen är nådd sparas ingen ny post utan Pro. Gränsen ligger på ett ställe i `regler/`.
- Belopp lagras i öre (`Long`), datum som `LocalDate`, flera bildfiler som kommaseparerade filnamn, typer som enum-namn. Som i Garantivalvet.
- Skatteregler (förbättringsutgifter vid försäljning) visas som information, aldrig som råd, med lagrum, och bara för Sverige. Siffrorna ligger på ett ställe i `regler/` och kontrolleras mot Skatteverket och inkomstskattelagen (45 kap. för fastighet, 46 kap. för bostadsrätt) innan uppdraget skrivs. I andra länder visar appen bara summor per år och typ.
- Färdiga förslag på uppgifter har intervall som användaren kan ändra. För sådant som kommunen eller tillverkaren bestämmer (sotning, brandskyddskontroll, service) står "enligt kommunen" eller "enligt tillverkaren", aldrig ett lagkrav.
- Ingen adress, plats eller karta. Appen behöver inte veta var huset ligger.
- Garantivalvets kod rörs inte från Husloggens uppdrag. Delar som båda behöver lyfts till `:karna` av Cursor (BESLUTSLOGG.md 2026-10-16).

## Inte i 1.0

Flera bostäder, mätarställningar, dokument (manualer, ritningar), plan för framtida kostnader, delning, widgets.
