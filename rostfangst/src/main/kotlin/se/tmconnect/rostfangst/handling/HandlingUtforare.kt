package se.tmconnect.rostfangst.handling

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import java.time.Instant
import se.tmconnect.rostfangst.paminnelse.PaminnelsePlanerare
import se.tmconnect.rostfangst.regler.Forslag
import se.tmconnect.rostfangst.regler.ForslagTyp

object HandlingUtforare {
    fun kalenderIntent(forslag: Forslag): Intent {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, forslag.titel)
            forslag.plats?.let { putExtra(CalendarContract.Events.EVENT_LOCATION, it) }
            forslag.start?.let { putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, it.toEpochMilli()) }
            val slut = forslag.slut ?: forslag.start?.plusSeconds(3600)
            slut?.let { putExtra(CalendarContract.EXTRA_EVENT_END_TIME, it.toEpochMilli()) }
        }
        return intent
    }

    fun utfor(context: Context, fangstId: Long, forslag: Forslag): UtforResultat {
        return when (forslag.typ) {
            ForslagTyp.KALENDER -> UtforResultat.Kalender(kalenderIntent(forslag))
            ForslagTyp.PAMINNELSE -> {
                val start = forslag.start
                if (start == null) {
                    UtforResultat.IngenHandling
                } else {
                    PaminnelsePlanerare.planera(context, fangstId, forslag.titel, start)
                    UtforResultat.Klar
                }
            }
            ForslagTyp.ATT_GORA, ForslagTyp.ANTECKNING, ForslagTyp.FRAGA -> UtforResultat.Klar
        }
    }
}

sealed interface UtforResultat {
    data object Klar : UtforResultat
    data object IngenHandling : UtforResultat
    data class Kalender(val intent: Intent) : UtforResultat
}
