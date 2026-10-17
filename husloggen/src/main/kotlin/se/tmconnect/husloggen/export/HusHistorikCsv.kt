package se.tmconnect.husloggen.export

import se.tmconnect.karna.export.csvDokument
import se.tmconnect.karna.export.csvRad
import se.tmconnect.karna.export.prisSomDecimal

fun husHistorikCsv(data: HusHistorikData, texter: HusHistorikTexter): String {
    val rader = mutableListOf<String>()
    val rubriker = buildList {
        add(texter.kolumnDatum)
        add(texter.kolumnBeskrivning)
        add(texter.kolumnTyp)
        add(texter.kolumnBelopp)
        if (data.inkluderaSkatteAvsnitt) {
            add(texter.kolumnForbattring)
        }
    }
    rader += csvRad(rubriker)
    for (block in data.arBlock) {
        for (rad in block.poster) {
            val celler = buildList {
                add(rad.datumEtikett)
                add(rad.post.vad)
                add(rad.typEtikett)
                add(rad.post.beloppOre?.let { prisSomDecimal(it) }.orEmpty())
                if (data.inkluderaSkatteAvsnitt) {
                    add(rad.forbattringEtikett.orEmpty())
                }
            }
            rader += csvRad(celler)
        }
        rader += csvRad(
            listOf(
                texter.arSummaRubrik(block.ar),
                "",
                "",
                if (block.totalOre > 0) prisSomDecimal(block.totalOre) else "",
            ),
        )
    }
    return csvDokument(rader)
}
