# Uppgift: Långa och icke-ASCII filnamn

uppdrag: uppdrag/2026-10-21-styrelserummet-provklar.md
försök: 2
beror-på: 2026-10-21-styrelserummet-provklar-14-lagring-saknad-katalog.md

## Vad som saknades
- **Radera inte** befintliga tester i `JobbLagringTest.kt`. Lägg bara till testet `sparaFilSakertNamnLangtOchUnicode` (och ev. `sparaFilSakertNamn` för kort unicode-namn om du vill täcka exemplet) — alla tester som fanns på main ska finnas kvar oförändrade.
- Ta bort kommentaren `// ... rest of the file remains unchanged`; den är felaktig.
- Filnamnet i testet ska vara **minst 200 tecken** (upprepa t.ex. `"a"` eller ett ord) **och** innehålla svenska tecken/mellanslag, t.ex. bas `"Årsredovisning Testförening 2025.pdf"` utökad till ≥ 200 tecken.
- Assert: filnamnet på disk får **bara** innehålla tecken som tillåts av saneringen `[a-zA-Z0-9._-]` (regex `^[a-zA-Z0-9._-]+$`). Svenska bokstäver ska bli `_`, som i exemplet `"rapport åäö.pdf"` → namn med enbart `[a-zA-Z0-9._-]`.
- Utöka `sparaFil`-sanering **endast** om nuvarande kod kraschar eller skriver utanför `filer/` — t.ex. `substringAfterLast('/')` efter `\`/`-normalisering och `.take(200)` för långa namn är OK om testet kräver det. **Lägg inte till** å/ä/ö i tillåten teckenmängd.
- Verifiera bytes oförändrade med `assertArrayEquals(bytes, readBytes)` (eller likvärdigt), inte bara `contentToString()`.

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/JobbLagring.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/JobbLagringTest.kt

## Uppgift
Test `sparaFilSakertNamnLangtOchUnicode`:

1. `sparaFil` med filnamn ≥ 200 tecken och med svenska tecken/mellanslag, t.ex. bas `"Årsredovisning Testförening 2025.pdf"` utökad till ≥ 200 tecken.
2. Assert: fil finns under `filer/`, filnamnet på disk innehåller **bara** tecken `[a-zA-Z0-9._-]`.
3. Innehållet (bytes) ska vara oförändrat.

Utöka `sparaFil`-sanering **endast** om test visar att sparning misslyckas eller skriver utanför `filer/`.

## Exempel
- `"rapport åäö.pdf"` → sparad fil `_rapport___._pdf` eller liknande säkert namn, bytes läsbara tillbaka.
- Långt namn (≥ 200 tecken) → sparat namn trunkeras till högst 200 tecken **efter** sanering, fortfarande enbart `[a-zA-Z0-9._-]`.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*JobbLagringTest.sparaFilSakertNamn*"

Alla befintliga tester i `JobbLagringTest` ska fortsätta gå igenom:
gradle :styrelserummet-server:test --tests "*JobbLagringTest*"

## Gör inte
- Ändra `Indata.kt` `sakertFilnamn` (annan uppgift); fokus på lagring efter upload.
- Ta bort eller ändra befintliga tester i `JobbLagringTest.kt`.
