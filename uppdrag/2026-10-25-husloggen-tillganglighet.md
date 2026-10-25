# Uppdrag: Husloggen, tillgänglighets- och strängkontroll före publicering

Läs CLAUDE.md (punkt 6 och 8), ARBETSFLODE.md, uppgifter/MALL.md och husloggen/APP.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Husloggen har många nya skärmar (flera bostäder, flytt/radera-dialog, Kommande år, widget). Innan Mattias provar och appen publiceras ska skärmarna kontrolleras mot produktreglerna i CLAUDE.md punkt 6: kontrast minst 4,5:1, tryckytor minst 48 dp, innehållsbeskrivning på ikoner, inga hårdkodade strängar, svenska och engelska.

## Uppgift

Cursor delar upp arbetet i små uppgifter för den lokala modellen (uppgifter/MALL.md, högst 3 filer och cirka 150 rader per uppgift) och gör själv det som är för tungt.

1. Sök i `husloggen/src/main` efter hårdkodade användarsynliga strängar i Compose-kod (`Text("...")`, `contentDescription = "..."`, felmeddelanden, dialogtexter) och flytta dem till `strings.xml` (sv) och `values-en/strings.xml` (en). Varje strängnyckel ska finnas i båda filerna.
2. Alla `Icon`/`Image` som är klickbara eller bär betydelse ska ha `contentDescription` från strängresurs; rent dekorativa får ha `null`.
3. Klickbara element (knappar, ikonknappar, listrader, kryssrutor) ska ha tryckyta minst 48 dp (`minimumInteractiveComponentSize` eller `heightIn(min = 48.dp)`).
4. Färger utanför temat: ersätt hårdkodade `Color(0x...)` i skärmkod med temafärger.
5. Ett enhetstest som läser båda strängfilerna och kontrollerar att nycklarna är identiska (kan göras som vanligt JVM-test).

## Klart när

- Inga kvarvarande hårdkodade synliga strängar i `husloggen/src/main` (kontrolleras med sökning, resultatet skrivs i PR-beskrivningen).
- Testet för identiska strängnycklar går igenom, och `gradle :husloggen:test` och bygget är gröna.
- Inget beteende har ändrats.

## Gör inte

- Ändra databasschemat, `:karna` eller Garantivalvet.
- Lägga till beroenden eller behörigheter.
- Ändra `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md`.
- Kräva svar från Mattias. Frågor skrivs under "Frågor till Claude" i PR-beskrivningen.
