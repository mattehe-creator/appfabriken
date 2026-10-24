package se.tmconnect.husloggen.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import se.tmconnect.husloggen.MainActivity
import se.tmconnect.husloggen.R
import se.tmconnect.husloggen.data.HusDatabas
import se.tmconnect.husloggen.data.InstallningarLager

/**
 * Hemskärmswidget med de tre närmaste uppgifterna för vald bostad. Ett tryck öppnar appen.
 * Ingen behörighet, inget nätverk.
 */
class KommandeWidget : AppWidgetProvider() {
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

    companion object {
        private val RAD_ID = intArrayOf(R.id.widget_rad_1, R.id.widget_rad_2, R.id.widget_rad_3)

        /** Ritar om alla widgetar. Anropas när uppgifter ändras i appen. */
        suspend fun uppdateraAlla(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, KommandeWidget::class.java))
            if (ids.isNotEmpty()) uppdatera(context, manager, ids)
        }

        private suspend fun uppdatera(context: Context, manager: AppWidgetManager, ids: IntArray) {
            val dao = HusDatabas.hamta(context).husDao()
            val bostadId = InstallningarLager(context).valdBostadId.first() ?: dao.allaBostader().firstOrNull()?.id
            val rader = kommandeWidgetRader(dao.allaUppgifter(), bostadId, LocalDate.now(), RAD_ID.size)
            val views = byggVyer(context, rader, Locale.getDefault())
            ids.forEach { manager.updateAppWidget(it, views) }
        }

        private fun byggVyer(context: Context, rader: List<WidgetUppgiftRad>, locale: Locale): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_kommande)
            val format = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)
            RAD_ID.forEachIndexed { index, id ->
                val rad = rader.getOrNull(index)
                if (rad == null) {
                    views.setViewVisibility(id, View.GONE)
                } else {
                    val mall = if (rad.forsenad) R.string.widget_kommande_forsenad else R.string.widget_kommande_rad
                    views.setTextViewText(id, context.getString(mall, rad.namn, rad.datum.format(format)))
                    views.setTextColor(
                        id,
                        context.getColor(if (rad.forsenad) R.color.widget_fel else R.color.widget_text),
                    )
                    views.setViewVisibility(id, View.VISIBLE)
                }
            }
            views.setViewVisibility(R.id.widget_tom, if (rader.isEmpty()) View.VISIBLE else View.GONE)
            val oppna =
                PendingIntent.getActivity(
                    context,
                    0,
                    Intent(context, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
                )
            views.setOnClickPendingIntent(R.id.widget_rot, oppna)
            return views
        }
    }
}
