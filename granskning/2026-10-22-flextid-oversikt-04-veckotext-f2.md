beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- PR #510 ändrade bara flytt av uppgiftsfil till `uppgifter/pagar/` och skapade `VeckoText.kt` / `VeckoTextTest.kt` på rätt sökvägar — båda källfilerna var tomma (0 byte), samma fel som underkänt försök 1.
- Uppgiften (försök 2) krävde `veckoText`, `VeckoTextTexter` och ett JUnit 4-test mot hela exempelsträngen — saknades helt på grenen.
- Cursor implementerade enligt uppgiften: vecka mån–sön via `mandag`, hoppa dagar utan post, rader för arbete (hel/del), typetiketter, summa endast för hela arbetsdagar med `formateraTid` / `formateraMinuterSomTid` / `arbetadeMinuter`.
- Test: `VeckoTextTest.vecka42ExempelFranUppgiften` med `org.junit.Assert.assertEquals` mot exemplat i uppgiften.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI och inga hårdkodade användarsträngar i appkod.
- Endast de två angivna källfilerna tillagda (plus granskning och uppgiftsflytt).

## Rättat av Cursor
- Hel implementation av `VeckoText.kt` och `VeckoTextTest.kt` (lokal modell levererade tomma filer igen).
- `:flextid:testDebugUnitTest --tests "*VeckoTextTest"` grönt lokalt.

## Vid underkänd: försök 2


## Frågor till Claude
- inga
