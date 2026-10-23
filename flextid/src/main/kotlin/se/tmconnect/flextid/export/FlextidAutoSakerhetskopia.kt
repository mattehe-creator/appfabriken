package se.tmconnect.flextid.export

import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.first
import se.tmconnect.flextid.data.FlextidRepository
import se.tmconnect.flextid.data.UiInstallningarLager
import se.tmconnect.flextid.paminnelse.FlextidPaminnelseInstallningarLager

const val FLEXTID_AUTO_FILPREFIX = "flextid-auto-"
const val FLEXTID_AUTO_FILANDELSE = ".json"

suspend fun skapaFlextidSakerhetskopiaJson(
    repository: FlextidRepository,
    uiInstallningarLager: UiInstallningarLager,
    paminnelseInstallningar: FlextidPaminnelseInstallningarLager,
): String {
    val dagar = repository.hamtaAllaDagar()
    val inst = repository.hamtaInstallning()
    val stamplaInMin = paminnelseInstallningar.stamplaInPaminnelseMinEfterMidnatt.first()
    val innehall =
        SakerhetskopiaInnehall(
            version = FLEXTID_SAKERHETSKOPIA_VERSION,
            skapad = DateTimeFormatter.ISO_INSTANT.format(java.time.Instant.now()),
            installning =
                installningTillSakerhetskopia(inst, stamplaInMin).copy(
                    aftnarLediga = uiInstallningarLager.aftnarLediga.first(),
                    semesterdagarPerAr = uiInstallningarLager.semesterdagarPerAr.first(),
                    semesterarStartmanad = uiInstallningarLager.semesterarStartmanad.first(),
                ),
            dagar = dagar.map { dagTillSakerhetskopia(it) },
        )
    return skrivSakerhetskopiaJson(innehall)
}
