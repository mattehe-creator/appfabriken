package se.tmconnect.styrelserummet.regler

fun avgiftCsv(perLagenhet: List<Long>, trappa: List<Long>): String {
    val lagResult = if (perLagenhet.isNotEmpty()) {
        val header = listOf("Lägenhet", "Per år", "Per månad")
        val rows = perLagenhet.mapIndexed { index, amount ->
            val monthly = (amount * 2 + 12) / 24
            listOf((index + 1).toString(), amount.toString(), monthly.toString())
        }
        listOf(header) + rows
    } else {
        emptyList()
    }

    val trapResult = if (trappa.isNotEmpty()) {
        val header = listOf("År", "Årsavgifter")
        val rows = trappa.mapIndexed { index, amount ->
            listOf((index + 1).toString(), amount.toString())
        }
        listOf(header) + rows
    } else {
        emptyList()
    }

    val allRows = if (lagResult.isNotEmpty() && trapResult.isNotEmpty()) {
        lagResult + listOf(emptyList()) + trapResult
    } else {
        lagResult + trapResult
    }

    return if (allRows.isEmpty()) {
        ""
    } else {
        allRows.joinToString("\n") { row ->
            row.joinToString(";")
        } + "\n"
    }
}
