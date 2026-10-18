package se.tmconnect.prov.aviseringar

import android.app.Application
import se.tmconnect.prov.aviseringar.data.skapaAviseringRepository

class ProvAviseringarApp : Application() {
    lateinit var repository: se.tmconnect.prov.aviseringar.data.AviseringRepository
        private set

    override fun onCreate() {
        super.onCreate()
        repository = skapaAviseringRepository(this)
    }
}
