# Husloggen (arbetsnamn)

Regler för appen utöver CLAUDE.md. Val och omfång i IDEURVAL.md (2026-10-16). Modul `:husloggen`, paket `se.tmconnect.husloggen`. Appnamn och applicationId beslutar Mattias vid första publiceringen (CLAUDE.md punkt 9).

## Vad appen gör

Håller ordning på husets underhåll: återkommande uppgifter med påminnelser, och en logg över utfört arbete och utgifter med kvitton. Loggen är underlag när bostaden säljs.

## Begrepp

- **Uppgift**: något som återkommer, med intervall i månader (1–120). Nästa datum är senast gjord plus intervallet, eller startdatumet om uppgiften aldrig gjorts.
- **Post**: en rad i loggen, det vill säga utfört arbete eller en utgift. Typ: underhåll, reparation, eller ny-, till- eller ombyggnad. En post kan höra till en uppgift.

## Regler

- Gratisgränsen är 15 poster. Uppgifter, påminnelser, mätare med avläsningar och dokument (manualer, ritningar m.m.) är obegränsade. "Markera som gjord" flyttar alltid nästa datum; när gränsen är nådd sparas ingen ny post utan Pro. Gränsen ligger på ett ställe i `regler/`.
- Belopp lagras i öre (`Long`), datum som `LocalDate`, flera bildfiler som kommaseparerade filnamn, typer som enum-namn. Som i Garantivalvet.
- Skatteregler (förbättringsutgifter vid försäljning) visas som information, aldrig som råd, med lagrum, och bara för Sverige. Siffrorna ligger på ett ställe i `regler/` och kontrolleras mot Skatteverket och inkomstskattelagen (45 kap. för fastighet, 46 kap. för bostadsrätt) innan uppdraget skrivs. I andra länder visar appen bara summor per år och typ.

## Förbättringsutgifter och rot/rut (verifierat 2026-10-17)

Vid vinstberäkning vid försäljning räknas bara den del av utgiften som ägaren själv haft — alltså fakturabeloppet minus den skattereduktion (rot eller rut) som redan beviljats för samma arbete. Dubbel avdrag medges inte.

**Källor (kontrollerade 2026-10-17):**

- Skatteverket, [Avdrag för renoveringar och nybyggnad](https://www.skatteverket.se/privat/fastigheterochbostad/forsaljningavprivatbostad/forbattringsutgifter.4.6d02084411db6e252fe80009228.html) (samma innehåll som sidan under *Försäljning av bostad*): under *Det här får du inte göra avdrag för* står att du inte kan få avdrag för *den del av utgiften som du har fått som rotavdrag*.
- Skatteverket, [Så fungerar rotavdraget](https://www.skatteverket.se/privat/fastigheterochbostad/rotarbeteochrutarbete/safungerarrotavdraget.4.5947400c11f47f7f9dd80004014.html): rotavdraget är skattereduktion på arbetskostnaden; fakturan visar vad du betalade efter avdrag.
- Skatteverket, [Så fungerar rutavdraget](https://www.skatteverket.se/privat/fastigheterochbostad/rotarbeteochrutarbete/safungerarrutavdraget.4.d5e04db14b6fef2c866097.html): samma modell för rut (hushållsnära tjänster).
- Inkomstskattelagen (1999:1229): **44 kap. 14 §** andra stycket — utgifter för reparation, underhåll samt om- och tillbyggnad ska inte ingå i omkostnadsbeloppet till den del skattereduktion tillgodoräknats enligt **67 kap.** (rot/rut). **45 kap.** reglerar förbättringsutgifter vid avyttring av fastigheter; **46 kap.** motsvarande för bostadsrätter.
- Skatteverkets ställningstagande dnr 131 646612-09/111 (sammanfattning på [lagen.nu](https://lagen.nu/rs/skv/131-646612-09-111)): samma princip för hushållsarbete — endast egna utgifter efter skattereduktion.

**I appen:** valfritt fält *Avdrag (rot/rut)* på utgiftsposter (öre). Sammanställning, PDF och CSV summerar `belopp − avdrag`. Tomt fält (null) = hela beloppet räknas som tidigare. Appen ger ingen skatterådgivning; användaren kontrollerar mot Skatteverket.
- Färdiga förslag på uppgifter har intervall som användaren kan ändra. För sådant som kommunen eller tillverkaren bestämmer (sotning, brandskyddskontroll, service) står "enligt kommunen" eller "enligt tillverkaren", aldrig ett lagkrav.
- Ingen adress, plats eller karta. Appen behöver inte veta var huset ligger.
- Garantivalvets kod rörs inte från Husloggens uppdrag. Delar som båda behöver lyfts till `:karna` av Cursor (BESLUTSLOGG.md 2026-10-16).

## Mätare

- Mätare har namn, enhet och valfri anteckning. Avläsningar har datum och värde (tusendelar av enheten).
- Regler för avläsningar (lägre värde, unikt datum, förbrukning) ligger i `regler/`.
- Ingår i säkerhetskopia och kan exporteras som CSV per mätare.

## Dokument

- Manualer, ritningar, besiktningsprotokoll och liknande sparas som PDF eller bild via systemets filväljare, utan extra behörigheter.
- Varje dokument har titel, valfri kategori (Manual, Ritning, Besiktning, Garanti, Avtal, Övrigt), valfri anteckning och tillagt datum.
- Filer lagras i appens privata lagring; högst 25 MB per fil. Öppnas med systemets visare via `FileProvider`.
- Ingår i säkerhetskopia (metadata och filer). Äldre säkerhetskopior utan dokument kan fortfarande importeras.

## Inte i 1.0

Flera bostäder, plan för framtida kostnader, delning, widgets.
