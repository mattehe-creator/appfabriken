package se.tmconnect.flextid.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import se.tmconnect.flextid.MainActivity
import se.tmconnect.flextid.R
import se.tmconnect.flextid.data.FlextidRepository
import se.tmconnect.flextid.data.UiInstallningarLager
import se.tmconnect.flextid.data.skapaFlextidRepository
import se.tmconnect.flextid.regler.StampelLage
import se.tmconnect.flextid.regler.arbetstid
import se.tmconnect.flextid.regler.avrundaTillHelMinut
import se.tmconnect.flextid.regler.formateraSaldoMinuter
import se.tmconnect.flextid.regler.formateraTid
import se.tmconnect.flextid.regler.stampelLage
import se.tmconnect.karna.pro.ProStatusLager

/**
 * Hemskärmswidget: saldo, dagens läge och en knapp som stämplar in eller ut.
 * När dagen är klar öppnar knappen appen. Ett tryck på texten öppnar alltid appen.
 * Ingen behörighet krävs.
 */
class SaldoWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        val pending = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                uppdatera(context.applicationContext, manager, ids)
            } finally {
                pending.finish()
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ATGARD_STAMPLA) {
            super.onReceive(context, intent)
            return
        }
        val pending = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                val app = context.applicationContext
                // Misslyckas stämplingen (gränsen utan Pro) ritas widgeten bara om; appen öppnas med
                // ett tryck på texten. En mottagare får inte starta en aktivitet i bakgrunden.
                stampla(app)
                uppdateraAlla(app)
            } finally {
                pending.finish()
            }
        }
    }

    companion object {
        const val ATGARD_STAMPLA = "se.tmconnect.flextid.widget.STAMPLA"

        /** Ritar om alla widgetar. Anropas när dagar eller inställningar ändras. */
        suspend fun uppdateraAlla(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, SaldoWidget::class.java))
            if (ids.isNotEmpty()) uppdatera(context, manager, ids)
        }

        /** Stämplar in eller ut. Falskt när inget sparades. */
        private suspend fun stampla(context: Context): Boolean {
            val repo = skapaFlextidRepository(context)
            val idag = LocalDate.now()
            val nu = avrundaTillHelMinut(LocalTime.now())
            val arPro = ProStatusLager(context).hamtaArPro()
            val resultat =
                when (stampelLage(repo.hamtaDag(idag))) {
                    StampelLage.STAMPLA_IN -> repo.stamplaIn(idag, nu, repo.hamtaInstallning().standardRastMin, arPro)
                    StampelLage.STAMPLA_UT -> repo.stamplaUt(idag, nu, arPro)
                    StampelLage.KLAR -> null
                }
            return resultat == FlextidRepository.SparaDagResult.Ok
        }

        private suspend fun uppdatera(context: Context, manager: AppWidgetManager, ids: IntArray) {
            val repo = skapaFlextidRepository(context)
            val ui = UiInstallningarLager(context)
            val inst = repo.hamtaInstallning()
            val at = inst.arbetstid(ui.aftnarLediga.first(), ui.normaltidUndantag.first())
            val tillstand = widgetTillstand(repo.hamtaAllaDagar(), inst.ingangssaldoMin, at, LocalDate.now())
            val views = byggVyer(context, tillstand, Locale.getDefault())
            ids.forEach { manager.updateAppWidget(it, views) }
        }

        private fun byggVyer(context: Context, tillstand: WidgetTillstand, locale: Locale): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_saldo)
            views.setTextViewText(
                R.id.widget_saldo,
                context.getString(R.string.widget_saldo, formateraSaldoMinuter(tillstand.saldoMin, locale)),
            )
            val status =
                when (tillstand.lage) {
                    StampelLage.STAMPLA_IN -> context.getString(R.string.widget_inte_instamplad)
                    StampelLage.STAMPLA_UT ->
                        context.getString(R.string.widget_in_sedan, formateraTid(tillstand.start, locale))
                    StampelLage.KLAR -> context.getString(R.string.widget_klar)
                }
            views.setTextViewText(R.id.widget_status, status)
            val knappText =
                when (tillstand.lage) {
                    StampelLage.STAMPLA_IN -> context.getString(R.string.ruta_stampla_in)
                    StampelLage.STAMPLA_UT -> context.getString(R.string.ruta_stampla_ut)
                    StampelLage.KLAR -> context.getString(R.string.widget_oppna)
                }
            views.setTextViewText(R.id.widget_knapp, knappText)
            views.setContentDescription(R.id.widget_knapp, knappText)

            val stamplaIntent =
                PendingIntent.getBroadcast(
                    context,
                    0,
                    Intent(context, SaldoWidget::class.java).setAction(ATGARD_STAMPLA),
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
                )
            val oppnaIntent =
                PendingIntent.getActivity(
                    context,
                    1,
                    Intent(context, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
                )
            views.setOnClickPendingIntent(
                R.id.widget_knapp,
                if (tillstand.lage == StampelLage.KLAR) oppnaIntent else stamplaIntent,
            )
            views.setOnClickPendingIntent(R.id.widget_text_omrade, oppnaIntent)
            return views
        }
    }
}
