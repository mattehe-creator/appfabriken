beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- PR #523 ändrade från den lokala modellen enbart flytt av uppgiftsfil till `uppgifter/pagar/`; inga källfiler.
- Uppgiften (försök 2) krävde `KommandeJobb`, `kommandePerManad` och utökade tester — saknades på grenen (samma som försök 1).
- Cursor implementerade enligt spec: intervall `idag`–`slut`, `forekomster` per uppgift, `forsenad` vid datum före `idag`, försenade i innevarande månad, `toSortedMap()` och sortering datum sedan namn, tomma månader utelämnade.
- Test `kommandePerManad grupperar och sorterar enligt exemplen A B C` jämför hela kartan mot uppgiftens exempel med `org.junit.Assert.assertEquals`; befintliga `forekomster`-tester oförändrade.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI eller hårdkodade strängar i appkod.
- Endast de två angivna filerna ändrades (plus granskning och uppgiftsflytt).

## Rättat av Cursor
- `KommandeJobb`, `kommandePerManad` i `Kommande.kt` och nytt JUnit 4-test för A/B/C-exemplet (lokal modell levererade ingen kod).
- `:husloggen:testDebugUnitTest --tests "*KommandeTest"` grönt lokalt.

## Vid underkänd: försök 2


## Frågor till Claude
- inga
