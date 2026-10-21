# Uppgift: Avskrivningsplanen i utskriften

uppdrag: uppdrag/claude/2026-10-21-styrelserummet-avskrivningsplan.md
försök: 1
beror-på: 2026-10-21-styrelserummet-plan-08-server-plan.md

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/UtskriftHtml.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/UtskriftPlanTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Utskrift.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivningsplan.kt

## Uppgift
1. Lägg till en parameter sist i `utskriftHtml`: `plan: List<PlanAr> = emptyList()` (importera `se.tmconnect.styrelserummet.regler.PlanAr`).
2. Om `plan` inte är tom: skriv en andra tabell efter den första `</div>` som stänger `tabellram`, inom samma `<div class="ram bred">`:

```html
<h2>Avskrivningsplan</h2>
<div class="tabellram">
  <table>
    <caption>Avskrivningsplan — utkast</caption>
    <thead><tr><th>År</th><th class="tal">Avskrivning</th><th class="tal">Bokfört värde vid årets slut</th></tr></thead>
    <tbody>...en rad per PlanAr...</tbody>
  </table>
</div>
```

Raderna: `<tr><td>ÅR</td><td class="tal">AVSKRIVNING</td><td class="tal">BOKFÖRT</td></tr>`, med beloppen formaterade med samma funktion som `Utskrift.kt` använder för kronor (`kronor(...)`).
3. Tom `plan` ger exakt samma HTML som i dag.
4. Ingen inline-stil och inget inline-skript (tjänstens CSP blockerar dem).

## Exempel
- `utskriftHtml("Brf Sol", 2025, emptyList(), listOf(PlanAr(2026, 60000, 780000)))` innehåller `Avskrivningsplan`, `<td>2026</td>` och `kronor(60000)`.
- `utskriftHtml("Brf Sol", 2025, emptyList())` innehåller inte `Avskrivningsplan`.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*UtskriftPlanTest"

## Gör inte
- Ändra andra filer (Claude kopplar in planen i rutten).
- Lägga till beroenden.
