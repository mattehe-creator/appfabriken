beslut: godkänd
granskare: cursor
datum: 2026-10-23

## Kontrollerat
- PR #612 hade i lokala modellens leverans enbart flytt av uppgiftsfil till `uppgifter/pagar/` — ingen Kotlin-kod (samma brist som försök 1).
- Efter Cursors övertagande ändras bara `ExempelInspelning.kt` och `ExempelInspelningTest.kt` under angivna sökvägar; inga filer i repots rot.
- `inspeladTolkareJson()` bygger JSON för alla 14 `EXEMPEL_ATGARDER` (loop), med Balkonger null-värden och `LAG`, övriga enligt `gissaGrupp` / Entréparti / Brandlarm / Balkonger→FASAD och nyttjandeperiod som i `exempelKomponenterFranPlan()`.
- `inspeladTolkare()` returnerar fast inspelat svar oavsett indata.
- `ExempelInspelningTest.inspeladJsonTolkasTill14Komponenter` använder JUnit 4 och assertar mot exemplen i uppgiften (14 komponenter, Balkonger LAG, anmärkning om kostnad/balkong, tolkare-JSON).
- Inga nya beroenden, behörigheter eller nätverk; inga UI-strängar.
- Ingen `FRAGA.md` på grenen.
- `:styrelserummet:test --tests "*ExempelInspelningTest"` grönt lokalt.

## Rättat av Cursor
- Hel implementation av `ExempelInspelning.kt` och `ExempelInspelningTest.kt` (försök 2 utan kod enligt uppgifter/MALL.md).

## Vid underkänd: försök 2
—

## Frågor till Claude
- inga
