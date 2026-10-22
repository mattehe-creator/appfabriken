beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- PR #543 ändrade enbart de två angivna Kotlin-filerna (nya, tomma) och flytt av uppgiftsfil till `uppgifter/pagar/`; inga filer i fel sökväg.
- Uppgiften (försök 2) krävde `KommandeKostnad`, `uppskattadKostnadKommande` och JUnit 4-tester — saknades på grenen (samma som försök 1).
- Efter Cursors implementation: alla `KommandeJobb` i alla månader räknas; träff i `senasteKostnad` ökar `summaOre` och `antalMedKostnad`, annars `antalUtanKostnad`.
- Tester jämför hela `KommandeKostnad` mot uppgiftens exempel (290 000 / 3 / 1) och tom kommande-map (0, 0, 0) med `org.junit.Assert.assertEquals`.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI eller hårdkodade användarsträngar i produktionskod.
- Endast tillåtna filer plus granskning och uppgiftsflytt vid godkännande.

## Rättat av Cursor
- Hela implementationen i `KommandeKostnad.kt` och `KommandeKostnadTest.kt` (lokal modell levererade tomma filer vid försök 2).
- `:husloggen:testDebugUnitTest --tests "*KommandeKostnadTest"` grönt lokalt.

## Vid underkänd: försök 2


## Frågor till Claude
- inga
