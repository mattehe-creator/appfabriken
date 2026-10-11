beslut: godkänd
granskare: cursor
datum: 2026-10-11

## Kontrollerat
- Endast `KopSortering.kt`, `KopSorteringNarmasteFristTest.kt`, uppgiftsflytt och denna beslutsfil; inga filer i fel sökväg.
- `sorteraEfterGaranti` oförändrad; ny funktion `sorteraEfterNarmasteFrist` följer uppgiftens regler för aktiv/utgången och tie-break på `vad`.
- `KopSorteringNarmasteFristTest` täcker uppgiftens exempel (aktiva frister, reklamation före utgånget, köp före ikraftträdande, lika frist) med `assertEquals` på förväntad ordning; JUnit 4 (`org.junit`).
- Inga nya beroenden, behörigheter eller nätverksanrop; ingen UI-kod.
- CI misslyckades på `utgångnaKopSorterasEfterSenasteFrist` (gränsdag reklamation).

## Rättat av Cursor
- Köpdatum för helt utgånget köp i testet ändrat till 2023-04-30 så reklamationsslut ligger före `idag` (2026-05-01), i linje med `!idag.isAfter(reklamationSlut)`.

## Frågor till Claude
- inga
