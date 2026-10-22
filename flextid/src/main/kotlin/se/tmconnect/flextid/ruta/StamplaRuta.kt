package se.tmconnect.flextid.ruta

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import se.tmconnect.flextid.MainActivity
import se.tmconnect.flextid.R
import se.tmconnect.flextid.data.FlextidRepository
import se.tmconnect.flextid.data.skapaFlextidRepository
import se.tmconnect.flextid.regler.StampelLage
import se.tmconnect.flextid.regler.formateraTid
import se.tmconnect.flextid.regler.stampelLage
import se.tmconnect.karna.pro.ProStatusLager

/**
 * Snabbinställningsrutan "Stämpla": ett tryck stämplar in, nästa stämplar ut.
 * När dagen redan är klar, eller gränsen utan Pro är nådd, öppnas appen i stället.
 * Ingen ny behörighet: BIND_QUICK_SETTINGS_TILE skyddar tjänsten, appen begär den inte.
 */
class StamplaRuta : TileService() {
    private var scope: CoroutineScope? = null

    override fun onCreate() {
        super.onCreate()
        scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    }

    override fun onDestroy() {
        scope?.cancel()
        scope = null
        super.onDestroy()
    }

    override fun onStartListening() {
        super.onStartListening()
        scope?.launch { uppdatera() }
    }

    override fun onClick() {
        super.onClick()
        scope?.launch {
            val repo = skapaFlextidRepository(applicationContext)
            val oppnaAppen =
                withContext(Dispatchers.IO) {
                    val idag = LocalDate.now()
                    val arPro = ProStatusLager(applicationContext).hamtaArPro()
                    val resultat =
                        when (stampelLage(repo.hamtaDag(idag))) {
                            StampelLage.STAMPLA_IN ->
                                repo.stamplaIn(idag, LocalTime.now().withSecond(0).withNano(0), repo.hamtaInstallning().standardRastMin, arPro)
                            StampelLage.STAMPLA_UT ->
                                repo.stamplaUt(idag, LocalTime.now().withSecond(0).withNano(0), arPro)
                            StampelLage.KLAR -> null
                        }
                    resultat == null || resultat != FlextidRepository.SparaDagResult.Ok
                }
            if (oppnaAppen) oppnaApp() else uppdatera()
        }
    }

    private suspend fun uppdatera() {
        val tile = qsTile ?: return
        val dag = withContext(Dispatchers.IO) { skapaFlextidRepository(applicationContext).hamtaDag(LocalDate.now()) }
        val locale = Locale.getDefault()
        tile.icon = Icon.createWithResource(this, R.drawable.ic_ruta_stampla)
        when (stampelLage(dag)) {
            StampelLage.STAMPLA_IN -> {
                tile.state = Tile.STATE_INACTIVE
                tile.label = getString(R.string.ruta_stampla_in)
                undertext(tile, null)
            }
            StampelLage.STAMPLA_UT -> {
                tile.state = Tile.STATE_ACTIVE
                tile.label = getString(R.string.ruta_stampla_ut)
                undertext(tile, dag?.start?.let { getString(R.string.ruta_in_sedan, formateraTid(it, locale)) })
            }
            StampelLage.KLAR -> {
                tile.state = Tile.STATE_INACTIVE
                tile.label = getString(R.string.ruta_klar)
                undertext(tile, null)
            }
        }
        tile.updateTile()
    }

    private fun undertext(tile: Tile, text: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) tile.subtitle = text
    }

    @SuppressLint("StartActivityAndCollapseDeprecated")
    private fun oppnaApp() {
        val intent = Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= 34) {
            val pending = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            startActivityAndCollapse(pending)
        } else {
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        }
    }
}
