# Uppgift: Avskrivningsplanen i utskriften

uppdrag: uppdrag/claude/2026-10-21-styrelserummet-avskrivningsplan.md
försök: 2
beror-på: 2026-10-21-styrelserummet-plan-08-server-plan.md

## Vad som saknades
- PR #487 skapade `UtskriftPlanTest.kt` på rätt sökväg men lämnade filen **tom**. `UtskriftHtml.kt` ändrades inte alls.
- Ingen parameter `plan`, ingen HTML-tabell för avskrivningsplanen, inget JUnit 4-test mot exemplen.
- Testkommandot i uppgiften kan köras men verifierar inget.

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/UtskriftHtml.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/UtskriftPlanTest.kt (fyll i — filen finns redan tom)

## Läs som förebild (ändra inte)
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Utskrift.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivningsplan.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Formatering.kt (`kronor`)

## Uppgift
1. Lägg till en parameter sist i `utskriftHtml`: `plan: List<PlanAr> = emptyList()` (importera `se.tmconnect.styrelserummet.regler.PlanAr` och `se.tmconnect.styrelserummet.regler.kronor`).
2. Om `plan` inte är tom: efter den första `</div>` som stänger `tabellram` (komponenttabellen), men **före** den avslutande `</div>` för `<div class="ram bred">`, lägg till:

<h2>Avskrivningsplan</h2>
<div class="tabellram">
  <table>
    <caption>Avskrivningsplan — utkast</caption>
    <thead><tr><th>År</th><th class="tal">Avskrivning</th><th class="tal">Bokfört värde vid årets slut</th></tr></thead>
    <tbody>...en rad per PlanAr...</tbody>
  </table>
</div>

Raderna: `<tr><td>ÅR</td><td class="tal">${htmlEsc(kronor(planRad.avskrivning))}</td><td class="tal">${htmlEsc(kronor(planRad.bokfortVarde))}</td></tr>` (år som heltal i `<td>`, belopp via `kronor` som i `Utskrift.kt`).
3. Tom `plan` ger **byte-identisk** HTML som före ändringen (samma signatur utom default-parametern räknas inte som nytt anropssätt för befintliga anrop).
4. Ingen inline-stil och inget inline-skript.

## Exempel
- `utskriftHtml("Brf Sol", 2025, emptyList(), listOf(PlanAr(2026, 60000, 780000)))` innehåller strängen `Avskrivningsplan`, `<td>2026</td>`, och det formaterade beloppet för 60 000 kr (samma som `kronor(60000L)`, t.ex. `60\u00A0000 kr`).
- `utskriftHtml("Brf Sol", 2025, emptyList())` innehåller inte `Avskrivningsplan`.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*UtskriftPlanTest"

Skriv klassen `UtskriftPlanTest` med `org.junit.Test` och `org.junit.Assert` (inte `kotlin.test`). Minst två tester som motsvarar exemplen ovan.

## Gör inte
- Ändra andra filer (Claude kopplar in planen i rutten).
- Lägga till beroenden.
