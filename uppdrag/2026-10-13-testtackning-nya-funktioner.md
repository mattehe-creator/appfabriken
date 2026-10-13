# Uppdrag: tester för dagens nya funktioner (säkerhetskopia, bilder på fel, uppföljning, export)

Läs CLAUDE.md, ARBETSFLODE.md och uppgifter/MALL.md först och följ dem. Ändra inget utöver uppdraget. Uppdraget är tungt (Robolectric, Room, WorkManager, ViewModel): gör det själv direkt i en PR, dela inte upp det för den lokala modellen.

## Bakgrund

Claude har idag lagt till funktioner som bara delvis har tester (den rena logiken är testad, men inte samspelet med Room, filer och ViewModel). Appen ska snart till Google Play och inget av detta har körts på telefon. Skriv de tester som saknas, så att regressioner fångas av bygget. Hittar testerna riktiga buggar: rätta dem i koden (minsta möjliga ändring) och beskriv dem i PR-beskrivningen.

## Uppgift (Cursor, en PR)

Skriv Robolectric- och enhetstester (mönster: `data/KopRepositoryImportTest.kt`, `data/KopDaoTest.kt`, `export/SakerhetskopiaZipTest.kt`, `data/KvittoLagerNormaliseraTest.kt`) för:

1. **Säkerhetskopia med bilder på fel och reklamationsutfall.** `Sakerhetskopia.skriv` följt av `Sakerhetskopia.lasIn` och `KopRepository.importeraSakerhetskopia` med köp som har `felBilder` (riktiga filer via `KvittoLager`) och `reklamationUtfall`/`reklamationUtfallDatum`: allt kommer tillbaka på nya filnamn, filerna finns på disk, dubbletter hoppas över, en säkerhetskopia där en refererad felbild saknas ger fel utan att något importeras (ingen delvis import). En version-1-fil från före dagens fält (se `SakerhetskopiaJsonTest`) läses fortfarande.
2. **`KvittoLager`.** `sparaFelBild` (galleri-URI och kamera-URI i cache: kamerafilen tas bort efteråt, främmande URI rörs inte, trasig bild ger `null` och lämnar ingen fil kvar), `rensaCache` (bara filer äldre än gränsen tas bort), `taBort`.
3. **`KopViewModel`** (med riktig Room i minnet, `KopRepository` och `KvittoLager` via Robolectric): `laggTillFelBild` (högst 5, sjätte avvisas och filen raderas, bilden hamnar på rätt köp även om valt köp bytts under tiden, borttaget köp lämnar ingen föräldralös fil), `taBortFelBild` (två snabba borttagningar av olika bilder ger ett konsekvent resultat), `sattReklamationsUtfall`, `markeraReklamerad(null)` nollställer utfall och datum, `sparaHemforsakringCsv` (börjar med UTF-8-BOM, semikolon, summa på sista raden) och `sparaHemforsakringPdf` (resultatet börjar med `%PDF`, temporärfilen är borttagen efteråt).
4. **Säkerhetskopieringsjobbet.** `SakerhetskopiaWorker` med `WorkManager` testdriver (`androidx.work:work-testing:2.10.1`, **godkänt som enda nya beroende, bara som `testImplementation`**): inget jobb när mappen saknas, intervall respekteras, "Kopiera nu" (`tvinga`) kopierar oavsett intervall, och att två körningar efter varandra inte lämnar dubbletter. Använd `MappLagring` i minnet där SAF inte går att köra (se `AutoSakerhetskopiaTest`), eller bryt ut det som behövs minimalt.
5. **Påminnelser.** `PaminnelseWorker` skapar frister av rätt typ: `FristTyp.SVAR` (14 dagar efter reklamationen, bara utan utfall) och att ARN-påminnelsen försvinner vid löst eller anmält ärende (se `regler/Paminnelse.kt`, `regler/ReklamationsUppfoljning.kt`). Räcker det att testa den rena logiken som redan finns, gör bara det som saknas.

## Klart när

- `gradle testDebugUnitTest` och `gradle assembleDebug` är gröna för alla moduler.
- Varje punkt 1–5 ovan har minst ett test som verkligen kontrollerar beteendet (inte bara att koden körs).
- Eventuella riktiga buggar som testerna hittat är rättade och listade i PR-beskrivningen.

## Gör inte

- Ändra databasschema, behörigheter eller appens funktion och texter (utom buggrättningar enligt ovan).
- Lägga till andra beroenden än `work-testing` som `testImplementation`.
- Ändra `butik/`, `.github/workflows/`, `verktyg/`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md` eller `release.json`.

Skriv frågor under "Frågor till Claude" i PR-beskrivningen.
