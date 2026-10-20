package se.tmconnect.styrelserummet.server

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import se.tmconnect.styrelserummet.modell.Forening
import se.tmconnect.styrelserummet.pdf.PdfLasare
import se.tmconnect.styrelserummet.tolking.Tolkare
import se.tmconnect.styrelserummet.underlag.byggUtkast
import kotlin.io.path.readBytes

class JobbKo(
    private val lagring: JobbLagring,
    private val tolkare: Tolkare,
    private val pdfLasare: PdfLasare = PdfLasare(),
    scope: CoroutineScope,
    private val synkron: Boolean = false,
) {
    private val kanal = Channel<String>(Channel.UNLIMITED)

    init {
        scope.launch(Dispatchers.IO) {
            for (id in kanal) {
                bearbeta(id)
            }
        }
    }

    fun lagTill(id: String) {
        if (synkron) {
            bearbeta(id)
        } else {
            kanal.trySend(id)
        }
    }

    private fun bearbeta(id: String) {
        val meta = lagring.lasMeta(id) ?: return
        lagring.skrivMeta(meta.copy(status = JobbStatus.BEARBETAR.name))
        try {
            val filer = lagring.listaFiler(id)
            if (filer.isEmpty()) {
                fel(id, "Inga filer hittades.")
                return
            }
            var underhallsplan = ""
            var arsredovisning = ""
            var planNamn = "underhallsplan.pdf"
            filer.forEach { path ->
                val bytes = path.readBytes()
                if (!pdfLasare.harLasbarText(bytes)) {
                    fel(
                        id,
                        "Filen ${path.fileName} innehåller ingen läsbar text. Ladda upp en PDF som går att markera text i.",
                    )
                    return
                }
                val text = pdfLasare.lasSomText(bytes).getOrElse {
                    fel(id, "Filen ${path.fileName} innehåller ingen läsbar text. Ladda upp en PDF som går att markera text i.")
                    return
                }
                val namn = path.fileName.toString().lowercase()
                if (namn.contains("ars") || namn.contains("redov")) {
                    arsredovisning += text + "\n"
                } else {
                    underhallsplan += text + "\n"
                    planNamn = path.fileName.toString()
                }
            }
            if (underhallsplan.isBlank() && arsredovisning.isNotBlank()) {
                underhallsplan = arsredovisning
                arsredovisning = ""
            }
            if (underhallsplan.isBlank()) {
                fel(id, "Kunde inte läsa underhållsplanen. Ladda upp minst en PDF med text.")
                return
            }
            val utkast = byggUtkast(
                underhallsplanText = underhallsplan,
                arsredovisningText = arsredovisning.ifBlank { underhallsplan },
                forening = Forening(namn = meta.forening, byggnadsar = null),
                tolkare = tolkare,
            )
            lagring.sparaUtkast(id, tillUtkastDto(utkast, meta.arsslut))
            lagring.skrivMeta(
                meta.copy(
                    status = JobbStatus.KLAR.name,
                    senastAktivMs = System.currentTimeMillis(),
                    felmeddelande = null,
                ),
            )
        } catch (_: Exception) {
            fel(id, "Bearbetningen misslyckades.")
        }
    }

    private fun fel(id: String, meddelande: String) {
        val meta = lagring.lasMeta(id) ?: return
        lagring.skrivMeta(
            meta.copy(
                status = JobbStatus.FEL.name,
                felmeddelande = meddelande,
                senastAktivMs = System.currentTimeMillis(),
            ),
        )
    }
}

fun rensaGamlaJobb(lagring: JobbLagring, nuMs: Long = System.currentTimeMillis()) {
    lagring.allaJobb().forEach { id ->
        val meta = lagring.lasMeta(id) ?: return@forEach
        if (skaRensas(meta.senastAktivMs, nuMs)) {
            lagring.raderaJobb(id)
        }
    }
}
