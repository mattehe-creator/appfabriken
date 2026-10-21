# Uppgift: Kontrast enligt WCAG och gemensam.css

uppdrag: uppdrag/2026-10-21-styrelserummet-provklar.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/regler/Kontrast.kt (ny)
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/regler/KontrastTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet-server/src/main/resources/static/gemensam.css

## Uppgift
1. `fun kontrastforhallande(fg: String, bg: String): Double` — hex `#rgb` och `#rrggbb`, WCAG 2.x relativ luminans. Avrunda till **en decimal** (half-up), t.ex. `#777777` mot `#ffffff` ≈ **4,5** (test med tolerans ±0,1).
2. Enhetstest: `#000000` mot `#ffffff` → **21,0**.
3. Test som läser `gemensam.css` från classpath (`static/gemensam.css`), plockar ut `:root`-variabler för **ljust läge** och för **mörkt läge** (`@media (prefers-color-scheme: dark)`). Kräv:
   - `--text` mot `--bg` ≥ **4,5**
   - `--dampad` mot `--bg` ≥ **4,5**
   - `--accent-text` mot `--accent` ≥ **4,5**
   - sekundär knappkant: `--accent` mot `--bg` ≥ **3,0** (samma par som kant mot sidbakgrund)
4. Saknas en variabel i CSS → testet ska **faila med tydligt meddelande** om vilken variabel som saknas (assert-message eller eget undantag).

## Exempel
- `kontrastforhallande("#000000", "#ffffff")` → `21.0`
- `kontrastforhallande("#fff", "#000")` → `21.0`
- CSS-test: om `--text` saknas i `:root` → feltext innehåller `--text`

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*KontrastTest"

## Gör inte
- Ändra `gemensam.css` eller HTML.
- Lägga till beroenden.
