beslut: godkänd
granskare: cursor
datum: 2026-10-09

## Kontrollerat
- Endast tillåtna kodfiler ändrade mot main: `KopDetaljSkarm.kt` och `MainActivity.kt` (ingen `KvittoHelskarm.kt`, ingen duplicerad detaljvy i `KopSkarmar.kt`).
- `KopDetaljSkarm` har `kvittoUri` och `onOppnaKvitto`; miniatyr laddas via `ContentResolver` + `BitmapFactory.decodeStream`, visas före Ändra/Ta bort, klick anropar `onOppnaKvitto`, innehållsbeskrivning `R.string.kvitto_fornhandsvisning`.
- `MainActivity` skickar `sparatKvittoUri(kop)` och `visaKvittoHelskarm(kop.id)`; helskärmsgrenen är fortfarande kommenterad för uppgift 05.
- Inga nya beroenden, behörigheter eller nätverk; inga hårdkodade UI-strängar i ändringarna.
- Angivet test `*KopDaoTest` grönt lokalt efter rättningar; befintliga appmodultester använder JUnit 4.

## Rättat av Cursor
- Kompileringsfel (smart cast på delegated `bitmap`) i `KopDetaljSkarm.kt`.
- Otillåten ändring av `KopFormularSkarm.kt` (oanvända imports) återställd till main.
- Grenens `MainActivity` saknade formulärkvitto från main; synkad med main plus uppgiftens detaljkoppling.
- Miniatyr: `fillMaxWidth()` + `height(160.dp)` i stället för `size(160.dp)`.
- Felaktig flytt av `2026-10-09-03-formular-kvitto-knappar-f2.md` i `uppgifter/` återställd mot main.

## Frågor till Claude
- inga
