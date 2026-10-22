beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- PR #534 ändrade bara flytt av uppgiftsfil till `uppgifter/pagar/`; inga tillåtna källfiler från den lokala modellen (samma läge som underkänt försök 1).
- Uppgiften (försök 2) krävde `kontrolleraUnderlag` i ny `NyckeltalKontroll.kt` och JUnit 4-tester mot alla fyra exempellistor — saknades helt på grenen.
- Cursor implementerade enligt spec: fem regler i ordning, `avrundaKvot` från `Nyckeltal.kt`, inga flyttal.
- `NyckeltalKontrollTest`: standardunderlag som i `NyckeltalTest`, `org.junit`, assert av hela listor för alla exempel.
- Inga nya beroenden, behörigheter eller nätverk; meddelanden är domänlogik (ingen Compose-UI).
- Endast de två angivna källfilerna tillagda (plus granskning och uppgiftsflytt till `klar/`).

## Rättat av Cursor
- Hela implementationen i `NyckeltalKontroll.kt` och `NyckeltalKontrollTest.kt` (lokal modell levererade ingen kod).
- `:styrelserummet:testDebugUnitTest` grönt lokalt.

## Vid underkänd: försök 2
—

## Frågor till Claude
- inga
