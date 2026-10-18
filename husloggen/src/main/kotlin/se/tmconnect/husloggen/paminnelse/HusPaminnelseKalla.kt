package se.tmconnect.husloggen.paminnelse

import android.content.Context
import android.content.Intent
import java.time.LocalDate
import java.util.Locale
import kotlinx.coroutines.flow.first
import se.tmconnect.husloggen.MainActivity
import se.tmconnect.husloggen.R
import se.tmconnect.husloggen.data.HusDatabas
import se.tmconnect.husloggen.data.Uppgift
import se.tmconnect.husloggen.regler.PAMINNELSE_DAGAR_FORE
import se.tmconnect.karna.format.formateraDatum
import se.tmconnect.karna.paminnelse.PaminnelseFrist
import se.tmconnect.karna.paminnelse.PaminnelseInstallningarLager
import se.tmconnect.karna.paminnelse.PaminnelseKalla
import se.tmconnect.karna.paminnelse.PaminnelseNotisInnehall

class HusPaminnelseKalla(
    private val context: Context,
) : PaminnelseKalla {
    override val installningarLager: PaminnelseInstallningarLager =
        PaminnelseInstallningarLager(context, DATASTORE_NAMN)

    override val dagarInnan: Int = PAMINNELSE_DAGAR_FORE

    override suspend fun hamtaFrister(idag: LocalDate): List<PaminnelseFrist> {
        val dao = HusDatabas.hamta(context).husDao()
        val uppgifter = dao.allaUppgifterFlow().first()
        return paminnelseFristerFranUppgifter(uppgifter, idag)
    }

    override suspend fun notisForEnFrist(frist: PaminnelseFrist): PaminnelseNotisInnehall {
        val dao = HusDatabas.hamta(context).husDao()
        val uppgift = uppgiftForFrist(frist)
        val locale = Locale.getDefault()
        val datumEtikett = formateraDatum(frist.slutdatum, locale)
        val uppgiftNamn = uppgift?.namn ?: ""
        val text =
            if (dao.antalBostader() > 1) {
                val bostadNamn =
                    uppgift?.bostadId?.let { dao.hamtaBostad(it)?.namn }.orEmpty()
                context.getString(
                    R.string.paminnelse_notis_text_med_bostad,
                    bostadNamn,
                    uppgiftNamn,
                    datumEtikett,
                )
            } else {
                context.getString(
                    R.string.paminnelse_notis_text,
                    uppgiftNamn,
                    datumEtikett,
                )
            }
        return PaminnelseNotisInnehall(
            titel = context.getString(R.string.paminnelse_notis_titel),
            text = text,
            notisId = notisIdForFrist(frist),
        )
    }

    override suspend fun notisSamlad(antal: Int): PaminnelseNotisInnehall =
        PaminnelseNotisInnehall(
            titel = context.getString(R.string.paminnelse_notis_samlad_titel),
            text = context.getString(R.string.paminnelse_notis_samlad_text, antal),
            notisId = SAMMANSATT_NOTIS_ID,
        )

    override fun skapaAppIntent(frist: PaminnelseFrist?): Intent =
        Intent(context, MainActivity::class.java).apply {
            putExtra(PaminnelseWorker.EXTRA_OPPNA_ATT_GORA, true)
        }

    override fun kanalId(): String = KANAL_ID

    override fun kanalNamn(context: Context): String =
        context.getString(R.string.paminnelse_kanal_namn)

    override fun notisIkon(): Int = R.drawable.ic_launcher_foreground

    private suspend fun uppgiftForFrist(frist: PaminnelseFrist): Uppgift? {
        val uppgiftId = frist.nyckel.substringBefore(':').toLongOrNull() ?: return null
        return HusDatabas.hamta(context).husDao().hamtaUppgift(uppgiftId)
    }

    companion object {
        const val DATASTORE_NAMN = "husloggen_paminnelser"
        const val UNIKT_JOBB_NAMN = "husloggen_paminnelse_daglig"
        private const val KANAL_ID = "husloggen_paminnelser"
        private const val SAMMANSATT_NOTIS_ID = 1

        fun notisIdForFrist(frist: PaminnelseFrist): Int =
            frist.nyckel.substringBefore(':').toLongOrNull()?.toInt()
                ?: frist.nyckel.hashCode()
    }
}
