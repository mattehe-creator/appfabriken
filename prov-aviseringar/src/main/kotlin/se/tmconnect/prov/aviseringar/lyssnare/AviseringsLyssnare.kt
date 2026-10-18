package se.tmconnect.prov.aviseringar.lyssnare

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import se.tmconnect.prov.aviseringar.ProvAviseringarApp

class AviseringsLyssnare : NotificationListenerService() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return
        val repo = (application as ProvAviseringarApp).repository
        scope.launch {
            repo.behandlaAvisering(sbn)
        }
    }
}
