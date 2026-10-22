package se.tmconnect.styrelserummet.server

import se.tmconnect.styrelserummet.regler.PlanAr
import se.tmconnect.styrelserummet.regler.kronor

fun utskriftHtml(forening: String, arsslut: Int, rader: List<UtskriftsRadDto>, plan: List<PlanAr> = emptyList()): String {
    val tbody =
        rader.joinToString("") { rad ->
            val klass = if (rad.arSumma) " class=\"summa\"" else ""
            """
            <tr$klass>
              <td>${htmlEsc(rad.grupp)}</td>
              <td>${htmlEsc(rad.namn)}</td>
              <td class="tal">${htmlEsc(rad.anskaffningsvarde)}</td>
              <td class="tal">${htmlEsc(rad.nyttjandeperiod)}</td>
              <td class="tal">${htmlEsc(rad.avskrivningAret)}</td>
              <td class="tal">${htmlEsc(rad.bokfortVarde)}</td>
            </tr>
            """.trimIndent()
        }
    
    val planHtml = if (plan.isNotEmpty()) {
        val planTbody = plan.joinToString("") { planRad ->
            """
            <tr>
              <td>${planRad.ar}</td>
              <td class="tal">${htmlEsc(kronor(planRad.avskrivning))}</td>
              <td class="tal">${htmlEsc(kronor(planRad.bokfortVarde))}</td>
            </tr>
            """.trimIndent()
        }
        
        """
        <h2>Avskrivningsplan</h2>
        <div class="tabellram">
          <table>
            <caption>Avskrivningsplan — utkast</caption>
            <thead><tr><th>År</th><th class="tal">Avskrivning</th><th class="tal">Bokfört värde vid årets slut</th></tr></thead>
            <tbody>$planTbody</tbody>
          </table>
        </div>
        """.trimIndent()
    } else {
        ""
    }
    
    return """
        <!DOCTYPE html>
        <html lang="sv">
        <head>
          <meta charset="utf-8">
          <title>Utskrift — ${htmlEsc(forening)}</title>
          <link rel="stylesheet" href="/gemensam.css">
        </head>
        <body>
          <div class="ram bred">
            <div class="stampel-rad">
              <span class="stampel">UTKAST</span>
              <span class="hjalp">Beslutas av förening och revisor.</span>
            </div>
            <h1>${htmlEsc(forening.ifBlank { "Komponentlista" })}</h1>
            <p class="meta">Räkenskapsår slut $arsslut. Uppgifterna är utkast och ska kontrolleras mot handlingarna.</p>
            <div class="tabellram">
              <table>
                <caption>Komponentlista enligt K3 — utkast</caption>
                <thead>
                  <tr>
                    <th>Grupp</th>
                    <th>Komponent</th>
                    <th class="tal">Anskaffningsvärde</th>
                    <th class="tal">Nyttjandeperiod</th>
                    <th class="tal">Avskrivning året</th>
                    <th class="tal">Bokfört värde</th>
                  </tr>
                </thead>
                <tbody>
                  $tbody
                </tbody>
              </table>
            </div>
            $planHtml
          </div>
          <script src="/utskrift.js"></script>
        </body>
        </html>
        """.trimIndent()
}

private fun htmlEsc(s: String): String =
    s.replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
