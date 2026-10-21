package se.tmconnect.styrelserummet.server

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import se.tmconnect.styrelserummet.modell.Forening
import se.tmconnect.styrelserummet.pdf.IngenLasbarTextException
import se.tmconnect.styrelserummet.pdf.KrypteradPdfException
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
    private val loggSkriv: (String) -> Unit = ::println,
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
                fel(id, ServerFel.Okant)
                return
            }
            var underhallsplan = ""
            var arsredovisning = ""
            var planNamn = "underhallsplan.pdf"
            filer.forEach { path ->
                val bytes = path.readBytes()
                if (pdfLasare.arKrypterad(bytes)) {
                    fel(id, ServerFel.KrypteradPdf)
                    return
                }
                if (!pdfLasare.harLasbarText(bytes)) {
                    fel(id, ServerFel.IngenLasbarText)
                    return
                }
                val text = pdfLasare.lasSomText(bytes).getOrElse {
                    when (it) {
                        is KrypteradPdfException -> fel(id, ServerFel.KrypteradPdf)
                        is IngenLasbarTextException -> fel(id, ServerFel.IngenLasbarText)
                        else -> fel(id, ServerFel.Okant)
                    }
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
                fel(id, ServerFel.Okant)
                return
            }
            val utkast = byggUtkast(
                underhallsplanText = underhallsplan,
                arsredovisningText = arsredovisning.ifBlank { underhallsplan },
                forening = Forening(namn = meta.forening, byggnadsar = null),
                tolkare = tolkare,
            )
            if (utkast.komponenter.isEmpty()) {
                // Kontrollera om alla anmärkningar är "Tolkning misslyckades" (typiskt timeout)
                val harTimeoutAnmärkningar = utkast.anmarkningar.all { it.startsWith("Tolkning misslyckades") }
                if (harTimeoutAnmärkningar) {
                    fel(id, ServerFel.AiTimeout)
                } else {
                    fel(id, ServerFel.AiOgiltigtSvar)
                }
                return
            }
            lagring.sparaUtkast(id, tillUtkastDto(utkast, meta.arsslut))
            lagring.skrivMeta(
                meta.copy(
                    status = JobbStatus.KLAR.name,
                    senastAktivMs = System.currentTimeMillis(),
                    felmeddelande = null,
                ),
            )
        } catch (_: Exception) {
            fel(id, ServerFel.Okant)
        }
    }

    private fun fel(id: String, serverFel: ServerFel) {
        val meta = lagring.lasMeta(id) ?: return
        val tidMs = System.currentTimeMillis()
        lagring.skrivMeta(
            meta.copy(
                status = JobbStatus.FEL.name,
                felmeddelande = felTillMeddelande(serverFel),
                senastAktivMs = tidMs,
            ),
        )
        loggSkriv(loggJobbFel(id, serverFel, tidMs))
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
