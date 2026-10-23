package se.tmconnect.flextid.paminnelse

import android.content.Context
import android.content.Intent
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.flow.first
import se.tmconnect.flextid.MainActivity
import se.tmconnect.flextid.R
import se.tmconnect.flextid.data.FlextidDatabas
import se.tmconnect.karna.paminnelse.PaminnelseFrist
import se.tmconnect.karna.paminnelse.PaminnelseKalla
import se.tmconnect.karna.paminnelse.PaminnelseNotisInnehall

import se.tmconnect.flextid.data.UiInstallningarLager

class FlextidPaminnelseKalla(
    private val context: Context,
    private val installningar: FlextidPaminnelseInstallningarLager,
) : PaminnelseKalla {
    override val installningarLager = installningar.karnaInstallningar

    override val dagarInnan: Int = 0

    override suspend fun hamtaFrister(idag: LocalDate): List<PaminnelseFrist> {
        val dao = FlextidDatabas.hamta(context).flextidDao()
        val installning = dao.hamtaInstallning() ?: return emptyList()
        val dag = dao.hamtaDag(idag)
        val nu = LocalTime.now()
        val stamplaUtPa = installningar.stamplaUtPa.first()
        val stamplaInPa = installningar.stamplaInPa.first()
        val stamplaInMin = installningar.stamplaInPaminnelseMinEfterMidnatt.first()
        val aftnarLediga = UiInstallningarLager(context).aftnarLediga.first()
        return paminnelseFristerForDag(
            idag = idag,
            dag = dag,
            installning = installning,
            nu = nu,
            stamplaUtPa = stamplaUtPa,
            stamplaInPa = stamplaInPa,
            stamplaInPaminnelseMinEfterMidnatt = stamplaInMin,
            aftnarLediga = aftnarLediga,
        )
    }

    override suspend fun notisForEnFrist(frist: PaminnelseFrist): PaminnelseNotisInnehall {
        val arStamplaIn = frist.nyckel.startsWith("stampla_in:")
        return if (arStamplaIn) {
            PaminnelseNotisInnehall(
                titel = context.getString(R.string.paminnelse_stampla_in_titel),
                text = context.getString(R.string.paminnelse_stampla_in_text),
                notisId = NOTIS_ID_STAMPLA_IN,
            )
        } else {
            PaminnelseNotisInnehall(
                titel = context.getString(R.string.paminnelse_stampla_ut_titel),
                text = context.getString(R.string.paminnelse_stampla_ut_text),
                notisId = NOTIS_ID_STAMPLA_UT,
            )
        }
    }

    override suspend fun notisSamlad(antal: Int): PaminnelseNotisInnehall =
        PaminnelseNotisInnehall(
            titel = context.getString(R.string.paminnelse_samlad_titel),
            text = context.getString(R.string.paminnelse_samlad_text, antal),
            notisId = NOTIS_ID_SAMMANSATT,
        )

    override fun skapaAppIntent(frist: PaminnelseFrist?): Intent =
        Intent(context, MainActivity::class.java).apply {
            putExtra(PaminnelseWorker.EXTRA_OPPNA_IDAG, true)
        }

    override fun kanalId(): String = KANAL_ID

    override fun kanalNamn(context: Context): String =
        context.getString(R.string.paminnelse_kanal_namn)

    override fun notisIkon(): Int = R.drawable.ic_launcher_foreground

    companion object {
        const val UNIKT_JOBB_NAMN = "flextid_paminnelse_daglig"
        const val KANAL_ID = "flextid_paminnelser"
        const val NOTIS_ID_STAMPLA_IN = 2
        const val NOTIS_ID_STAMPLA_UT = 3
        const val NOTIS_ID_SAMMANSATT = 1
    }
}
