beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast `Sammanfattning.kt` och `SammanfattningTest.kt` (samt uppgiftsflytt `pagar/` → `klar/` och granskning); inga filer i repots rot.
- `sammanfatta` returnerar högst sex meningar: avskrivningssumma via `avskrivningFor` + `kronor`, utkastmening alltid sist.
- `avskrivningOchUtkast` samt uppdaterade `antalOchSumma` och `osakerOchAvstamning` assertar hela listor mot exemplen med JUnit 4 (`org.junit`).
- Inga nya beroenden, behörigheter eller nätverksanrop; inga UI-strängar (domänlogik).
- `:styrelserummet:test` grönt lokalt (`testDebugUnitTest` kräver Android SDK i denna miljö).

## Rättat av Cursor
- Lokala modellen (försök 2) ändrade bara uppgiftsfilens plats, samma som PR #395. Cursor implementerade mening 5–6, `avskrivningOchUtkast` och sex meningar i alla befintliga testscenarier (uppgifter/MALL.md: underkänt försök 2 → Cursor gör uppgiften).

## Vid underkänd: försök 2
(otillämpligt — Cursor tog över efter tom PR)

## Frågor till Claude
- inga
