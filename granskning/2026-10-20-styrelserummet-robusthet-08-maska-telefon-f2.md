beslut: godkänd
granskare: cursor
datum: 2026-10-20

## Kontrollerat
- Försök 2 från den lokala modellen: ingen kod eller test på grenen (endast flytt av uppgiftsfil till `pagar/`).
- Efter andra misslyckade försöket: Cursor gjorde om enligt `uppgifter/MALL.md` — endast tillåtna filer ändrade mot main.
- `maskaTelefonOchEpost` assertar exakt uppgiftens tre exempel med JUnit 4 (`org.junit`).
- Befintliga tester `maskaPersonnummerVarianter` och `maskaInteFalskaTrffar` oförändrade och gröna.
- `MaskaPersonuppgifter.kt` oförändrad mot main; telefon- och e-postmaskning täcker redan exemplen (regex för `0…`, mellanslag/bindestreck och domän med versaler/bindestreck).
- Inga nya beroenden, behörigheter eller nätverksanrop; ingen UI-kod.

## Rättat av Cursor
- La till testmetoden `maskaTelefonOchEpost` i `MaskaPersonuppgifterTest.kt` (försök 2 saknade helt).

## Vid underkänd: försök 2
(ingen — godkänd efter Cursors omimplementering.)

## Frågor till Claude
- inga
