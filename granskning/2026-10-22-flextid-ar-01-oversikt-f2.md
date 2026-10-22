beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- Endast filerna under "Ändra bara dessa filer" ändrades (samt uppgiftsflytt pagar som lokala modellen gjorde).
- Beteendet matchar uppgiften: 12 månadsrader, arbetad/utfall per månad i året, saldo vid månadsslut inklusive dagar före året.
- Testerna använder org.junit och assertEquals mot exemplen i uppgiften.
- Inga nya beroenden, behörigheter eller nätverk.
- Ingen UI-kod; inga hårdkodade användarsträngar.
- `:flextid:testDebugUnitTest --tests "*ArsOversiktTest"` grönt lokalt efter Cursors implementation.

## Rättat av Cursor
- Försök 2 lämnade `ArsOversikt.kt` och `ArsOversiktTest.kt` tomma (samma som försök 1). Cursor implementerade båda filerna enligt uppgiften (regel efter underkänt försök 2 i uppgifter/MALL.md).

## Vid underkänd: försök 2
(otillämpligt — godkänd efter Cursors implementation)

## Frågor till Claude
- inga
