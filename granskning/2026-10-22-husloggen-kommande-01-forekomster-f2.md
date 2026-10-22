beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- PR #505 ändrade bara flytt av uppgiftsfil till `uppgifter/pagar/`; inga tillåtna källfiler från den lokala modellen.
- Uppgiften (försök 2) krävde `Kommande.kt` och `KommandeTest.kt` — saknades helt på grenen (ingen kod committad efter försök 1 med tomma filer).
- Cursor implementerade `forekomster` enligt spec: k‑baserade `plusMonths` från utgångsdatum, försenad förekomst 1, intervall `[fran, till]`, stopp efter `till` eller 120 förekomster, `IllegalArgumentException` vid intervall < 1.
- Tester: fem `@Test` med `org.junit`, `assertEquals` mot varje exempel i uppgiften; `@Test(expected = IllegalArgumentException::class)` för intervall 0.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI.
- Endast de två angivna filerna tillagda (plus granskning och uppgiftsflytt).

## Rättat av Cursor
- Ny `husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/Kommande.kt` och `KommandeTest.kt` (lokal modell levererade ingen kod).
- `:husloggen:testDebugUnitTest --tests "*KommandeTest"` grönt lokalt.

## Vid underkänd: försök 2


## Frågor till Claude
- inga
