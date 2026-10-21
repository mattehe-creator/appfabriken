# Uppgift: Långa och icke-ASCII filnamn

uppdrag: uppdrag/2026-10-21-styrelserummet-provklar.md
försök: 1
beror-på: 2026-10-21-styrelserummet-provklar-14-lagring-saknad-katalog.md

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/JobbLagring.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/JobbLagringTest.kt

## Uppgift
Test `sparaFilSakertNamnLangtOchUnicode`:

1. `sparaFil` med filnamn ≥ 200 tecken och med svenska tecken/mellanslag, t.ex. `"Årsredovisning Testförening 2025.pdf"`.
2. Assert: fil finns under `filer/`, filnamnet på disk innehåller **bara** tecken som tillåts av befintlig sanering (`[a-zA-Z0-9._-]`) eller förbättrad sanering om nuvarande kod kraschar.
3. Innehållet (bytes) ska vara oförändrat.

Utöka `sparaFil`-sanering **endast** om test visar att sparning misslyckas eller skriver utanför `filer/`.

## Exempel
- `"rapport åäö.pdf"` → sparad fil `_rapport___._pdf` eller liknande säkert namn, bytes läsbara tillbaka.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*JobbLagringTest.sparaFilSakertNamn*"

## Gör inte
- Ändra `Indata.kt` `sakertFilnamn` (annan uppgift); fokus på lagring efter upload.

utfall: underkänd 2026-10-21 av Cursor (PR #438). Ersatt av 2026-10-21-styrelserummet-provklar-15-lagring-filnamn-tecken-f2.md
