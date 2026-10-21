package se.tmconnect.styrelserummet.server

fun utskriftHtml(forening: String, arsslut: Int, rader: List<UtskriftsRadDto>): String {
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
